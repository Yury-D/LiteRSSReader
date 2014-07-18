package testproject.ambal.literssreader.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import testproject.ambal.literssreader.R;

/**
 * Created by Ambal on 18.07.14.
 */

public class DataUpdater extends AsyncTask<String, String, String> {
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
    protected String doInBackground(String... params) {
        mDownloader = new Downloader(params[0]);
        return mDownloader.download();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }


    }
}



