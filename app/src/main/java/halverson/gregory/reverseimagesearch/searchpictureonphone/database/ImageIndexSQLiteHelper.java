package halverson.gregory.reverseimagesearch.searchpictureonphone.database;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ImageIndexSQLiteHelper extends SQLiteOpenHelper
{
    // Logcat tag
    public static final String TAG = "ImageIndexSQLiteHelper";

    // Table name
    public static final String TABLE_NAME = "hash_table";

    // Column identifiers
    public static final String ID_COLUMN_NAME = "_id";
    public static final int ID_COLUMN_NUMBER = 0;
    public static final String IMAGE_PATH_COLUMN_NAME = "file_path";
    public static final int IMAGE_PATH_COLUMN_NUMBER = 1;
    public static final String AVERAGE_HASH_UPPER_COLUMN_NAME = "average_hash_upper";
    public static final int AVERAGE_HASH_UPPER_COLUMN_NUMBER = 2;
    public static final String AVERAGE_HASH_LOWER_COLUMN_NAME = "average_hash_lower";
    public static final int AVERAGE_HASH_LOWER_COLUMN_NUMBER = 3;
    public static final String FILE_MODIFIED_DATE_COLUMN_NAME = "file_modified_date";
    public static final int FILE_MODIFIED_DATE_COLUMN_NUMBER = 4;

    // Database name and version
    private static final String DATABASE_NAME = "imageindex.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE =
            "create table if not exists " + TABLE_NAME
            + " ("
                    + ID_COLUMN_NAME + " integer primary key autoincrement not null unique, "
                    + IMAGE_PATH_COLUMN_NAME + " text not null unique on conflict replace, "
                    + AVERAGE_HASH_UPPER_COLUMN_NAME + " integer not null, "
                    + AVERAGE_HASH_LOWER_COLUMN_NAME + " integer not null, "
                    + FILE_MODIFIED_DATE_COLUMN_NAME + " integer not null"
            + ");";

    public ImageIndexSQLiteHelper(Activity activity)
    {
        super(activity, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        Log.d(TAG, "Creating database in SQL: " + DATABASE_CREATE);
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(ImageIndexSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}