package testproject.ambal.literssreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import testproject.ambal.literssreader.ORM.HelperFactory;
import testproject.ambal.literssreader.ORM.entities.Channel;
import testproject.ambal.literssreader.service.DataUpdater;


public class StartScreenActivity extends SherlockActivity {

    private  EditText mText;
    private SharedPreferences sPref;
    final String SAVED_TEXT = "saved_text";
    String newFeed;

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
        List<Channel> myChannels = Collections.EMPTY_LIST;

        try {
            myChennelDao = HelperFactory.getHelper().getChannelDao();
            myChannels = myChennelDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //создаем по кнопке на канал
        for (Iterator<Channel> mIterator = myChannels.iterator(); mIterator.hasNext();){
            final Channel channel = mIterator.next();
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
            case R.id.menuItem_load: {
                //item.setActionView(R.layout.progressbar);
                DataUpdater mDataUpdater = new DataUpdater(this);
                mDataUpdater.execute("http://news.tut.by/rss/auto/autobusiness.rss");
                //item.collapseActionView();
                //item.setActionView(null);
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
        if (!URLUtil.isValidUrl(newFeed)){
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


}
