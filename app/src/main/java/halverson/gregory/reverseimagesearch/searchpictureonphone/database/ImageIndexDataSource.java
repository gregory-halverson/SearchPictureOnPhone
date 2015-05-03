package halverson.gregory.reverseimagesearch.searchpictureonphone.database;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import halverson.gregory.image.hash.Hash;
import halverson.gregory.reverseimagesearch.searchpictureonphone.fragment.SearchOnPhoneWaitingFragment;

public class ImageIndexDataSource
{
    // Logcat tag
    private final static String TAG = "ImageIndexDataSource";

    // Activity pointer
    Activity activity;

    // Database fields

    private SQLiteDatabase database;
    private ImageIndexSQLiteHelper dbHelper;

    private String[] allColumns =
    {
            ImageIndexSQLiteHelper.ID_COLUMN_NAME,
            ImageIndexSQLiteHelper.IMAGE_PATH_COLUMN_NAME,
            ImageIndexSQLiteHelper.AVERAGE_HASH_UPPER_COLUMN_NAME,
            ImageIndexSQLiteHelper.AVERAGE_HASH_LOWER_COLUMN_NAME,
            ImageIndexSQLiteHelper.FILE_MODIFIED_DATE_COLUMN_NAME
    };

    public ImageIndexDataSource(Activity activity)
    {
        this.activity = activity;
        dbHelper = new ImageIndexSQLiteHelper(activity);
    }

    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long createEntry(String imagePathString, Hash averageHash, Long fileModifiedDate)
    {
        // Generate query
        ContentValues values = new ContentValues();
        values.put(ImageIndexSQLiteHelper.IMAGE_PATH_COLUMN_NAME, "\'" + imagePathString.replace("\'", "\'\'") + "\'");
        values.put(ImageIndexSQLiteHelper.AVERAGE_HASH_UPPER_COLUMN_NAME, averageHash.getUpper());
        values.put(ImageIndexSQLiteHelper.AVERAGE_HASH_LOWER_COLUMN_NAME, averageHash.getLower());
        values.put(ImageIndexSQLiteHelper.FILE_MODIFIED_DATE_COLUMN_NAME, fileModifiedDate);

        //Log.d(TAG, "values " + values.toString());

        // Execute query and return id number
        return database.insert(ImageIndexSQLiteHelper.TABLE_NAME, null, values);
    }

    // Delete entry by id
    public void deleteEntry(int id)
    {
        database.delete(ImageIndexSQLiteHelper.TABLE_NAME, ImageIndexSQLiteHelper.ID_COLUMN_NAME + " = " + id, null);
        Log.d(TAG, "Hash entry deleted with id: " + id);
    }

    // Returns true if entry containing image uri string exists, false if not
    public boolean entryExists(String imageFilePath, Long fileModifiedDate)
    {
        // Generate query
        String imagePathQuery = ImageIndexSQLiteHelper.IMAGE_PATH_COLUMN_NAME + "=\'" + imageFilePath.replace("\'", "\'\'") + "\'";
        String fileDateModifiedQuery = ImageIndexSQLiteHelper.FILE_MODIFIED_DATE_COLUMN_NAME +  "=" + fileModifiedDate;
        String whereQuery = imagePathQuery + " AND " + fileDateModifiedQuery;

        //Log.d(TAG, "checking where " + whereQuery);

        // Execute query
        Cursor cursor = database.query(
                ImageIndexSQLiteHelper.TABLE_NAME,
                allColumns,
                whereQuery,
                null, null, null, null);

        // Check if cursor is valid
        boolean result = cursor.moveToFirst();

        // Close cursor
        cursor.close();

        // Return validity of cursor as boolean
        return result;
    }

    public ImageProfile get(String imageFilePath)
    {
        // Generate query
        File file = new File(imageFilePath);
        Long fileModifiedDate = file.lastModified();
        String imagePathQuery = ImageIndexSQLiteHelper.IMAGE_PATH_COLUMN_NAME + "=\'" + imageFilePath.replace("\'", "\'\'") + "\'";
        String fileDateModifiedQuery = ImageIndexSQLiteHelper.FILE_MODIFIED_DATE_COLUMN_NAME +  "=" + fileModifiedDate;
        String whereQuery = imagePathQuery + " AND " + fileDateModifiedQuery;

        //Log.d(TAG, "checking where " + whereQuery);

        // Execute query
        Cursor cursor = database.query(
                ImageIndexSQLiteHelper.TABLE_NAME,
                allColumns,
                whereQuery,
                null, null, null, null);

        // Check if cursor is valid
        if (!cursor.moveToFirst())
            return null;

        // Compile hash
        ImageProfile record = getProfileFromCursor(cursor);

        // Always close the cursor when you're done with the database
        cursor.close();

        // Return hash object
        return record;
    }

    // Returns hash object from records associated with given file path
    public Hash getHash(String imageFilePath)
    {
        // Generate query
        File file = new File(imageFilePath);
        Long fileModifiedDate = file.lastModified();
        String imagePathQuery = ImageIndexSQLiteHelper.IMAGE_PATH_COLUMN_NAME + "=\'" + imageFilePath.replace("\'", "\'\'") + "\'";
        String fileDateModifiedQuery = ImageIndexSQLiteHelper.FILE_MODIFIED_DATE_COLUMN_NAME +  "=" + fileModifiedDate;
        String whereQuery = imagePathQuery + " AND " + fileDateModifiedQuery;

        //Log.d(TAG, "checking where " + whereQuery);

        // Execute query
        Cursor cursor = database.query(
                ImageIndexSQLiteHelper.TABLE_NAME,
                allColumns,
                whereQuery,
                null, null, null, null);

        // Check if cursor is valid
        if (!cursor.moveToFirst())
            return null;

        // Compile hash
        Hash hash = new Hash(getAverageHashUpperFromCursor(cursor), getAverageHashLowerFromCursor(cursor));

        // Always close the cursor when you're done with the database
        cursor.close();

        // Return hash object
        return hash;
    }

    // Returns a map associating file paths with hash objects for all records in database
    public Map<String, ImageProfile> getHashTable()
    {
        // Hash table to store queried records
        Map<String, ImageProfile> hashTable = new HashMap<String, ImageProfile>();

        // Query all records into cursor
        Cursor cursor = database.query(ImageIndexSQLiteHelper.TABLE_NAME, allColumns, null, null, null, null, null);

        // Move to beginning of cursor
        cursor.moveToFirst();

        // Check if cursor is still valid
        while (!cursor.isAfterLast())
        {
            // Load hash into table
            hashTable.put(getFilePathFromCursor(cursor), getProfileFromCursor(cursor));

            // Increment cursor to next record
            cursor.moveToNext();
        }

        // Make sure to close the cursor
        cursor.close();

        // Return hash table
        return hashTable;
    }

    public long getEntryCount()
    {
        return DatabaseUtils.queryNumEntries(database, ImageIndexSQLiteHelper.TABLE_NAME);
    }

    // Cursor attribute getters

    private static long getIdFromCursor(Cursor cursor)
    {
        return cursor.getLong(ImageIndexSQLiteHelper.ID_COLUMN_NUMBER);
    }

    private static String getFilePathFromCursor(Cursor cursor)
    {
        return cursor.getString(ImageIndexSQLiteHelper.IMAGE_PATH_COLUMN_NUMBER);
    }

    private static int getAverageHashUpperFromCursor(Cursor cursor)
    {
        return cursor.getInt(ImageIndexSQLiteHelper.AVERAGE_HASH_UPPER_COLUMN_NUMBER);
    }

    private static int getAverageHashLowerFromCursor(Cursor cursor)
    {
        return cursor.getInt(ImageIndexSQLiteHelper.AVERAGE_HASH_LOWER_COLUMN_NUMBER);
    }

    private static long getFileModifiedDateFromCursor(Cursor cursor)
    {
        return cursor.getLong(ImageIndexSQLiteHelper.FILE_MODIFIED_DATE_COLUMN_NUMBER);
    }

    private static ImageProfile getProfileFromCursor(Cursor cursor)
    {
        return new ImageProfile(
                getIdFromCursor(cursor),
                new Hash(
                        getAverageHashUpperFromCursor(cursor),
                        getAverageHashLowerFromCursor(cursor)),
                getFileModifiedDateFromCursor(cursor));
    }
}