package testproject.ambal.literssreader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.dao.Dao;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import testproject.ambal.literssreader.ORM.HelperFactory;
import testproject.ambal.literssreader.ORM.entities.Channel;
import testproject.ambal.literssreader.service.RSSLoader;
import static testproject.ambal.literssreader.service.Downloader.*;

public class ActivityStartScreen extends SherlockFragmentActivity implements LoaderManager.LoaderCallbacks<Map<String, String>> {
    private static final int LOADER_ID = 1;

    private List<Channel> myChannels;
    private static final String LOG_TAG = "mylogs";

    Loader<Map<String, String>> loader = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start_screen);

        initImageLoader(getApplicationContext());
    }


    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(5 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                //.writeDebugLogs() //TODO Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.start_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_for_list clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        ArrayList<String> currentChannelsUrlsList = new ArrayList<String>();
        for (Channel mChannel : myChannels) {
            currentChannelsUrlsList.add(mChannel.getUrl());
        }
        String[] urls = new String[myChannels.size()];
        urls = currentChannelsUrlsList.toArray(urls);

        switch (item.getItemId()) {
            case R.id.menuItem_update: {
                Bundle bundle = new Bundle();
                bundle.putStringArray("urlList", urls);
                getSupportLoaderManager().initLoader(LOADER_ID, bundle, this);
                loader = getSupportLoaderManager().getLoader(LOADER_ID);
                loader.forceLoad();
                break;
            }
            case R.id.menuItem_settings: {
                Intent mIntent = new Intent(this, ActivityPreferences.class);
                startActivity(mIntent);
                break;
            }
            case R.id.menuItem_addFeed: {
                Intent mIntent = new Intent(this, ActivityAddFeed.class);
                startActivity(mIntent);
                break;
            }
            default:
                break;
        }
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
        checkForCrashes();
        checkForUpdates();


        List<Button> mButtons = getButtonListFromDB();
        ButtonAdapter mAdapter = new ButtonAdapter((ArrayList<Button>) mButtons);
        ListView view = (ListView) findViewById(R.id.listView);
        view.setAdapter(mAdapter);

    }

    private List<Button> getButtonListFromDB(){
        //создаем кнопки каналов, кажд. раз при возврате на экран
        //читаем список имеющихся каналов
        HelperFactory.setHelper(getApplicationContext());
        myChannels = Collections.emptyList();
        try {
            Dao<Channel, Integer> myChannelDao = HelperFactory.getHelper().getChannelDao();
            myChannels = myChannelDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Button> mButtons = new ArrayList<Button>();
        //создаем по кнопке на канал
        for (final Channel channel : myChannels) {
            Button myButton = new Button(this);
            myButton.setText(channel.getTitle());
            myButton.setBackgroundResource(R.drawable.custom_btn_blue);

            // создаем обработчик нажатия
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ActivityDetailFeed.class);
                    //передаем id фида в intent
                    intent.putExtra("ChannelId", channel.getId());
                    startActivity(intent);
                }
            };
            myButton.setOnClickListener(onClickListener);
            mButtons.add(myButton);
        }
        return mButtons;
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void checkForCrashes() {
        CrashManager.register(this, "e9beebfb7cff09c7dcbec9900b91f5a2");
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this, "e9beebfb7cff09c7dcbec9900b91f5a2");
    }


    @Override
    public Loader<Map<String, String>> onCreateLoader(int i, Bundle bundle) {
        Log.e(LOG_TAG, " onCreateLoader");

        if (i == LOADER_ID) {
            loader = new RSSLoader(this, bundle);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Map<String, String>> loader, Map<String, String> data) {
        Log.e(LOG_TAG, loader.hashCode() + " onLoadFinished for loader ");
        if (data!=null) {
            if (!data.get(STATUS_MSG).equals(STATUS_SUCCESS)) {
                Toast.makeText(getBaseContext(), String.valueOf(data.get(STATUS_MSG)), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), (String.valueOf(data.get(STATUS_MSG)))
                        .concat(String.valueOf(", updated - "))
                        .concat(String.valueOf(data.get(UPDATED)))
                        //.concat(", created - ")
                        //.concat(String.valueOf(data.get(CREATED)))
                        , Toast.LENGTH_SHORT).show();
            }
            //Log.e(LOG_TAG, String.valueOf(data.get(STATUS_CODE)));
            //Log.e(LOG_TAG, String.valueOf(data.get(STATUS_MSG)));
            //Log.e(LOG_TAG, String.valueOf(data.get(UPDATED)));
            //Log.e(LOG_TAG, String.valueOf(data.get(CREATED)));
        } else {
            Log.e(LOG_TAG, "data null");
        }

    }


    @Override
    public void onLoaderReset(Loader<Map<String, String>> objectLoader) {
        Log.e(LOG_TAG, loader.hashCode() + " onLoaderReset for loader");
    }











    //----------------------------------------------------------------------------------
    private class ButtonAdapter extends ArrayAdapter<Button> {
        private ArrayList<Button> buttons;

        public ButtonAdapter(ArrayList<Button> buttons) {
            super(getBaseContext(), 0 , buttons);
            this.buttons = buttons;
        }
        @Override
        public int getCount() { return buttons.size(); }
        @Override
        public Button getItem(int position) { return buttons.get(position); }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = buttons.get(position);
            }
            return convertView;
        }
    }




























}
