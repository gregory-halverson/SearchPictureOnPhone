package halverson.gregory.reverseimagesearch.searchpictureonphone.database;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by Gregory on 5/1/2015.
 */
public class FileValidator
{
    public static long getLastModifiedDateFromUriString(String uriString)
    {
        return getLastModifiedDateFromFilePathString(Uri.parse(uriString).getPath());
    }

    public static long getLastModifiedDateFromFilePathString(String filePathString)
    {
        return new File(filePathString).lastModified();
    }

    public static boolean checkUri(String uriString, Activity activity)
    {
        ContentResolver cr = activity.getContentResolver();
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cur = cr.query(Uri.parse(uriString), projection, null, null, null);

        if(cur == null)
            return false;

        cur.moveToFirst();
        String filePath = cur.getString(0);

        return checkFilePath(filePath);
    }

    public static boolean checkFilePath(String filePath)
    {
        File file = new File(filePath);

        return file.exists();
    }
}
