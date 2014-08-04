package testproject.ambal.literssreader;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import java.sql.SQLException;
import java.util.ArrayList;

import testproject.ambal.literssreader.ORM.HelperFactory;
import testproject.ambal.literssreader.ORM.entities.Channel;
import testproject.ambal.literssreader.ORM.entities.Item;

/**
 * Created by Ambal on 03.08.2014.
 */
public class ActivityItemPager extends SherlockFragmentActivity implements FragmentSelectedItem.OnFragmentInteractionListener{
    private static final String LOG_TAG = "mylogs";
    private ArrayList<Item> mItems;
    int itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager_items);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);
        Intent args = getIntent();
        int channelId = args.getIntExtra("channelId", 0);
        itemId = args.getIntExtra("itemId", 0);
        Channel mChannel = null;
        try {
            mChannel = HelperFactory.getHelper().getChannelDao().queryForId(channelId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (mChannel!=null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(mChannel.getTitle());
            mItems = new ArrayList<Item>(mChannel.getItems());
        } else {mItems = new ArrayList<Item>(0);}
        FragmentManager manager = getSupportFragmentManager();

        mViewPager.setAdapter(new FragmentStatePagerAdapter(manager) {
            @Override
            public Fragment getItem(int position) {
                Item item = mItems.get(position);
                return FragmentSelectedItem.newInstance(item);
            }

            @Override
            public int getCount() { return mItems.size(); }
            @Override
            public CharSequence getPageTitle(int position){
                return (CharSequence)mItems.get(position).getCategory();
            }
        });
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(Color.DKGRAY);
        mViewPager.setCurrentItem(itemId);

    }

    @Override
    public void onFragmentInteraction(String id) { Log.e(LOG_TAG, "onFragmentInteraction"); }
}
