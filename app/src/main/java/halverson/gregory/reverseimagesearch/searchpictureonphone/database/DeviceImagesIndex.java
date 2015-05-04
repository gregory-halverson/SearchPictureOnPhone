package halverson.gregory.reverseimagesearch.searchpictureonphone.database;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import halverson.gregory.image.hash.Hash;
import halverson.gregory.reverseimagesearch.searchpictureonphone.adapter.SearchResultsAdapter;

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

    public DeviceImagesIndex(Activity activity)
    {
        this.activity = activity;
        this.dataSource = new ImageIndexDataSource(this.activity);
        this.dataSource.open();
        this.searchResultFilePathStrings = new ArrayList<String>();
    }

    public ImageProfile get(String filePath)
    {
        return dataSource.get(filePath);
    }

    // Get collected list of all images on device
    public ArrayList<String> getMediaStoreImageFileList()
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
            path = cursor.getString(dataColumnIndex);

            if (FileValidator.checkFilePath(path))
                allImagesPathList.add(path);
        }

        cursor.close();

        return allImagesPathList;
    }

    public ArrayList<String> getTestSet()
    {
        ArrayList<String> testSetPathList = new ArrayList<String>();

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
            path = cursor.getString(dataColumnIndex);

            if (path.contains("/storage/extSdCard/testset") && FileValidator.checkFilePath(path))
                testSetPathList.add(path);
        }

        cursor.close();

        return testSetPathList;
    }

    // Get list of missing indices
    public ArrayList<String> getListOfMissingIndices()
    {
        ArrayList<String> listOfMissingIndices = new ArrayList<String>();

        ArrayList<String> completeList = getMediaStoreImageFileList();

        for (String imageFilePath: completeList)
            if (!indexExists(imageFilePath))
            {
                Log.d(TAG, "Queueing path " + imageFilePath);
                listOfMissingIndices.add(imageFilePath);
            }

        return listOfMissingIndices;
    }

    public ArrayList<String> getListOfMissingIndicesTestSet()
    {
        ArrayList<String> listOfMissingIndices = new ArrayList<String>();

        ArrayList<String> completeList = getTestSet();

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

    public Map<String, ImageProfile> getHashTable()
    {
        return dataSource.getHashTable();
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
        return Uri.decode(Uri.fromFile(new File(searchResultFilePathStrings.get(index))).toString());
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

    public void notifyAdapter()
    {
        if (searchResultsAdapter != null)
            searchResultsAdapter.notifyDataSetChanged();
    }

    public void attachSearchResultsAdapter(SearchResultsAdapter searchResultsAdapter)
    {
        this.searchResultsAdapter = searchResultsAdapter;
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

}
