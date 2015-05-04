package halverson.gregory.reverseimagesearch.searchpictureonphone.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import halverson.gregory.reverseimagesearch.searchpictureonphone.R;
import halverson.gregory.reverseimagesearch.searchpictureonphone.activity.LiveSearchImageOnPhoneActivity;
import halverson.gregory.reverseimagesearch.searchpictureonphone.thread.SearchJob;

public class LiveWaitingScreenFragment extends Fragment
{
    LiveSearchImageOnPhoneActivity activity;
    View view;

    ImageLoader imageLoader;

    TextView statusText;
    ImageView imageView;
    ProgressBar spinner;

    public LiveWaitingScreenFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Store pointer to activity
        activity = (LiveSearchImageOnPhoneActivity) super.getActivity();

        imageLoader = ImageLoader.getInstance();

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_live_waiting_screen, container, false);

        statusText = (TextView) view.findViewById(R.id.liveStatusText);
        spinner = (ProgressBar) view.findViewById(R.id.liveSpinner);

        // Load intent
        Intent intent = activity.getIntent();
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        String targetUriString = imageUri.toString();

        // Load image
        imageView = (ImageView) view.findViewById(R.id.liveImageView);
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(targetUriString);

        // Display image
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);

        // Hash images on phone
        activity.searchJob = new SearchJob(activity, targetUriString, statusText);
        activity.searchJob.execute();

        return view;
    }

    public void setStatusText(String text)
    {
        statusText.setText(text);
    }

    public void hideSpinner()
    {
        if (spinner != null)
            spinner.setVisibility(View.INVISIBLE);
    }
}
