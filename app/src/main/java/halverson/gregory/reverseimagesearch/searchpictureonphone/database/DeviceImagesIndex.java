package halverson.gregory.reverseimagesearch.searchpictureonphone.database;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Map;

import halverson.gregory.image.hash.Hash;
import halverson.gregory.reverseimagesearch.searchpictureonphone.adapter.SearchResultsAdapter;
import halverson.gregory.reverseimagesearch.searchpictureonphone.fragment.SearchOnPhoneWaitingFragment;

/**
 * Created by Gregory on 4/20/2015.
 */
public class DeviceImagesIndex
{
    public static final String TAG = "DeviceImagesIndex";
    public static final int AVERAGE_HAMMING_DISTANCE_CUTOFF = 11;

    private Activity activity;
    private SearchResultsAdapter searchResultsAdapter = null;
    private ImageIndexDataSource dataSource;

    private ArrayList<String> searchResultFilePathStrings = null;

    public DeviceImagesIndex(Activity activity, SearchOnPhoneWaitingFragment waitingFragment)
    {
        this.activity = activity;
        this.dataSource = new ImageIndexDataSource(this.activity, waitingFragment);
        this.dataSource.open();
    }

    // Get collected list of all images on device
    public ArrayList<String> getCompleteList()
    {
        return getAllImagesPathList(activity);
    }

    // Get list of missing indices
    public ArrayList<String> getListOfMissingIndices()
    {
        ArrayList<String> listOfMissingIndices = new ArrayList<String>();

        ArrayList<String> completeList = getCompleteList();

        for (String imageFilePath: completeList)
            if (!indexExists(imageFilePath))
            {
                Log.d(TAG, "Queueing path " + imageFilePath);
                listOfMissingIndices.add(imageFilePath);
            }

        return listOfMissingIndices;
    }

    public Hash getHash(String filePath)
    {
        return dataSource.getHash(filePath);
    }

    public Map<String, Hash> getHashTable()
    {
        return dataSource.getHashTable();
    }

    public String getHashString(String filePath)
    {
        return dataSource.getHashString(filePath);
    }

    public boolean indexExists(String imageFilePath)
    {
        File file = new File(imageFilePath);
        Long fileModifiedDate = file.lastModified();

        return dataSource.entryExists(imageFilePath, fileModifiedDate);
    }

    public void setSearchResultsList(ArrayList<String> searchResultsList)
    {
        this.searchResultFilePathStrings = searchResultsList;
    }

    public void createIndex(String imagePathString, Hash averageHash)
    {
        File file = new File(imagePathString);
        Long fileModifiedDate = file.lastModified();

        dataSource.createEntry(imagePathString, averageHash, fileModifiedDate);
    }

    public String getImageUriAtPosition(int index)
    {
        return Uri.fromFile(new File(searchResultFilePathStrings.get(index))).toString();
    }

    public long getImageCount()
    {
        return searchResultFilePathStrings.size();
    }

    // Get data source for image index database
    public ImageIndexDataSource getDataSource()
    {
        return dataSource;
    }

    // Get attached display adapter
    public SearchResultsAdapter getSearchResultsAdapter()
    {
        return searchResultsAdapter;
    }

    public void setAdapter(SearchResultsAdapter searchResultsAdapter)
    {
        this.searchResultsAdapter = searchResultsAdapter;
    }

    public ArrayList<String> getAllImagesPathList(Activity activity)
    {
        ArrayList<String> allImagesPathList = new ArrayList<String>();

        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;

        //Stores all the images from the gallery in Cursor
        Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);

        //Total number of images
        int count = cursor.getCount();

        //Create an array to store path to all the images
        String path = "";

        // Iterate through cursor
        for (int i = 0; i < count; i++)
        {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            //Store the path of the image
            //path = URLDecoder.decode(cursor.getString(dataColumnIndex), "UTF-8").replaceAll("%20", " ");
            path = cursor.getString(dataColumnIndex);

            if (FileValidator.checkFilePath(path))
                allImagesPathList.add(path);
        }

        cursor.close();

        return allImagesPathList;
    }


}