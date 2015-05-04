package halverson.gregory.reverseimagesearch.searchpictureonphone.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import halverson.gregory.reverseimagesearch.searchpictureonphone.R;
import halverson.gregory.reverseimagesearch.searchpictureonphone.activity.LiveSearchImageOnPhoneActivity;
import halverson.gregory.reverseimagesearch.searchpictureonphone.activity.WaitingScreenSearchPictureOnPhoneActivity;
import halverson.gregory.reverseimagesearch.searchpictureonphone.adapter.SearchResultsAdapter;
import halverson.gregory.reverseimagesearch.searchpictureonphone.database.DeviceImagesIndex;

// Image grid activity
public class LiveDisplayGridFragment extends Fragment
{
    // Activity
    LiveSearchImageOnPhoneActivity activity;

    // View
    View rootView;
    GridView gridView;
    TextView statusTextView;
    LinearLayout statusBar;

    // Image loader
    DisplayImageOptions options;
    private ImageLoader imageLoader;

    SearchResultsAdapter searchResultsAdapter;

    public LiveDisplayGridFragment() { }

    public void hideStatusBar()
    {
        statusBar.setVisibility(View.INVISIBLE);
    }

    // Load image grid
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        activity = (LiveSearchImageOnPhoneActivity) super.getActivity();
        rootView = inflater.inflate(R.layout.fragment_live_display_grid, container, false);

        // Instantiate image loader
        imageLoader = ImageLoader.getInstance();

        // Set options for image loader
        options = new DisplayImageOptions.Builder()
                // Set loading icon
                //.showImageOnLoading(R.drawable.place_holder)
                //.showImageForEmptyUri(R.drawable.hand)
                //.showImageOnFail(R.drawable.big_problem)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        // Get handle of image grid
        gridView = (GridView) rootView.findViewById(R.id.liveGridView);

        // Get pointer to status text view
        statusTextView = (TextView) rootView.findViewById(R.id.liveGridStatusText);
        activity.searchJob.attachStatusTextView(statusTextView);

        statusBar = (LinearLayout) rootView.findViewById(R.id.statusBar);

        // Get pointer to image index
        DeviceImagesIndex deviceImagesIndex = activity.getDeviceImagesIndex();

        this.searchResultsAdapter = new SearchResultsAdapter(activity, imageLoader, options, deviceImagesIndex);

        // Set adapter for image grid data
        gridView.setAdapter(this.searchResultsAdapter);
        deviceImagesIndex.setAdapter(this.searchResultsAdapter);
/*
        // Set listener for pressing image in grid
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                gridview.getAdapter().getItem(position);
                Intent intent = new Intent(ImageGridActivity.this, ShowImageActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("uri", deviceImagesIndex.getMediaStoreImageFileList().get(position));
                startActivity(intent);
            }
        });
*/

        // Don't use this method, it's handled by inflater.inflate() above :
        // setContentView(R.layout.activity_layout);

        // The FragmentActivity doesn't contain the layout directly so we must use our instance of     LinearLayout :
        // layout.findViewById(R.id.someGuiElement);
        // Instead of :
        // findViewById(R.id.someGuiElement);
        //return layout; // We must return the loaded Layout
        return rootView;
    }

    /*
    static Integer[] mThumbIds = {R.drawable.amazed, R.drawable.angelic,
            R.drawable.cool, R.drawable.crying, R.drawable.devil,
            R.drawable.laughing, R.drawable.loving, R.drawable.question,
            R.drawable.sad, R.drawable.silence, R.drawable.simple, R.drawable.sleeping,
            R.drawable.smiling, R.drawable.tongue, R.drawable.winking, R.drawable.worried,
            R.drawable.amazed, R.drawable.angelic, R.drawable.cool, R.drawable.crying,
            R.drawable.devil, R.drawable.laughing, R.drawable.loving, R.drawable.question,
            R.drawable.sad, R.drawable.silence, R.drawable.simple, R.drawable.sleeping,
            R.drawable.smiling, R.drawable.tongue, R.drawable.winking, R.drawable.worried};
    */
}

