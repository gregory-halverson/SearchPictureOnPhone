package halverson.gregory.reverseimagesearch.searchpictureonphone.thread;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import halverson.gregory.image.AndroidCodec;
import halverson.gregory.image.hash.Hash;
import halverson.gregory.image.hash.ImageHash;
import halverson.gregory.reverseimagesearch.searchpictureonphone.activity.LiveSearchImageOnPhoneActivity;
import halverson.gregory.reverseimagesearch.searchpictureonphone.database.DeviceImagesIndex;
import halverson.gregory.reverseimagesearch.searchpictureonphone.database.FileValidator;
import halverson.gregory.reverseimagesearch.searchpictureonphone.database.ImageProfile;

/**
 * Created by Gregory on 5/2/2015.
 */
// Asynchronous task for hashing images on phone
public class SearchJob extends AsyncTask<Void, Void, SearchJob.ReturnCode>
{
    public static final String TAG = "SearchJob";

    public static enum ReturnCode
    {
        SEARCH_COMPLETED_WITH_NO_ERROR,
        NO_IMAGES_FOUND,
        SEARCH_CANCELLED
    }

    // Pointers
    LiveSearchImageOnPhoneActivity activity;
    String targetUriString;
    TextView statusTextView;
    DeviceImagesIndex deviceImagesIndex;

    public SearchJob(LiveSearchImageOnPhoneActivity activity, String targetUriString, TextView statusTextView)
    {
        this.activity = activity;
        this.targetUriString = targetUriString;
        this.statusTextView = statusTextView;
    }

    public void attachStatusTextView(TextView statusTextView)
    {
        this.statusTextView = statusTextView;
    }

    @Override
    protected ReturnCode doInBackground(Void [] args)
    {
        // Map of hamming distances to store unsorted search results
        Map<String, Integer> hammingDistances = new HashMap<String, Integer>();

        // Get pointer to image loader
        ImageLoader imageLoader = ImageLoader.getInstance();

        // Open database
        status("Opening database");
        deviceImagesIndex = activity.openDatabase();

        // Load hash table into map
        status("Loading hash table");
        Map<String, ImageProfile> hashTable = deviceImagesIndex.getHashTable();
        int hashTableSize = hashTable.size();

        // Get hash of target image
        status("Hashing target image");
        Hash targetHash = AndroidCodec.hashFromUriString(targetUriString);
        long targetModifiedDate = FileValidator.getLastModifiedDateUri(targetUriString);
/*
        // Get list of image file paths from media store database
        status("Querying media store");
        //ArrayList<String> mediaStorePathStrings = deviceImagesIndex.getMediaStoreImageFileList();
        ArrayList<String> mediaStorePathStrings = deviceImagesIndex.getTestSet();
        int mediaStoreImageFileCount = mediaStorePathStrings.size();
*/
        boolean gridFragmentDisplayed = false;

        int indexedImagesIterator = 1;

        // Loop through all hashes in database
        for (String imageFilePath: hashTable.keySet())
        {
            // Check if thread is cancelled
            if (isCancelled())
                return ReturnCode.SEARCH_CANCELLED;

            // Report progress to waiting fragment
            status("Searching " + indexedImagesIterator++ + " of " + hashTableSize);

            // Skip if file doesn't exist
            if (!FileValidator.checkFilePath(imageFilePath))
                continue;

            // Query hash for queried image
            ImageProfile imageProfile = hashTable.get(imageFilePath);

            // Skip if hash is out of date
            if (imageProfile.modifiedDate != FileValidator.getLastModifiedDatePath(imageFilePath))
                continue;

            // Calculate hamming distance between target image and queried image
            int hammingDistance = Hash.hammingDistance(targetHash, imageProfile.averageHash);
            //Log.d(TAG, "hamming(" + targetHash.toHexString() + ", " + imageProfile.averageHash.toHexString() + "): " + hammingDistance + " for " + imageFilePath);

            // Include in search results if the hamming distance is less than the cutoff
            if (hammingDistance < DeviceImagesIndex.AVERAGE_HAMMING_DISTANCE_CUTOFF)
            {
                // Add to hamming distances
                hammingDistances.put(imageFilePath, hammingDistance);

                // Sort results by hamming distance
                deviceImagesIndex.setSearchResultsList(sortSearchResults(hammingDistances));

                // Display results
                if (gridFragmentDisplayed)
                    notifyAdapter();
                else
                    activity.displayGridFragment();
            }
        }

        // Search of previously indexed images complete, update database

        status("Looking for new images");

        // List of missing indices
        //ArrayList<String> missingIndices = deviceImagesIndex.getListOfMissingIndicesTestSet();
        ArrayList<String> missingIndices = deviceImagesIndex.getListOfMissingIndices();
        int missingIndicesCount = missingIndices.size();
        int missingIndicesIterator = 1;

        // Loop through files that haven't been indexed yet
        for (String imageFilePath: missingIndices)
        {
            // Check if thread is cancelled
            if (isCancelled())
                return ReturnCode.SEARCH_CANCELLED;

            // Report progress to waiting fragment
            status("Indexing " + missingIndicesIterator++ + " of " + missingIndicesCount);

            // Load bitmap to be hashed and indexed
            Bitmap bitmap = imageLoader.loadImageSync(AndroidCodec.decodedUriStringFromFilePathString(imageFilePath));

            // Make sure bitmap loaded
            if (bitmap == null)
            {
                Log.d(TAG, "unable to load " + imageFilePath);
                continue;
            }

            // Generate new hash
            Hash generatedHash = ImageHash.Average.hashFromBitmap(bitmap);

            // Store successful hash in the database
            if (generatedHash != null)
                deviceImagesIndex.createIndex(imageFilePath, generatedHash);
            else
                continue;

            // Calculate hamming distance between target image and queried image
            int hammingDistance = Hash.hammingDistance(targetHash, generatedHash);
            //Log.d(TAG, "hamming(" + targetHash.toHexString() + ", " + imageProfile.averageHash.toHexString() + "): " + hammingDistance + " for " + imageFilePath);

            // Include in search results if the hamming distance is less than the cutoff
            if (hammingDistance < DeviceImagesIndex.AVERAGE_HAMMING_DISTANCE_CUTOFF)
            {
                // Add to hamming distances
                hammingDistances.put(imageFilePath, hammingDistance);

                // Sort results by hamming distance
                deviceImagesIndex.setSearchResultsList(sortSearchResults(hammingDistances));

                // Display results
                if (gridFragmentDisplayed)
                    notifyAdapter();
                else
                    activity.displayGridFragment();
            }
        }

        // Check if no images were found
        if (deviceImagesIndex.getImageCount() == 0)
            return ReturnCode.NO_IMAGES_FOUND;

        return ReturnCode.SEARCH_COMPLETED_WITH_NO_ERROR;
    }

    private static ArrayList<String> sortSearchResults(Map<String, Integer> hammingDistances)
    {
        ArrayList<String> searchResults = new ArrayList<String>();

        List<Map.Entry<String, Integer>> sortableHammingDistances = new ArrayList<Map.Entry<String, Integer>>();

        sortableHammingDistances.addAll(hammingDistances.entrySet());

        Collections.sort(sortableHammingDistances, new Comparator<Map.Entry<String, Integer>>()
        {
            @Override
            public int compare(Map.Entry<String, Integer> left, Map.Entry<String, Integer> right)
            {
                return left.getValue().compareTo(right.getValue());
            }
        });

        for (Map.Entry<String, Integer> entry: sortableHammingDistances)
            searchResults.add(entry.getKey());

        return searchResults;
    }

    // Report progress to UI thread
    private void status(String text)
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
                Log.d(TAG, "setting status " + text);
                statusTextView.setText(text);
            }
        }

        if (statusTextView != null)
            activity.runOnUiThread(new StatusUpdateRunnable(text));
    }

    private void notifyAdapter()
    {
        class NotifyAdapterRunnable implements Runnable
        {
            String text;

            NotifyAdapterRunnable()
            {
                super();
            }

            @Override
            public void run()
            {
                deviceImagesIndex.notifyAdapter();
            }
        }

        if (deviceImagesIndex != null)
            activity.runOnUiThread(new NotifyAdapterRunnable());
    }

    // Cleanup after search
    @Override
    protected void onPostExecute(ReturnCode result)
    {
        switch (result)
        {
            // Close splash screen after hash has been fetched and browser intent sent
            case SEARCH_COMPLETED_WITH_NO_ERROR:
                //activity.displayGridFragment();
                activity.hideStatusBar();
                break;

            // Report that no images were found
            case NO_IMAGES_FOUND:
                status("No similar images found on device");
                activity.hideSpinner();
                break;

            // Clear memory
            case SEARCH_CANCELLED:
                Log.d(TAG, "Search job cancelled");
                ImageLoader.getInstance().clearMemoryCache();
                ImageLoader.getInstance().clearDiskCache();
                break;
        }
    }
}