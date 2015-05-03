package halverson.gregory.reverseimagesearch.searchpictureonphone.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import halverson.gregory.reverseimagesearch.searchpictureonphone.R;
import halverson.gregory.reverseimagesearch.searchpictureonphone.activity.SearchPictureOnPhoneActivity;
import halverson.gregory.reverseimagesearch.searchpictureonphone.thread.SearchJob;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchOnPhoneWaitingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchOnPhoneWaitingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchOnPhoneWaitingFragment extends Fragment
{
    SearchPictureOnPhoneActivity activity;
    View view;

    TextView statusText;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchOnPhoneWaitingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchOnPhoneWaitingFragment newInstance(String param1, String param2)
    {
        SearchOnPhoneWaitingFragment fragment = new SearchOnPhoneWaitingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchOnPhoneWaitingFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        try
        {
            // Store pointer to activity
            activity = (SearchPictureOnPhoneActivity) super.getActivity();

            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_search_on_phone_waiting, container, false);

            statusText = (TextView) view.findViewById(R.id.statusText);

            // Load intent
            Intent intent = activity.getIntent();
            Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
            String targetUriString = imageUri.toString();

            // Load image
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            Bitmap bitmap = null;
            bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);

            // Display image
            imageView.setImageBitmap(bitmap);

            // Hash images on phone
            activity.hashJob = new SearchJob(activity, this, targetUriString, statusText);
            activity.hashJob.execute();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return view;
    }

    public void setStatusText(String text)
    {
        statusText.setText(text);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
