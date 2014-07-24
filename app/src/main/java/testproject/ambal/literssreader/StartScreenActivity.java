package testproject.ambal.literssreader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import testproject.ambal.literssreader.ORM.HelperFactory;
import testproject.ambal.literssreader.ORM.entities.Channel;
import testproject.ambal.literssreader.service.DataUpdater;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;



public class StartScreenActivity extends SherlockActivity {
    private List<Channel> myChannels;
    private  EditText mText;
    private SharedPreferences sPref;
    private final String SAVED_TEXT = "saved_text";
    private String newFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        LinearLayout buttonlayout = (LinearLayout)findViewById(R.id.button_keeper);
        mText = (EditText) findViewById(R.id.editText);

        loadLastInput();
        //читаем список имеющихся каналов
        HelperFactory.setHelper(getApplicationContext());
        Dao<Channel, Integer> myChennelDao;
        myChannels = Collections.EMPTY_LIST;
        try {
            myChennelDao = HelperFactory.getHelper().getChannelDao();
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
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
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
        switch (item.getItemId()) {
            case R.id.menuItem_update: {
                DataUpdater mDataUpdater = new DataUpdater(this);
                List<String> currentChannelsUrls = new ArrayList<String>();
                for (Channel mChannel: myChannels){
                    currentChannelsUrls.add(mChannel.getUrl());
                }
                String[] urls = new String[myChannels.size()];
                urls = currentChannelsUrls.toArray(urls);
                Toast.makeText(this, String.valueOf(urls.length), Toast.LENGTH_SHORT).show();
                mDataUpdater.execute(urls);
                break;
            }
            case R.id.menuItem_settings: {
                Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show();
                break;
            }
            default:
                break;
        }
        return true;
    }

    //"add Feed" button listener
    public void addFeed(View v) {
        newFeed = mText.getText().toString();
        saveLastInput();

        if (!Patterns.WEB_URL.matcher(newFeed).matches()) {
            Toast.makeText(this, "Incorrect input URL", Toast.LENGTH_SHORT).show();
            return;
        }
        DataUpdater mUpdater = new DataUpdater(this);
        mUpdater.execute(newFeed);
    }

    //для удобства сохраняем последний введенный URL
    void saveLastInput() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TEXT, newFeed);
        ed.commit();
    }

    void loadLastInput() {
        sPref = getPreferences(MODE_PRIVATE);
        String savedText = sPref.getString(SAVED_TEXT, getString(R.string.defaultUrl));
        mText.setText(savedText);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForCrashes();
        checkForUpdates();
    }

    private void checkForCrashes() {
        CrashManager.register(this, "e9beebfb7cff09c7dcbec9900b91f5a2");
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this, "e9beebfb7cff09c7dcbec9900b91f5a2");
    }



}
