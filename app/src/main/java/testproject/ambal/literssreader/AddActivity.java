package testproject.ambal.literssreader;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

import java.util.Map;

import testproject.ambal.literssreader.service.DataUpdater;

/**
 * Created by Ambal on 28.07.14.
 */
public class AddActivity extends SherlockActivity{

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
                        final Map<String, Integer> result = (Map<String, Integer>) msg.obj;
                        String mapToString = result.toString();
                        //показываем сколько скачалось и сколько обновилось
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
