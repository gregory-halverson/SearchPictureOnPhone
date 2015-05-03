package halverson.gregory.image.hash;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Created by Gregory on 2015-04-01.
 */
public class ImageHash
{
    private static final String FILE_ENCODING = "UTF-8";
    private static final ReductionOrder REDUCTION_ORDER = ReductionOrder.GRAY_THEN_SCALE;

    public static enum Channel
    {
        LUMINOSITY,
        RED,
        GREEN,
        BLUE
    }

    private static enum ReductionOrder
    {
        SCALE_THEN_GRAY,
        GRAY_THEN_SCALE
    }

    /**
     * Created by Gregory on 2015-04-02.
     */
    public static class Average
    {
        public static Hash hashFromBitmap(Bitmap sourceImage)
        {
            return hashFromBitmap(sourceImage, Channel.LUMINOSITY);
        }

        public static Hash hashFromBitmap(Bitmap sourceImage, Channel channel)
        {
            Bitmap scaledImage = null;
            Bitmap grayImage = null;
            int imageMatrix[][];

            switch (channel)
            {
                case RED:
                    scaledImage = scaleImage(sourceImage, 8, 8);
                    imageMatrix = extractMatrix(scaledImage, Channel.RED);
                    break;

                case GREEN:
                    scaledImage = scaleImage(sourceImage, 8, 8);
                    imageMatrix = extractMatrix(scaledImage, Channel.GREEN);
                    break;

                case BLUE:
                    scaledImage = scaleImage(sourceImage, 8, 8);
                    imageMatrix = extractMatrix(scaledImage, Channel.BLUE);
                    break;

                default:
                case LUMINOSITY:
                    switch (REDUCTION_ORDER)
                    {
                        default:
                        case SCALE_THEN_GRAY:
                            scaledImage = scaleImage(sourceImage, 8, 8);
                            grayImage = reduceGrayImage(scaledImage);
                            imageMatrix = extractMatrix(grayImage);
                            break;
                        case GRAY_THEN_SCALE:
                            grayImage = reduceGrayImage(sourceImage);
                            scaledImage = scaleImage(grayImage, 8, 8);
                            imageMatrix = extractMatrix(scaledImage);
                            break;
                    }

                    break;
            }

            scaledImage.recycle();
            scaledImage = null;
            grayImage.recycle();
            grayImage = null;

            int average = calculateAverage(imageMatrix, 8);

            return compareAverage(imageMatrix, average, 8);
        }

        public static Hash hashFromGray8by8Matrix(int[][] imageMatrix)
        {
            int average = calculateAverage(imageMatrix, 8);

            return compareAverage(imageMatrix, average, 8);
        }
    }

    private static Bitmap scaleImage(Bitmap original, int width, int height)
    {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, width, height, true);

        return scaledBitmap;
    }

    private static Bitmap reduceGrayImage(Bitmap original)
    {
        int width, height;
        height = original.getHeight();
        width = original.getWidth();

        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(f);
        canvas.drawBitmap(original, 0, 0, paint);

        return grayBitmap;
    }

    private static int[][] extractMatrix(Bitmap Bitmap)
    {
        return extractMatrix(Bitmap, Channel.BLUE);
    }

    private static int[][] extractMatrix(Bitmap Bitmap, Channel channel)
    {
        int width = Bitmap.getWidth();
        int height = Bitmap.getHeight();
        int[][] imageMatrix = new int[height][width];
        int bitMask = 0xFF;

        switch (channel)
        {
            case RED:
                bitMask = 0xFF0000;
                break;

            case GREEN:
                bitMask = 0xFF00;
                break;

            case BLUE:
                bitMask = 0xFF;
                break;
        }

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                imageMatrix[y][x] = Bitmap.getPixel(x, y) & bitMask;

        return imageMatrix;
    }

    private static int calculateAverage(int[][] imageMatrix, int sizeBits)
    {
        int sum = 0;

        for (int y = 0; y < sizeBits; y++)
            for (int x = 0; x < sizeBits; x++)
                sum += imageMatrix[y][x];

        return (int)(sum / Math.pow(sizeBits, 2));
    }

    private static Hash compareAverage(int[][] imageMatrix, int average, int sizeBits)
    {
        Hash hash = new Hash();

        for (int y = 0; y < sizeBits; y++)
            for (int x = 0; x < sizeBits; x++)
                hash.fromMatrix(x, y, imageMatrix[y][x] > average);

        return hash;
    }
}
