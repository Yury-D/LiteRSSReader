package testproject.ambal.literssreader;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import testproject.ambal.literssreader.ORM.entities.Item;

public class FragmentSelectedItem extends SherlockFragment {
    private static final String LOG_TAG = "mylogs";
    private static final String ARG_ID = "id";
    private Item mItem;

    private OnFragmentInteractionListener mListener;

    public static FragmentSelectedItem newInstance(Item selectedItem) {
        FragmentSelectedItem fragment = new FragmentSelectedItem();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ID, selectedItem);
        fragment.setArguments(args);

        return fragment;
    }
    public FragmentSelectedItem() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getArguments() != null) {
            mItem = (Item) getArguments().getSerializable(ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selected_item, container, false);
        TextView title = (TextView) view.findViewById(R.id.tvTitle);
        TextView content = (TextView) view.findViewById(R.id.tvContent);
        TextView date = (TextView) view.findViewById(R.id.tvPubDate);
        ImageView img = (ImageView) view.findViewById(R.id.imageView);
        title.setText(mItem.getTitle());
        content.setText(Html.fromHtml(mItem.getDescription()));
        content.setMovementMethod(LinkMovementMethod.getInstance());
        date.setText(mItem.getPubDate());
        ImageLoader.getInstance().displayImage(mItem.getEnclosure(), img);

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Log.e(LOG_TAG, "click");
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
