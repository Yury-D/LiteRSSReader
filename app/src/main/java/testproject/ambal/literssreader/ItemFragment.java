package testproject.ambal.literssreader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;


import com.actionbarsherlock.app.SherlockFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import testproject.ambal.literssreader.ORM.entities.Channel;
import testproject.ambal.literssreader.ORM.entities.Item;
import testproject.ambal.literssreader.dummy.DummyContent;

public class ItemFragment extends SherlockFragment implements AbsListView.OnItemClickListener {
    //private static final String ARG_PARAM1 = "param1";
    static DisplayImageOptions options;
    private static List<String> iconUrls;

    private OnFragmentInteractionListener mListener;
    private AbsListView mListView;
    private ListAdapter mAdapter;
    private static List<Item> sItems;

    public static ItemFragment newInstance(Channel channel) {
        ItemFragment fragment = new ItemFragment();
        //Bundle args = new Bundle();
        //ArrayList<String> titles = new ArrayList<String>(channel.getItems().size());

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
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();

        /*for (Item mItem : sItems) {
            titles.add(mItem.getTitle());
        }
        args.putStringArrayList(ARG_PARAM1, titles);
        fragment.setArguments(args);
        */
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new CustomAdapter((ArrayList<Item>) sItems);
        /*
        ArrayList<String> titles = new ArrayList<String>();
        if (getArguments() != null) {
            titles = getArguments().getStringArrayList(ARG_PARAM1);
        }
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, titles) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setTextColor(Color.parseColor("black"));
                return view;
            }*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
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
                convertView = inflater.inflate(R.layout.item, null);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                holder.tvPubDate = (TextView) convertView.findViewById(R.id.tvPubDate);
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvTitle.setText(items.get(position).getTitle());

            //приводим дату в нормальный формат гггг-мм-дд чч:cc - пример 2000-04-01 00:00
            SimpleDateFormat defFormat = new SimpleDateFormat("EEE, mm MMM yyyy HH:mm:ss Z", Locale.US);
            Date formattedDate = new Date();
            try {
                formattedDate = defFormat.parse(items.get(position).getPubDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            holder.tvPubDate.setText(newFormat.format(formattedDate));

            ImageLoader.getInstance().displayImage(iconUrls.get(position), holder.imageView);
            //holder.imageView.setImageBitmap(ImageLoader.getInstance().loadImageSync(iconUrls.get(position)));
            return convertView;
        }
    }



    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }









}
