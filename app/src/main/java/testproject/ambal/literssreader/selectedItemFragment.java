package testproject.ambal.literssreader;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;

import testproject.ambal.literssreader.ORM.entities.Item;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectedItemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectedItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SelectedItemFragment extends SherlockFragment {
    private static final String ARG_ID = "id";
    private static int itemId = 0;
    private static Item mItem;

    private OnFragmentInteractionListener mListener;

    public static SelectedItemFragment newInstance(Item selectedItem) {
        SelectedItemFragment fragment = new SelectedItemFragment();
        mItem = selectedItem;
        return fragment;
    }
    public SelectedItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemId = getArguments().getInt(ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        TextView title = (TextView) view.findViewById(R.id.tvTitle);
        TextView content = (TextView) view.findViewById(R.id.tvContent);
        TextView date = (TextView) view.findViewById(R.id.tvPubDate);
        title.setText(mItem.getTitle());
        content.setText(Html.fromHtml(mItem.getDescription()));
        content.setMovementMethod(LinkMovementMethod.getInstance());
        date.setText(mItem.getPubDate());

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
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
