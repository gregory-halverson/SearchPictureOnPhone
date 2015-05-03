package halverson.gregory.reverseimagesearch.searchpictureonphone.thread;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import halverson.gregory.image.AndroidCodec;
import halverson.gregory.image.hash.Hash;
import halverson.gregory.image.hash.ImageHash;
import halverson.gregory.reverseimagesearch.searchpictureonphone.activity.SearchPictureOnPhoneActivity;
import halverson.gregory.reverseimagesearch.searchpictureonphone.database.DeviceImagesIndex;
import halverson.gregory.reverseimagesearch.searchpictureonphone.database.FileValidator;
import halverson.gregory.reverseimagesearch.searchpictureonphone.database.ImageProfile;
import halverson.gregory.reverseimagesearch.searchpictureonphone.fragment.SearchOnPhoneWaitingFragment;

/**
 * Created by Gregory on 5/2/2015.
 */
// Asynchronous task for hashing images on phone
public class SearchJob extends AsyncTask<Void, Void, Integer>
{
    public static final String TAG = "SearchJob";

    // Pointers
    SearchPictureOnPhoneActivity activity;
    SearchOnPhoneWaitingFragment fragment;
    String targetUriString;
    TextView statusTextView;

    public SearchJob(SearchPictureOnPhoneActivity activity, SearchOnPhoneWaitingFragment fragment, String targetUriString, TextView statusTextView)
    {
        this.activity = activity;
        this.fragment = fragment;
        this.targetUriString = targetUriString;
        this.statusTextView = statusTextView;
    }

    @Override
    protected Integer doInBackground(Void [] args)
    {
        String imageFilePath = "";
        String queriedHashString = "";
        Map<String, Integer> hammingDistances = new HashMap<String, Integer>();
        ArrayList<String> searchResults = new ArrayList<String>();

        // Scaled image size
        ImageSize imageSize = new ImageSize(8, 8);

        // Open database
        status("Opening database");
        DeviceImagesIndex deviceImagesIndex = activity.openDatabase(fragment);

        status("Loading hash table");
        Map<String, Hash> hashTable = deviceImagesIndex.getHashTable();

        // Get hash of target image
        status("Hashing target image");
        //Bitmap bitmap = ImageLoader.getInstance().loadImageSync(targetUriString);
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(targetUriString, imageSize, null);

        //Hash targetHash = ImageHash.Average.hashFromBitmap(bitmap);
        Hash targetHash = AndroidCodec.hashFrom8by8Bitmap(bitmap);
        String targetHashString = targetHash.toHexString();
        targetHash = null;

        bitmap.recycle();
        bitmap = null;
/*
        // Media store query
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;

        //Stores all the images from the gallery in Cursor
        Cursor mediaCursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
*/

        status("Querying media store");
        ArrayList<String> mediaStorePathStrings = deviceImagesIndex.getCompleteList();
        int count = mediaStorePathStrings.size();

/*
        //Total number of images
        int count = mediaCursor.getCount();

        // Column index for file path of images in media store
        int columnIndex = mediaCursor.getColumnIndex(MediaStore.Images.Media.DATA);
*/

        /*
        // Progress timer
        long startTime = System.nanoTime();
        long currentTime = System.nanoTime();
        float elapsedSeconds = 0;
        float averageOperationTime = 0;
        int estimatedSeconds = 0;
        String timeRemaining = "";
        */

        // Loop through all images on device
        for (int i = 0; i < count; i++)
        {
            /*
            // Check status of thread every 10 iterations
            if (i % 10 == 0 && i > 0)
            {
                // Check if thread is cancelled
                if (isCancelled())
                {
                    mediaCursor.close();
                    return 2;
                }

                // Update progress
                currentTime = System.nanoTime();
                elapsedSeconds = (currentTime - startTime) / 1000000000.0f;
                averageOperationTime = elapsedSeconds / i;
                estimatedSeconds = (int) (averageOperationTime * (count - i));
                timeRemaining = " (" + (estimatedSeconds / 60) + ":" + String.format("%02d", estimatedSeconds % 60) + "s left)";
            }
            */

            // Check if thread is cancelled
            if (isCancelled())
            {
                //mediaCursor.close();
                return 2;
            }

            //status("Searching " + i + " of " + count + timeRemaining);
            status("Searching " + i + " of " + count);

            // Get path of queried image
            //mediaCursor.moveToPosition(i);
            //imageFilePath = mediaCursor.getString(columnIndex);
            imageFilePath = mediaStorePathStrings.get(i);
/*
            // Validate path
            if (!FileValidator.checkFilePath(imageFilePath))
            {
                Log.d(TAG, imageFilePath + " not available");

                continue;
            }
*/
            // Query hash for queried image
            //Hash queriedHash = deviceImagesIndex.getHash(imageFilePath);
            //String queriedHashString = deviceImagesIndex.getHashString(imageFilePath);
            Hash queriedHash = hashTable.get(imageFilePath);

            if (queriedHash == null)
            {
                // Load bitmap for queried image
                //bitmap = ImageLoader.getInstance().loadImageSync(Uri.decode(Uri.fromFile(new File(imageFilePath)).toString()));

                // Load 8 by 8 bitmap for queried image
                bitmap = ImageLoader.getInstance().loadImageSync(Uri.decode(Uri.fromFile(new File(imageFilePath)).toString()), imageSize, null);

                // Make sure the bitmap was loaded
                if (bitmap != null)
                {
                    // Get hash for queried file
                    //queriedHash = ImageHash.Average.hashFromBitmap(bitmap);
                    queriedHash = AndroidCodec.hashFrom8by8Bitmap(bitmap);
                    queriedHashString = queriedHash.toHexString();
                    bitmap.recycle();
                    bitmap = null;

                    // Store hash in the database
                    deviceImagesIndex.createIndex(imageFilePath, queriedHash);
                }
            }
            else
            {
                queriedHashString = queriedHash.toHexString();
            }
/*
            // Update database if no hash returned from database
            //if (queriedHash == null)
            if (queriedHashString == null)
            {
                // Load bitmap for queried image
                //bitmap = ImageLoader.getInstance().loadImageSync(Uri.decode(Uri.fromFile(new File(imageFilePath)).toString()));

                // Load 8 by 8 bitmap for queried image
                bitmap = ImageLoader.getInstance().loadImageSync(Uri.decode(Uri.fromFile(new File(imageFilePath)).toString()), imageSize, null);

                // Make sure the bitmap was loaded
                if (bitmap != null)
                {
                    // Get hash for queried file
                    //queriedHash = ImageHash.Average.hashFromBitmap(bitmap);
                    Hash queriedHash = AndroidCodec.hashFrom8by8Bitmap(bitmap);
                    queriedHashString = queriedHash.toHexString();
                    bitmap.recycle();
                    bitmap = null;

                    // Store hash in the database
                    deviceImagesIndex.createIndex(imageFilePath, queriedHash);
                }
            }
*/
            // Make sure hash has been calculated
            //if (queriedHash != null)
            if (queriedHashString != null)
            {
                // Calculate hamming distance between target image and queried image
                //int hammingDistance = Hash.hammingDistance(targetHash, queriedHash);
                int hammingDistance = Hash.hammingDistanceFromStrings(targetHashString, queriedHashString);

                // Include in search results if the hamming distance is less than the cutoff
                if (hammingDistance < DeviceImagesIndex.AVERAGE_HAMMING_DISTANCE_CUTOFF)
                    hammingDistances.put(imageFilePath, hammingDistance);
            }
        }

        // Close the cursor for the media store database
        //mediaCursor.close();

        // Sort search results ascending by hamming distance

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

        deviceImagesIndex.setSearchResultsList(searchResults);

        if (deviceImagesIndex.getImageCount() == 0)
            return 1;

        return 0;
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
                fragment.setStatusText(text);
            }
        }

        activity.runOnUiThread(new StatusUpdateRunnable(text));
    }

    // Close splash screen after hash has been fetched and browser intent sent
    @Override
    protected void onPostExecute(Integer result)
    {
        if (result == 0)
            activity.displayGridFragment();
        else if (result == 1)
            status("No similar images found on device");
        else if (result == 2)
        {
            Log.d(TAG, "Search job cancelled");
            ImageLoader.getInstance().clearMemoryCache();
            ImageLoader.getInstance().clearDiskCache();
        }
    }
}