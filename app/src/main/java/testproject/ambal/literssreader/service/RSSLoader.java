package testproject.ambal.literssreader.service;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
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

import static testproject.ambal.literssreader.service.Downloader.*;

/**
 * Created by Ambal on 30.07.14.
 */
public class RSSLoader extends AsyncTaskLoader<Map<String, String>> {

    Context mContext;
    String[] urls;
    private static final String LOG_TAG = "mylogs";


    public RSSLoader(Context context, Bundle args) {
        super(context);
        Log.e(LOG_TAG, hashCode() + " constructor RSSLoader");
        mContext = context;
        if (args != null) {
            urls = args.getStringArray("urlList");
        } else {
            Log.e(LOG_TAG, "no valid urls for loader");
        }
    }

    @Override
    public Map<String, String> loadInBackground() {
        Log.e(LOG_TAG, hashCode() + " loadInBackground start");

        int createCounter = 0;
        int updateCounter = 0;
        Map<String, String> resultStatus = new HashMap<String, String>();
        Map<String, String> downloadedMap = new HashMap<String, String>();
        for (String url : urls) {
            Downloader mDownloader = new Downloader();

            downloadedMap = mDownloader.download(url);
            String stringDownloadedChannel = downloadedMap.get(RESULT);

            //проверяем, что скачалось хоть что-то
            if (stringDownloadedChannel != null && (stringDownloadedChannel.length() != 0)) {
                Parser parser = new Parser();
                Channel downloadedChannel = parser.parse(stringDownloadedChannel);
                // что это что-то удалось распарсить
                if (downloadedChannel != null) {
                    downloadedChannel.setUrl(url);
                    //проверяем, есть ли такой фид в базе
                    List<Channel> sameChannels = Collections.emptyList();
                    List<Channel> samePubDates = Collections.emptyList();
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
                                createCounter--;
                                oldId = HelperFactory.getHelper().getChannelDao().extractId(sameChannels.get(0));
                                List<Item> itemsForDelete = HelperFactory.getHelper().getItemDao().queryForEq("channel_id",
                                        sameChannels.get(0).getId());
                                HelperFactory.getHelper().getItemDao().delete(itemsForDelete);
                                HelperFactory.getHelper().getChannelDao().deleteById(sameChannels.get(0).getId());
                                //чтобы id не менялись при апдейте, сохраняем старый и здесь применяем его к новому
                                downloadedChannel.setId(oldId);
                            }

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
        }
        resultStatus.putAll(downloadedMap);
        downloadedMap = null;
        //Log.e(LOG_TAG, downloadedMap.toString());
        resultStatus.put(CREATED, String.valueOf(createCounter));
        resultStatus.put(UPDATED, String.valueOf(updateCounter));
        return resultStatus;
    }
}