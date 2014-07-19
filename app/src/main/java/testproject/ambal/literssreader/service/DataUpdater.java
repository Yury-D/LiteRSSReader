package testproject.ambal.literssreader.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import testproject.ambal.literssreader.ORM.HelperFactory;
import testproject.ambal.literssreader.ORM.entities.Channel;
import testproject.ambal.literssreader.R;

/**
 * Created by Ambal on 18.07.14.
 */

public class DataUpdater extends AsyncTask<String, Void, List<Channel>> {
    private Context mContext;
    private ProgressDialog dialog;
    private Downloader mDownloader;

    //данный конструктор нужен чтобы передать контекст в AsyncTask
    public DataUpdater(Context context) {
        mContext = context;
        dialog = new ProgressDialog(mContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setMessage(mContext.getString(R.string.progress_dialog_message));
        this.dialog.setIndeterminate(true);
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    @Override
    protected List<Channel> doInBackground(String... params) {
        List<Channel> result = new ArrayList<Channel>();
        Parser parser = new Parser();
        for (String url: params) {
            mDownloader = new Downloader(url);
            Channel downloadedChannel = parser.parse(mDownloader.download());
            result.add(downloadedChannel);
            try {
                HelperFactory.getHelper().getChannelDao().create(downloadedChannel);
            } catch (SQLException e) {
                e.printStackTrace();
                //TODO: убрать потом
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(List<Channel> result) {
        super.onPostExecute(result);
        //Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }


    }
}



