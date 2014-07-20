package testproject.ambal.literssreader.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import testproject.ambal.literssreader.ORM.HelperFactory;
import testproject.ambal.literssreader.ORM.entities.Channel;
import testproject.ambal.literssreader.ORM.entities.Item;
import testproject.ambal.literssreader.R;
import testproject.ambal.literssreader.StartScreenActivity;

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
        this.dialog.setCancelable(true);
        this.dialog.show();
    }

    @Override
    protected List<Channel> doInBackground(String... urls) {
        List<Channel> result = new ArrayList<Channel>();
        Parser parser = new Parser();
        for (String url: urls) {
            mDownloader = new Downloader(url);
            Channel downloadedChannel = parser.parse(mDownloader.download());
            try {
                downloadedChannel.setUrl(new URL(url));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            //проверяем, есть ли такой фид в базе
            List<Channel> sameChannels = Collections.EMPTY_LIST;
            List<Channel> samePubDates = Collections.EMPTY_LIST;
            try {
                sameChannels = HelperFactory.getHelper().getChannelDao().queryForEq("title", downloadedChannel.getTitle());
                samePubDates = HelperFactory.getHelper().getChannelDao().queryForEq("lastBuildDate", downloadedChannel.getLastBuildDate());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            result.add(downloadedChannel);

            boolean sameChanelDetected = sameChannels.size() > 0; //если >0, то в базе уже есть фиф с таким заголовком
            boolean laterPubDateDetected = !(samePubDates.size() > 0);//есть >0, то дата публикации одинакова

            //добавляем в базу, если такого фида нет, либо если фид есть, и дата публикации более поздняя
            if (!sameChanelDetected || (sameChanelDetected & (laterPubDateDetected))) {
                //сохраняем в базу данные о фиде
                try {
                    HelperFactory.getHelper().getChannelDao().create(downloadedChannel);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //сохраняем в базу айтемы фида - заголовки новостей
                Collection<Item> items = downloadedChannel.getItems();
                for (Iterator<Item> iterator = items.iterator(); iterator.hasNext(); ) {
                    try {
                        HelperFactory.getHelper().getItemDao().create(iterator.next());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
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


