package testproject.ambal.literssreader;

/**
 * Created by Ambal on 27.07.2014.
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import testproject.ambal.literssreader.ORM.HelperFactory;
import testproject.ambal.literssreader.ORM.entities.Channel;
import testproject.ambal.literssreader.ORM.entities.Item;
import testproject.ambal.literssreader.service.DataUpdater;


public class PrefActivity extends SherlockPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String LOG_TAG = "mylogs";
    ArrayList<String> currentChannelsUrlsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        prefs.registerOnSharedPreferenceChangeListener(this);


        //чтобы правильно расставить галочки, прочитаем из бд список каналов
        HelperFactory.setHelper(getApplicationContext());
        List<Channel> myChannels = Collections.EMPTY_LIST;
        try {
            myChannels = HelperFactory.getHelper().getChannelDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        currentChannelsUrlsList = new ArrayList<String>();
        for (Channel mChannel : myChannels) {
            currentChannelsUrlsList.add(mChannel.getUrl());
        }


        SharedPreferences.Editor editor = prefs.edit();
        Log.e(LOG_TAG, String.valueOf(prefs.getAll().size()));
        for (String url : currentChannelsUrlsList) {
            if (prefs.contains(url)) {
                editor.putBoolean(url, true).commit();
            }
        }
        //editor.commit();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.e(LOG_TAG, s);

        if (sharedPreferences.getBoolean(s,false)){
            if (!currentChannelsUrlsList.contains(s)){
                DataUpdater updater = new DataUpdater(getBaseContext(), new Handler(), false);
                updater.execute(s);
                Log.e(LOG_TAG, "added");
            }
        } else {
            //если убрали галочку, то найти канал с таким url и удалить его
            if (currentChannelsUrlsList.contains(s)){
                List<Channel> channelsForRemove;
                try {
                    channelsForRemove = HelperFactory.getHelper().getChannelDao().queryForEq("url", s);
                    //удаляем item-ы
                    for (Channel channel: channelsForRemove){
                        List<Item> itemsForDelete = HelperFactory.getHelper().getItemDao().queryForEq("channel_id",
                                channel.getId());
                        HelperFactory.getHelper().getItemDao().delete(itemsForDelete);
                    }
                    // и сам канал
                    HelperFactory.getHelper().getChannelDao().delete(channelsForRemove);
                    Log.e(LOG_TAG, "deleted");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.e(LOG_TAG, "on destroy");
        super.onDestroy();
    }
}

