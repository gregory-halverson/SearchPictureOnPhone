package halverson.gregory.reverseimagesearch.searchpictureonphone.database;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ImageIndexSQLiteHelper extends SQLiteOpenHelper
{
    public static final String TAG = "ImageIndexSQLiteHelper";

    public static final String TABLE_NAME = "hash_table";
    public static final String ID_COLUMN = "_id";
    public static final String IMAGE_PATH_COLUMN = "file_path";
    public static final String AVERAGE_HASH_COLUMN = "average_hash_hex";
    public static final String FILE_MODIFIED_DATE = "file_modified_date";

    private static final String DATABASE_NAME = "imageindex.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + " ("
                    + ID_COLUMN + " integer primary key autoincrement not null unique, "
                    + IMAGE_PATH_COLUMN + " text not null unique on conflict replace, "
                    + AVERAGE_HASH_COLUMN + " text not null, "
                    + FILE_MODIFIED_DATE + " integer not null"
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
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ImageIndexSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}