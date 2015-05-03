package halverson.gregory.reverseimagesearch.searchpictureonphone.database;

import java.math.BigInteger;

class UnsignedLongOutput {
    public static void main(String[] args) {
        for (String arg : args)
            System.out.println(toUnsignedString(Long.parseLong(arg)));
    }

    private static final BigInteger B64 = BigInteger.ZERO.setBit(64);
    public static String toUnsignedString(long num) {
        if (num >= 0)
            return String.valueOf(num);
        return BigInteger.valueOf(num).add(B64).toString();
    }
}