package testproject.ambal.literssreader;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

import java.util.Map;

import testproject.ambal.literssreader.service.DataUpdater;

import static testproject.ambal.literssreader.service.Downloader.CREATED;
import static testproject.ambal.literssreader.service.Downloader.STATUS_MSG;
import static testproject.ambal.literssreader.service.Downloader.STATUS_SUCCESS;
import static testproject.ambal.literssreader.service.Downloader.UPDATED;

/**
 * Created by Ambal on 28.07.14.
 */
public class AddActivity extends SherlockActivity{

    private static final String LOG_TAG = "mylogs";
    private EditText mText;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);
        mText = (EditText) findViewById(R.id.editText);

        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        final Map<String, String> result = (Map<String, String>) msg.obj;
                        if (result!=null) {
                            // если статус не "ОК", проверить инет
                            if (!result.get(STATUS_MSG).equals(STATUS_SUCCESS)) {
                                Toast.makeText(getBaseContext(), String.valueOf(result.get(STATUS_MSG))
                                        , Toast.LENGTH_SHORT).show();
                            } else {
                                //показываем сколько скачалось и сколько обновилось
                                Toast.makeText(getBaseContext(), (String.valueOf(result.get(STATUS_MSG)))
                                        .concat(String.valueOf(", updated - "))
                                        .concat(String.valueOf(result.get(UPDATED)))
                                        .concat(", created - ")
                                        .concat(String.valueOf(result.get(CREATED)))
                                        , Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e(LOG_TAG, "empty result");
                        }
                        break;
                }
            }
        };


        if (NavUtils.getParentActivityName(this) != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (NavUtils.getParentActivityName(this) != null) {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //"add Feed" button listener
    public void addFeed(View v) {
        String newFeed = mText.getText().toString();

        if (!Patterns.WEB_URL.matcher(newFeed).matches()) {
            Toast.makeText(this, "Incorrect input URL", Toast.LENGTH_SHORT).show();
            return;
        }
        DataUpdater mUpdater = new DataUpdater(this, mHandler, true);
        mUpdater.execute(newFeed);
    }

}
