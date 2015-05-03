package halverson.gregory.image;

import android.graphics.Bitmap;
import android.net.Uri;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.File;

import halverson.gregory.image.hash.Hash;
import halverson.gregory.image.hash.ImageHash;

/**
 * Created by Gregory on 5/2/2015.
 */
public class AndroidCodec
{
    // Scaled image size
    public static final ImageSize AVERAGE_HASH_SCALED_BITMAP_SIZE = new ImageSize(8, 8);

    public static int[][] grayMatrixFrom8by8Bitmap(Bitmap bitmap)
    {
        int[][] matrix = new int[8][8];
        int[] pixelArray = new int[64];
        bitmap.getPixels(pixelArray, 0, 8, 0, 0, 8, 8);

        for (int i = 0; i < 64; i++)
        {
            int red = pixelArray[i] & 0x00FF0000 >> 16;
            int green = pixelArray[i] & 0x0000FF00 >> 8;
            int blue = pixelArray[i] & 0x000000FFF;
            int y = i / 8;
            int x = i % 8;
            matrix[y][x] = (red + green + blue) / 3;
        }

        return matrix;
    }

    public static Hash hashFromFilePathString(String filePathString)
    {
        return hashFromUriString(decodedUriStringFromFilePathString(filePathString));
    }

    public static Hash hashFromUriString(String uriString)
    {
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(uriString);
        Hash hash = ImageHash.Average.hashFromBitmap(bitmap);
        bitmap.recycle();
        bitmap = null;
        return hash;
    }

    public static Hash hashFromFilePathStringWithPrescaling(String filePathString)
    {
        return hashFromUriStringWithPrescaling(decodedUriStringFromFilePathString(filePathString));
    }

    public static Hash hashFromUriStringWithPrescaling(String uriString)
    {
        // Load bitmap scaled to 8 by 8
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(uriString, AVERAGE_HASH_SCALED_BITMAP_SIZE, null);

        if (bitmap == null)
            return null;

        // Return hash of 8 by 8 bitmap
        Hash hash = AndroidCodec.hashFrom8by8Bitmap(bitmap);

        // Recycle bitmap
        bitmap.recycle();
        bitmap = null;

        return hash;
    }

    public static Hash hashFrom8by8Bitmap(Bitmap bitmap)
    {
        return ImageHash.Average.hashFromGray8by8Matrix(grayMatrixFrom8by8Bitmap(bitmap));
    }

    public static String decodedUriStringFromFilePathString(String filePathString)
    {
        return Uri.decode(Uri.fromFile(new File(filePathString)).toString());
    }
}
