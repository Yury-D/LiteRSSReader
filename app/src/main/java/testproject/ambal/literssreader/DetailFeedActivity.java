package testproject.ambal.literssreader;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import java.sql.SQLException;

import testproject.ambal.literssreader.ORM.HelperFactory;
import testproject.ambal.literssreader.ORM.entities.Channel;

public class DetailFeedActivity extends SherlockFragmentActivity implements ItemFragment.OnFragmentInteractionListener {
    Intent mIntent;
    SherlockFragment myFragment;
    android.support.v4.app.FragmentTransaction mFragmentTransaction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int channelId = getIntent().getIntExtra("ChannelId",0);
        setContentView(R.layout.detail_feed);
        Channel mChannel = null;
        try {
            mChannel = HelperFactory.getHelper().getChannelDao().queryForId(channelId);
            Log.d("mylog", mChannel.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        myFragment = ItemFragment.newInstance(mChannel);
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.add(R.id.frgm, myFragment);
        mFragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(String id) {

    }
}