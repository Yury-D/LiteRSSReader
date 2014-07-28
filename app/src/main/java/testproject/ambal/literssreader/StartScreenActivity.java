package testproject.ambal.literssreader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
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
import testproject.ambal.literssreader.service.DataUpdater;



public class StartScreenActivity extends SherlockActivity {
    private List<Channel> myChannels;

    //private final String SAVED_TEXT = "saved_text";
    private static final String LOG_TAG = "mylogs";
    private Handler mHandler;
    private LinearLayout buttonlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_start_screen);
        buttonlayout = (LinearLayout)findViewById(R.id.button_keeper);


        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        final Map<String, Integer> result = (Map<String, Integer>) msg.obj;
                        String mapToString = result.toString();
                        Toast.makeText(getBaseContext(),  mapToString
                                .substring(1, mapToString.length() - 1), Toast.LENGTH_SHORT).show();
                        // если все 0, проверить инет
                        StringBuffer sb = new StringBuffer();
                        for (Integer next : result.values()) {
                            sb.append(next);
                        }
                        if (sb.toString().equals("00")){
                            Toast.makeText(getBaseContext(), getString(R.string.check_conn), Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        };

        // loadLastInput();


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
                .writeDebugLogs() //TODO Remove for release app
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        DataUpdater mDataUpdater = new DataUpdater(this, mHandler, true);
        ArrayList<String> currentChannelsUrlsList = new ArrayList<String>();
        for (Channel mChannel : myChannels) {
            currentChannelsUrlsList.add(mChannel.getUrl());
        }
        String[] urls = new String[myChannels.size()];
        urls = currentChannelsUrlsList.toArray(urls);

        switch (item.getItemId()) {
            case R.id.menuItem_update: {
                mDataUpdater.execute(urls);
                break;
            }
            case R.id.menuItem_settings: {
                Intent mIntent = new Intent(this, PrefActivity.class);
                startActivity(mIntent);
                break;
            }
            case R.id.menuItem_addFeed: {
                Intent mIntent = new Intent(this, AddActivity.class);
                startActivity(mIntent);
                break;
            }
            default:
                break;
        }
        return true;
    }

    //для удобства сохраняем последний введенный URL
/*    void saveLastInput() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TEXT, newFeed);
        ed.commit();
    }

    void loadLastInput() {
        sPref = getPreferences(MODE_PRIVATE);
        String savedText = sPref.getString(SAVED_TEXT, getString(R.string.defaultUrl));
        mText.setText(savedText);
    }*/

    @Override
    public void onResume() {
        super.onResume();
        checkForCrashes();
        checkForUpdates();


        //создаем кнопки каналов, кажд. раз при возврате на экран
        buttonlayout.removeAllViews();
        //читаем список имеющихся каналов
        HelperFactory.setHelper(getApplicationContext());
        myChannels = Collections.EMPTY_LIST;
        try {
            Dao<Channel, Integer> myChennelDao = HelperFactory.getHelper().getChannelDao();
            myChannels = myChennelDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //создаем по кнопке на канал
        for (final Channel channel : myChannels) {
            Button myButton = new Button(this);
            myButton.setText(channel.getTitle());
            myButton.setBackgroundResource(R.drawable.custom_btn_blue);

            // создаем обработчик нажатия
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), DetailFeedActivity.class);
                    //передаем id фида в intent
                    intent.putExtra("ChannelId", channel.getId());
                    startActivity(intent);
                }
            };

            myButton.setOnClickListener(onClickListener);
            buttonlayout.addView(myButton);
        }

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



}
