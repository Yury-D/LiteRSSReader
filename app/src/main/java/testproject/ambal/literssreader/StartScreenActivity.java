package testproject.ambal.literssreader;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
//import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import testproject.ambal.literssreader.ORM.HelperFactory;
import testproject.ambal.literssreader.ORM.entities.Item;
import testproject.ambal.literssreader.service.DataUpdater;


public class StartScreenActivity extends SherlockActivity {

    private MenuItem menuItem;
    private String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        HelperFactory.setHelper(getApplicationContext());
        try {
            Dao<Item, Integer> myDao = HelperFactory.getHelper().getItemDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_screen_menu, menu);
        //menuItem = menu.findItem(R.id.menuItem_load);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menuItem_load: {
                DataUpdater mDataUpdater = new DataUpdater(this);
                mDataUpdater.execute("http://www.news.tut.by/rss/auto/autobusiness.rss");
                /*try {
                    result = mDataUpdater.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }*/
                break;
            }
            case R.id.menuItem_settings: {
                Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show();
                break;
            }
            default:
                break;
        }
        return true;/*super.onOptionsItemSelected(item)*/
    }
}
