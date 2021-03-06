package testproject.ambal.literssreader;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import java.sql.SQLException;

import testproject.ambal.literssreader.ORM.HelperFactory;
import testproject.ambal.literssreader.ORM.entities.Channel;

public class ActivityDetailFeed extends SherlockFragmentActivity implements FragmentItemList.OnFragmentInteractionListener, FragmentSelectedItem.OnFragmentInteractionListener {
    private static final String LOG_TAG = "mylogs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int channelId = getIntent().getIntExtra("ChannelId",0);
        setContentView(R.layout.activity_detail_feed);

        Channel mChannel = null;
        try {
            mChannel = HelperFactory.getHelper().getChannelDao().queryForId(channelId);
            ActionBar actionBar = getSupportActionBar();
            //actionBar.setTitle(mChannel.getTitle());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = manager.beginTransaction();
        SherlockFragment myFragment = (SherlockFragment) manager.findFragmentById(R.id.frgm);
        if (null == myFragment) {
            myFragment = FragmentItemList.newInstance(mChannel);
            mFragmentTransaction.add(R.id.frgm, myFragment);
            mFragmentTransaction.commit();
        }
        mFragmentTransaction.show(myFragment);

    }

    @Override
    public void onFragmentInteraction(String id) {
        Log.e(LOG_TAG, "onFragmentInteraction");
    }

}