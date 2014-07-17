package testproject.ambal.literssreader;

import android.os.Bundle;
import android.view.Menu;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import testproject.ambal.literssreader.ORM.HelperFactory;
import testproject.ambal.literssreader.ORM.entities.Item;


public class StartScreenActivity extends SherlockActivity {

    private MenuItem menuItem;

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
        getMenuInflater().inflate(R.menu.start_screen, menu);
        //menuItem = menu.findItem(R.id.menu_load);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
