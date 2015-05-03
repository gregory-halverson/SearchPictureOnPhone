package halverson.gregory.reverseimagesearch.searchpictureonphone.thread;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import halverson.gregory.image.AndroidCodec;
import halverson.gregory.image.hash.Hash;
import halverson.gregory.image.hash.ImageHash;
import halverson.gregory.reverseimagesearch.searchpictureonphone.activity.UpdateIndexActivity;
import halverson.gregory.reverseimagesearch.searchpictureonphone.database.DeviceImagesIndex;
import halverson.gregory.reverseimagesearch.searchpictureonphone.database.FileValidator;
import halverson.gregory.reverseimagesearch.searchpictureonphone.database.ImageProfile;

// Asynchronous task for hashing images on phone
public class IndexJob extends AsyncTask<Void, Void, IndexJob.ReturnCode>
{
    public static final String TAG = "IndexJob";

    public static enum ReturnCode
    {
        INDEX_COMPLETED_WITH_NO_ERROR,
        INDEX_CANCELLED
    }

    // Pointers
    UpdateIndexActivity activity;
    TextView statusTextView;
    ImageView imageView;
    ProgressBar spinner;

    public IndexJob(UpdateIndexActivity activity, TextView statusTextView, ImageView imageView, ProgressBar spinner)
    {
        this.activity = activity;
        this.statusTextView = statusTextView;
        this.imageView = imageView;
        this.spinner = spinner;
    }

    @Override
    protected ReturnCode doInBackground(Void [] args)
    {
        // Get pointer to image loader
        ImageLoader imageLoader = ImageLoader.getInstance();

        // Open database
        setStatus("Opening database");
        DeviceImagesIndex deviceImagesIndex = activity.openDatabase();

        // Get list of image file paths from media store database
        setStatus("Querying media store");
        //ArrayList<String> mediaStorePathStrings = deviceImagesIndex.getMediaStoreImageFileList();
        ArrayList<String> mediaStorePathStrings = deviceImagesIndex.getTestSet();
        int mediaStoreImageFileCount = mediaStorePathStrings.size();

        // Loop through all images on device
        for (int i = 0; i < mediaStoreImageFileCount; i++)
        {
            // Check if thread is cancelled
            if (isCancelled())
                return ReturnCode.INDEX_CANCELLED;

            // Report progress to waiting fragment
            setStatus("Indexing " + i + " of " + mediaStoreImageFileCount);

            // Get path of queried image;
            String imageFilePath = mediaStorePathStrings.get(i);
            long targetModifiedDate = FileValidator.getLastModifiedDateFromUriString(imageFilePath);

            // Query hash for queried image
            ImageProfile imageProfile = deviceImagesIndex.get(imageFilePath);

            // Update database if no hash returned from database
            if (imageProfile == null || targetModifiedDate != imageProfile.modifiedDate)
            {
                // Generate new hash
                //Hash generatedHash = AndroidCodec.hashFromFilePathString(imageFilePath);
                Bitmap bitmap = imageLoader.loadImageSync(AndroidCodec.decodedUriStringFromFilePathString(imageFilePath));
                setImage(bitmap);
                Hash generatedHash = ImageHash.Average.hashFromBitmap(bitmap);

                // Store successful hash in the database
                if (generatedHash != null)
                    deviceImagesIndex.createIndex(imageFilePath, generatedHash);
            }
        }

        return ReturnCode.INDEX_COMPLETED_WITH_NO_ERROR;
    }

    // Report progress to UI thread
    private void setStatus(String text)
    {
        class StatusUpdateRunnable implements Runnable
        {
            String text;

            StatusUpdateRunnable(String text)
            {
                super();
                this.text = text;
            }

            @Override
            public void run()
            {
                statusTextView.setText(text);
            }
        }

        if (statusTextView != null)
            activity.runOnUiThread(new StatusUpdateRunnable(text));
    }

    private void setImage(Bitmap bitmap)
    {
        class StatusUpdateRunnable implements Runnable
        {
            Bitmap bitmap;

            StatusUpdateRunnable(Bitmap bitmap)
            {
                super();
                this.bitmap = bitmap;
            }

            @Override
            public void run()
            {
                imageView.setImageBitmap(bitmap);
            }
        }

        if (imageView != null)
            activity.runOnUiThread(new StatusUpdateRunnable(bitmap));
    }

    // Cleanup after search
    @Override
    protected void onPostExecute(ReturnCode result)
    {
        switch (result)
        {
            // Close splash screen after hash has been fetched and browser intent sent
            case INDEX_COMPLETED_WITH_NO_ERROR:
                setStatus("Index up to date");
                spinner.setVisibility(View.INVISIBLE);
                break;

            // Clear memory
            case INDEX_CANCELLED:
                Log.d(TAG, "Search job cancelled");
                ImageLoader.getInstance().clearMemoryCache();
                ImageLoader.getInstance().clearDiskCache();
                break;
        }
    }
}