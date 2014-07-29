package testproject.ambal.literssreader.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import testproject.ambal.literssreader.ORM.HelperFactory;
import testproject.ambal.literssreader.ORM.entities.Channel;
import testproject.ambal.literssreader.ORM.entities.Item;
import testproject.ambal.literssreader.R;

/**
 * Created by Ambal on 18.07.14.
 */

public class DataUpdater extends AsyncTask<String, Void, Map<String, Integer>> {
    private Context mContext;
    private Handler mHandler;
    private ProgressDialog dialog;
    private Map<String, Integer> resultStatus;
    private int createCounter = 0;
    private int updateCounter = 0;
    private static final String LOG_TAG = "mylogs";

    //данный конструктор нужен чтобы передать контекст в AsyncTask
    public DataUpdater(Context context, Handler handler, boolean showProgressDialog) {
        mContext = context;
        mHandler = handler;
        if (showProgressDialog){
            dialog = new ProgressDialog(mContext);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (null!=dialog){
            dialog.setMessage(mContext.getString(R.string.progress_dialog_message));
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
        }
    }

    @Override
    protected Map<String, Integer> doInBackground(String... urls) {
        resultStatus = new HashMap<String, Integer>(2);
        for (String url : urls) {
            Downloader mDownloader = new Downloader();
            Parser parser = new Parser();
            String stringDownloadedChannel = mDownloader.download(url);

            //если что нибудь скачалось, пытаемся парсить
            save:
            if (stringDownloadedChannel.length()!=0) {
                Channel downloadedChannel = null;
                try {
                    downloadedChannel = parser.parse(stringDownloadedChannel);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, mContext.getString(R.string.error_message));
                    break save;
                }
                downloadedChannel.setUrl(url);
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
                            updateCounter++;

                            oldId = HelperFactory.getHelper().getChannelDao().extractId(sameChannels.get(0));
                            List<Item> itemsForDelete = HelperFactory.getHelper().getItemDao().queryForEq("channel_id",
                                    sameChannels.get(0).getId());
                            HelperFactory.getHelper().getItemDao().delete(itemsForDelete);
                            HelperFactory.getHelper().getChannelDao().deleteById(sameChannels.get(0).getId());
                        }

                        //чтобы id не менялись при апдейте, сохраняем старый и здесь применяем его к новому
                        downloadedChannel.setId(oldId);
                        HelperFactory.getHelper().getChannelDao().create(downloadedChannel);
                        createCounter++;

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
        resultStatus.put("created", createCounter);
        resultStatus.put("updated", updateCounter);
        return resultStatus;
    }

    @Override
    protected void onPostExecute(Map<String, Integer> resultStatus) {
        super.onPostExecute(resultStatus);

        Message msg = mHandler.obtainMessage(0, resultStatus);
        mHandler.sendMessage(msg);
        if (null!=dialog){
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
        }
    }
}



