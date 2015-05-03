package halverson.gregory.reverseimagesearch.searchpictureonphone.fragment;

import halverson.gregory.image.hash.Hash;
import halverson.gregory.image.hash.ImageHash;
import halverson.gregory.reverseimagesearch.searchpictureonphone.activity.SearchPictureOnPhoneActivity;
import halverson.gregory.reverseimagesearch.searchpictureonphone.adapter.SearchResultsAdapter;
import halverson.gregory.reverseimagesearch.searchpictureonphone.database.DeviceImagesIndex;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;

import halverson.gregory.reverseimagesearch.searchpictureonphone.R;

// Image grid activity
public class DisplayGridFragment extends Fragment
{
    // Activity
    SearchPictureOnPhoneActivity activity;
    //LinearLayout layout;
    View rootView;

    // Image loader
    DisplayImageOptions options;
    private ImageLoader imageLoader;

    SearchResultsAdapter searchResultsAdapter;

    public DisplayGridFragment()
    {

    }

    // Load image grid
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (SearchPictureOnPhoneActivity) super.getActivity();
        // Replace LinearLayout by the type of the root element of the layout you're trying to load
        //layout    = (LinearLayout)    inflater.inflate(R.layout.activity_main, container, false);
        rootView = inflater.inflate(R.layout.display_grid, container, false);

        // Of course you will want to faActivity and llLayout in the class and not this method to access them in the rest of
        // the class, just initialize them here

        // Content of previous onCreate() here

        // Load intent
        Intent intent = activity.getIntent();
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        // Load target image
        try
        {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
            Hash hash = ImageHash.Average.hashFromBitmap(bitmap);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

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
        final GridView gridview = (GridView) rootView.findViewById(R.id.gridview);

        // Get pointer to image index
        DeviceImagesIndex deviceImagesIndex = activity.getDeviceImagesIndex();

        this.searchResultsAdapter = new SearchResultsAdapter(activity, imageLoader, options, deviceImagesIndex);

        // Set adapter for image grid data
        gridview.setAdapter(this.searchResultsAdapter);
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

