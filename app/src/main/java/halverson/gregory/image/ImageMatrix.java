package halverson.gregory.image;

/**
 * Created by Gregory on 5/2/2015.
 */
public class ImageMatrix
{
    public enum ColorMode { RGB, GRAY }

    // Image attributes
    private ColorMode colorMode;
    private int width;
    private int height;

    // Pixel data
    private Pixel[] pixels;

    public ImageMatrix(int width, int height, ColorMode colorMode)
    {
        this.width = width;
        this.height = height;
        this.colorMode = colorMode;
    }

    public ColorMode getColorMode()
    {
        return colorMode;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    // Stores integer pixel with methods for accessing colors
    public class Pixel
    {
        int pixel;

        public Pixel()
        {
            pixel = 0;
        }

        public Pixel(int pixel)
        {
            this.pixel = pixel;
        }

        public int getRed()
        {
            return (pixel & 0x00FF0000) >> 16;
        }

        public void setRed(int red)
        {
            pixel = (pixel & 0xFF00FFFF) | ((red << 16) & 0x00FF0000);
        }

        public int getGreen()
        {
            return (pixel & 0x0000FF00) >> 8;
        }

        public void setGreen(int green)
        {
            pixel = (pixel & 0xFFFF00FF) | ((green << 8) & 0x0000FF00);
        }

        public int getBlue()
        {
            return pixel & 0x000000FF;
        }

        public void setBlue(int blue)
        {
            pixel |= (blue & 0x000000FF);
        }

        public int toInteger()
        {
            return pixel;
        }

        public void fromInteger(int pixel)
        {
            this.pixel = pixel;
        }
    }
}
