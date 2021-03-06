package testproject.ambal.literssreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import testproject.ambal.literssreader.ORM.entities.Channel;
import testproject.ambal.literssreader.ORM.entities.Item;
import testproject.ambal.literssreader.service.DataUpdater;
import testproject.ambal.literssreader.service.ParserJackson;

public class FragmentItemList extends SherlockFragment implements AbsListView.OnItemClickListener {
    private static final String LOG_TAG = "mylogs";
    private static DisplayImageOptions options;
    private static List<String> iconUrls;

    private OnFragmentInteractionListener mListener;
    private AbsListView mListView;
    private ListAdapter mAdapter;
    private static Channel currentChannel;
    private static List<Item> sItems;



    public static FragmentItemList newInstance(Channel channel) {
        FragmentItemList fragment = new FragmentItemList();
        currentChannel = channel;
        sItems = new ArrayList<Item>(channel.getItems());
        iconUrls = new ArrayList<String>(channel.getItems().size());
        for (Item sItem : sItems) {
            iconUrls.add(sItem.getEnclosure());
        }
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentItemList() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new CustomAdapter((ArrayList<Item>) sItems);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item_for_list clicks
        mListView.setOnItemClickListener(this);
        if (NavUtils.getParentActivityName(getActivity()) != null) {
            getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu.size()==0) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.start_screen_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            case R.id.menuItem_update:
                DataUpdater mDataUpdater = new DataUpdater(getSherlockActivity(), new Handler(), true);
                mDataUpdater.execute(currentChannel.getUrl());
                return true;
            case R.id.menuItem_settings: {
                Toast.makeText(getSherlockActivity(), "Not implemented yet", Toast.LENGTH_SHORT).show();
               /* ParserJackson v = new ParserJackson();
                Channel ch = v.parse("http://news.tut.by/rss/it.rss");
                //Log.e(LOG_TAG, ch.toString());*/
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item_for_list has been selected.
            //mListener.onFragmentInteraction(position);

            Intent i = new Intent(getActivity(), ActivityItemPager.class);
            i.putExtra("channelId", currentChannel.getId());
            i.putExtra("itemId", position);
            Log.e(LOG_TAG, String.valueOf(position +  " position in ItemFragment"));
            startActivity(i);

        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    private class CustomAdapter extends ArrayAdapter<Item> {
        private LayoutInflater inflater;
        private ArrayList<Item> items;

        private class ViewHolder {
            TextView tvTitle;
            TextView tvPubDate;
            ImageView imageView;
        }

        public CustomAdapter(ArrayList<Item> items) {
            super(getActivity(), 0 , items);
            inflater = LayoutInflater.from(getActivity());
            this.items = items;
        }
        @Override
        public int getCount() {
            return items.size();
        }
        @Override
        public Item getItem(int position) {
            return items.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_for_list, null);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                holder.tvPubDate = (TextView) convertView.findViewById(R.id.tvPubDate);
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvTitle.setText(items.get(position).getTitle());

            //приводим дату в нормальный формат гггг-мм-дд чч:cc - пример 2000-04-01 00:00
            SimpleDateFormat defFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
            Date formattedDate = new Date();
            try {
                formattedDate = defFormat.parse(items.get(position).getPubDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            holder.tvPubDate.setText(newFormat.format(formattedDate));

            ImageLoader.getInstance().displayImage(iconUrls.get(position), holder.imageView, options);
            return convertView;
        }
    }

}
