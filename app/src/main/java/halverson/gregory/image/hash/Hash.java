package halverson.gregory.image.hash;

import java.util.HashMap;

/**
 * Created by Gregory on 2015-03-31.
 */
public class Hash
{
    private int upper;
    private int lower;

    Hash()
    {
        this.upper = 0;
        this.lower = 0;
    }

    public Hash(String hex)
    {
        parseHex(hex);
    }

    public static int hammingDistance(Hash left, Hash right)
    {
        return hammingWeight(left.upper ^ right.upper) + hammingWeight(left.lower ^ right.lower);
    }

    public static int hammingDistanceFromIntegers(int left, int right)
    {
        return hammingWeight(left ^ right);
    }

    public static int hammingDistanceFromStrings(String left, String right)
    {
        int hammingDistance = 0;

        for (int i = 0; i < 16; i++)
        {
            int leftInteger = hexValues.get(left.charAt(i));
            int rightInteger = hexValues.get(right.charAt(i));

            hammingDistance += hammingDistanceFromIntegers(leftInteger, rightInteger);
        }

        return hammingDistance;
    }

    private static int hammingWeight(int value)
    {
        value = value - ((value >> 1) & 0x55555555);
        value = (value & 0x33333333) + ((value >> 2) & 0x33333333);
        return (((value + (value >> 4)) & 0x0F0F0F0F) * 0x01010101) >> 24;
    }

    public int hammingWeight()
    {
        return hammingWeight(upper) + hammingWeight(lower);
    }

    public void parseHex(String hex)
    {
        if (hex.length() != 16)
            return;

        String upperHex = hex.substring(0, 8);
        String lowerHex = hex.substring(8, 16);

        this.upper = (int)Long.parseLong(upperHex, 16);
        this.lower = (int)Long.parseLong(lowerHex, 16);
    }

    public void fromMatrix(int x, int y, boolean value)
    {
        int bit = value ? 1 : 0;
        int bitIndex = y * 8 + x;
        setBitFromLeft(bitIndex, bit);
    }

    public void setBitFromLeft(int bitIndexFromLeft, int value)
    {
        // Upper 0-31
        // Lower 32-63
        if (bitIndexFromLeft < 32)
        {
            int bitIndex = 31 - bitIndexFromLeft;
            int shifted = value << bitIndex;
            int bitMask = ~(1 << bitIndex);
            upper &= bitMask;
            upper |= shifted;
        }
        else
        {
            int bitIndex = 63 - bitIndexFromLeft;
            int shifted = value << bitIndex;
            int bitMask = ~(1 << bitIndex);
            lower &= bitMask;
            lower |= shifted;
        }
    }

    public void setBitFromLeft(int bitIndexFromLeft)
    {
        setBitFromLeft(bitIndexFromLeft, 1);
    }

    public void clearBitFromLeft(int bitIndexFromLeft)
    {
        setBitFromLeft(bitIndexFromLeft, 0);
    }

    public void setByteFromLeft(int byteIndexFromLeft, int value)
    {
        // Upper 0-3
        // Lower 4-7
        if (byteIndexFromLeft < 4)
        {
            int byteIndex = 3 - byteIndexFromLeft;
            int bitIndex = byteIndex * 8;
            int shifted = value << bitIndex;
            int bitMask = ~(1 << bitIndex);
            upper &= bitMask;
            upper |= shifted;
        }
        else
        {
            int byteIndex = 7 - byteIndexFromLeft;
            int bitIndex = byteIndex * 8;
            int shifted = value << bitIndex;
            int bitMask = ~(1 << bitIndex);
            lower &= bitMask;
            lower |= shifted;
        }
    }

    public long toLong()
    {
        return ((long)upper << 32) | ((long)lower & 0xFFFFFFFL);
    }

    public static String paddedIntBinaryString(int value)
    {
        String padding = "00000000000000000000000000000000";
        String binary = padding + Long.toBinaryString(value);
        return binary.substring(binary.length() - 32, binary.length());
    }

    public static String padddedIntHexString(int value)
    {
        String padding = "00000000";
        String binary = padding + Long.toHexString(value);
        return binary.substring(binary.length() - 8, binary.length());
    }

    public String toBinaryString()
    {
        return paddedIntBinaryString(this.upper) + paddedIntBinaryString(this.lower);
    }

    public String toHexString()
    {
        return padddedIntHexString(this.upper) + padddedIntHexString(lower);
    }

    public String toString()
    {
        return toHexString();
    }

    static HashMap<Character, Byte> hexValues = new HashMap<Character, Byte>() {{
        put( Character.valueOf( '0' ), Byte.valueOf( (byte )0 ));
        put( Character.valueOf( '1' ), Byte.valueOf( (byte )1 ));
        put( Character.valueOf( '2' ), Byte.valueOf( (byte )2 ));
        put( Character.valueOf( '3' ), Byte.valueOf( (byte )3 ));
        put( Character.valueOf( '4' ), Byte.valueOf( (byte )4 ));
        put( Character.valueOf( '5' ), Byte.valueOf( (byte )5 ));
        put( Character.valueOf( '6' ), Byte.valueOf( (byte )6 ));
        put( Character.valueOf( '7' ), Byte.valueOf( (byte )7 ));
        put( Character.valueOf( '8' ), Byte.valueOf( (byte )8 ));
        put( Character.valueOf( '9' ), Byte.valueOf( (byte )9 ));
        put( Character.valueOf( 'a' ), Byte.valueOf( (byte )10 ));
        put( Character.valueOf( 'b' ), Byte.valueOf( (byte )11 ));
        put( Character.valueOf( 'c' ), Byte.valueOf( (byte )12 ));
        put( Character.valueOf( 'd' ), Byte.valueOf( (byte )13 ));
        put( Character.valueOf( 'e' ), Byte.valueOf( (byte )14 ));
        put( Character.valueOf( 'f' ), Byte.valueOf( (byte )15 ));
        put( Character.valueOf( 'A' ), Byte.valueOf( (byte )10 ));
        put( Character.valueOf( 'B' ), Byte.valueOf( (byte )11 ));
        put( Character.valueOf( 'C' ), Byte.valueOf( (byte )12 ));
        put( Character.valueOf( 'D' ), Byte.valueOf( (byte )13 ));
        put( Character.valueOf( 'E' ), Byte.valueOf( (byte )14 ));
        put( Character.valueOf( 'F' ), Byte.valueOf( (byte )15 ));
    }};
}