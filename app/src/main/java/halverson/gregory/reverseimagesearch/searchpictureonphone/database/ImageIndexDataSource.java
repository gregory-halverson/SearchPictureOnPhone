package halverson.gregory.reverseimagesearch.searchpictureonphone.database;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import halverson.gregory.image.hash.Hash;
import halverson.gregory.image.hash.ImageHash;
import halverson.gregory.reverseimagesearch.searchpictureonphone.fragment.SearchOnPhoneWaitingFragment;
import halverson.gregory.reverseimagesearch.searchpictureonphone.thread.SearchJob;

public class ImageIndexDataSource
{
    // Constants

    private final static String TAG = "ImageIndexDataSource";

    // Activity pointer
    Activity activity;
    SearchOnPhoneWaitingFragment waitingFragment;

    // Database fields

    private SQLiteDatabase database;
    private ImageIndexSQLiteHelper dbHelper;

    private String[] allColumns =
    {
            ImageIndexSQLiteHelper.ID_COLUMN,
            ImageIndexSQLiteHelper.IMAGE_PATH_COLUMN,
            ImageIndexSQLiteHelper.AVERAGE_HASH_COLUMN,
            ImageIndexSQLiteHelper.FILE_MODIFIED_DATE
    };

    public ImageIndexDataSource(Activity activity, SearchOnPhoneWaitingFragment waitingFragment)
    {
        this.activity = activity;
        this.waitingFragment = waitingFragment;
        dbHelper = new ImageIndexSQLiteHelper(activity);
    }

    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public ImageIndexEntry createEntry(String imagePathString, Hash averageHash, Long fileModifiedDate)
    {
        String averageHashString = averageHash.toHexString();

        //Log.d(TAG, "writing hash " + averageHashString + " for file " + imagePathString + " to database");

        ContentValues values = new ContentValues();
        values.put(ImageIndexSQLiteHelper.IMAGE_PATH_COLUMN, "\'" + imagePathString.replace("\'", "\'\'") + "\'");
        values.put(ImageIndexSQLiteHelper.AVERAGE_HASH_COLUMN, averageHashString);
        values.put(ImageIndexSQLiteHelper.FILE_MODIFIED_DATE, fileModifiedDate);

        //Log.d(TAG, "values " + values.toString());

        long insertId = database.insert(ImageIndexSQLiteHelper.TABLE_NAME, null, values);

        Cursor cursor = database.query( ImageIndexSQLiteHelper.TABLE_NAME,
                                        allColumns,
                                        ImageIndexSQLiteHelper.ID_COLUMN + " = " + insertId,
                                        null, null, null, null);
        cursor.moveToFirst();
        ImageIndexEntry newImageIndexEntry = entryFromCursor(cursor);
        cursor.close();
        return newImageIndexEntry;
    }

    public void deleteEntry(ImageIndexEntry imageIndexEntry)
    {
        long id = imageIndexEntry.getId();
        System.out.println("Hash entry deleted with id: " + id);
        database.delete(ImageIndexSQLiteHelper.TABLE_NAME, ImageIndexSQLiteHelper.ID_COLUMN
                + " = " + id, null);
    }

    // Returns true if entry containing image uri string exists, false if not
    public boolean entryExists(String imageFilePath, Long fileModifiedDate)
    {
        String imagePathQuery = ImageIndexSQLiteHelper.IMAGE_PATH_COLUMN + "=\'" + imageFilePath.replace("\'", "\'\'") + "\'";
        String fileDateModifiedQuery = ImageIndexSQLiteHelper.FILE_MODIFIED_DATE +  "=" + fileModifiedDate;
        String whereQuery = imagePathQuery + " AND " + fileDateModifiedQuery;

        //Log.d(TAG, "checking where " + whereQuery);

        Cursor cursor = database.query( ImageIndexSQLiteHelper.TABLE_NAME,
                allColumns,
                whereQuery,
                null, null, null, null);

        boolean result = cursor.moveToFirst();

        cursor.close();

        return result;
    }

    public Hash getHash(String imageFilePath)
    {
        File file = new File(imageFilePath);
        Long fileModifiedDate = file.lastModified();
        String imagePathQuery = ImageIndexSQLiteHelper.IMAGE_PATH_COLUMN + "=\'" + imageFilePath.replace("\'", "\'\'") + "\'";
        String fileDateModifiedQuery = ImageIndexSQLiteHelper.FILE_MODIFIED_DATE +  "=" + fileModifiedDate;
        String whereQuery = imagePathQuery + " AND " + fileDateModifiedQuery;

        //Log.d(TAG, "checking where " + whereQuery);

        Cursor cursor = database.query( ImageIndexSQLiteHelper.TABLE_NAME,
                allColumns,
                whereQuery,
                null, null, null, null);

        if (!cursor.moveToFirst())
            return null;

        //ImageIndexEntry imageIndexEntry = entryFromCursor(cursor);

        Hash hash = new Hash(cursor.getString(2));

        cursor.close();

        return hash;
    }

    public String getHashString(String imageFilePath)
    {
        File file = new File(imageFilePath);
        Long fileModifiedDate = file.lastModified();
        String imagePathQuery = ImageIndexSQLiteHelper.IMAGE_PATH_COLUMN + "=\'" + imageFilePath.replace("\'", "\'\'") + "\'";
        String fileDateModifiedQuery = ImageIndexSQLiteHelper.FILE_MODIFIED_DATE +  "=" + fileModifiedDate;
        String whereQuery = imagePathQuery + " AND " + fileDateModifiedQuery;

        //Log.d(TAG, "checking where " + whereQuery);

        Cursor cursor = database.query( ImageIndexSQLiteHelper.TABLE_NAME,
                allColumns,
                whereQuery,
                null, null, null, null);

        if (!cursor.moveToFirst())
            return null;

        //ImageIndexEntry imageIndexEntry = entryFromCursor(cursor);

        String hash = cursor.getString(2);

        cursor.close();

        return hash;
    }

    public List<ImageIndexEntry> getAllEntries()
    {
        List<ImageIndexEntry> hashEntries = new ArrayList<ImageIndexEntry>();

        Cursor cursor = database.query(ImageIndexSQLiteHelper.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            ImageIndexEntry imageIndexEntry = entryFromCursor(cursor);
            hashEntries.add(imageIndexEntry);
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();

        return hashEntries;
    }

    public Map<String, Hash> getHashTable()
    {
        Map<String, Hash> hashTable = new HashMap<String, Hash>();

        Cursor cursor = database.query(ImageIndexSQLiteHelper.TABLE_NAME, allColumns, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            //ImageIndexEntry imageIndexEntry = entryFromCursor(cursor);
            //hashEntries.add(imageIndexEntry);
            hashTable.put(cursor.getString(1), new Hash(cursor.getString(2)));
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();

        return hashTable;
    }

    public long getEntryCount()
    {
        return DatabaseUtils.queryNumEntries(database, ImageIndexSQLiteHelper.TABLE_NAME);
    }

    private ImageIndexEntry entryFromCursor(Cursor cursor)
    {
        ImageIndexEntry imageIndexEntry = new ImageIndexEntry();
        imageIndexEntry.setId(cursor.getLong(0));
        imageIndexEntry.setImageFilePath(cursor.getString(1));
        imageIndexEntry.setAverageHashHex(cursor.getString(2));
        imageIndexEntry.setFileModifiedDate(cursor.getLong(3));

        return imageIndexEntry;
    }
}