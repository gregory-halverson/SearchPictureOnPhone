package halverson.gregory.image;

import android.graphics.Bitmap;

import halverson.gregory.image.hash.Hash;
import halverson.gregory.image.hash.ImageHash;

/**
 * Created by Gregory on 5/2/2015.
 */
public class AndroidCodec
{
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

    public static Hash hashFrom8by8Bitmap(Bitmap bitmap)
    {
        return ImageHash.Average.hashFromGray8by8Matrix(grayMatrixFrom8by8Bitmap(bitmap));
    }
}
