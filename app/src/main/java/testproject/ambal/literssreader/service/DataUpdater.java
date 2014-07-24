package testproject.ambal.literssreader.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
    private static final String LOG_TAG = "mylogs";

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
        for (String url : urls) {
            mDownloader = new Downloader(url);
            Parser parser = new Parser();

            String stringDownloadedChannel = mDownloader.download();

            //если что нибудь скачалось, пытаемся парсить, иначе вернем пустой List
            save:
            if (null != stringDownloadedChannel) {
                Channel downloadedChannel = null;
                try {
                    downloadedChannel = parser.parse(stringDownloadedChannel);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "incorrect downloaded data, not rss link?");
                    break save;
                }
                downloadedChannel.setUrl(url);
                result.add(downloadedChannel);
                //проверяем, есть ли такой фид в базе
                List<Channel> sameChannels = Collections.EMPTY_LIST;
                List<Channel> samePubDates = Collections.EMPTY_LIST;
                try {
                    sameChannels = HelperFactory.getHelper().getChannelDao().queryForEq("title",
                            downloadedChannel.getTitle());
                    samePubDates = HelperFactory.getHelper().getChannelDao().queryForEq("lastBuildDate",
                            downloadedChannel.getLastBuildDate());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                boolean sameChanelDetected = sameChannels.size() > 0; //если >0, то в базе уже есть фид с таким заголовком
                boolean laterPubDateDetected = !(samePubDates.size() > 0);//если >0, то дата публикации одинакова
                //добавляем в базу, если такого фида нет, либо если фид есть, и дата публикации более поздняя
                if (!sameChanelDetected || (sameChanelDetected & (laterPubDateDetected))) {
                    //сохраняем в базу данные о фиде
                    try {
                        //предварительно удалив старый фид с таким же заголовком и все его айтемы
                        int oldId = 0;
                        if (sameChanelDetected) {
                            oldId = HelperFactory.getHelper().getChannelDao().extractId(sameChannels.get(0));
                            List<Item> itemsForDelete = HelperFactory.getHelper().getItemDao().queryForEq("channel_id",
                                    sameChannels.get(0).getId());
                            HelperFactory.getHelper().getItemDao().delete(itemsForDelete);
                            HelperFactory.getHelper().getChannelDao().deleteById(sameChannels.get(0).getId());
                        }
                        //чтобы id не менялись при апдейте, сохраняем старый и здесь применяем его к новому
                        downloadedChannel.setId(oldId);
                        HelperFactory.getHelper().getChannelDao().create(downloadedChannel);
                        //HelperFactory.getHelper().getChannelDao().updateId(downloadedChannel, oldId);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    //сохраняем в базу айтемы фида - заголовки новостей
                    Collection<Item> items = downloadedChannel.getItems();
                    for (Item item : items) {
                        try {
                            HelperFactory.getHelper().getItemDao().create(item);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(List<Channel> result) {
        super.onPostExecute(result);
        if (result.isEmpty()) {
            Toast.makeText(mContext, "Feed not found", Toast.LENGTH_LONG).show();
        }
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
    }
}



