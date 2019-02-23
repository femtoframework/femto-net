package org.femtoframework.lang;

/**
 * 格式化类
 *
 * @author fengyun
 * @version 1.00 2005-2-9 10:53:08
 */
class OctetFormat
{
    /**
     * Array of chars to lookup the byte for the digit in the tenth's
     * place for a two digit, base ten number.  The byte can be got by
     * using the number as the index.
     */
    private static final byte[] DIGIT_TENS = {
        '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
        '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
        '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
        '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
        '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
        '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
        '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
        '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
        '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
        '9', '9', '9', '9', '9', '9', '9', '9', '9', '9'
    };

    /**
     * Array of chars to lookup the byte for the digit in the unit's
     * place for a two digit, base ten number.  The byte can be got by
     * using the number as the index.
     */
    private static final byte[] DIGIT_ONES = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

//    /**
//     * 数字值
//     */
//    public static OctetBuffer append(OctetBuffer ob, byte b)
//    {
//        return append(ob, (int)b);
//    }
//
//    public static OctetBuffer append(OctetBuffer ob, short s)
//    {
//        return append(ob, (int)s);
//    }

    public static final byte BYTE_ZERO = '0';
    public static final char CHAR_ZERO = '0';
    public static final byte BYTE_ONE = '1';
    public static final char CHAR_ONE = '1';
    public static final byte BYTE_TWO = '2';
    public static final char CHAR_TWO = '2';
    public static final byte BYTE_THREE = '3';
    public static final char CHAR_THREE = '3';
    public static final byte BYTE_FOUR = '4';
    public static final char CHAR_FOUR = '4';
    public static final byte BYTE_FIVE = '5';
    public static final char CHAR_FIVE = '5';
    public static final byte BYTE_SIX = '6';
    public static final char CHAR_SIX = '6';
    public static final byte BYTE_SEVEN = '7';
    public static final char CHAR_SEVEN = '7';
    public static final byte BYTE_EIGHT = '8';
    public static final char CHAR_EIGHT = '8';
    public static final byte BYTE_NINE = '9';
    public static final char CHAR_NINE = '9';

    private static final byte[] INT_MIN_VALUE = "-2147483648".getBytes();
    private static final OctetString OS_INT_MIN_VALUE = new OctetString(INT_MIN_VALUE);

    private static final byte[] NEG_ONE = "-1".getBytes();
    private static final byte[] NEG_TWO = "-2".getBytes();
    private static final byte[] NEG_THREE = "-3".getBytes();

    private static final OctetString OS_NEG_ONE = new OctetString(NEG_ONE);
    private static final OctetString OS_NEG_TWO = new OctetString(NEG_TWO);
    private static final OctetString OS_NEG_THREE = new OctetString(NEG_THREE);

    private static final OctetString OS_ZERO = new OctetString(new byte[]{BYTE_ZERO});
    private static final OctetString OS_ONE = new OctetString(new byte[]{BYTE_ONE});
    private static final OctetString OS_TWO = new OctetString(new byte[]{BYTE_TWO});
    private static final OctetString OS_THREE = new OctetString(new byte[]{BYTE_THREE});
    private static final OctetString OS_FOUR = new OctetString(new byte[]{BYTE_FOUR});
    private static final OctetString OS_FIVE = new OctetString(new byte[]{BYTE_FIVE});
    private static final OctetString OS_SIX = new OctetString(new byte[]{BYTE_SIX});
    private static final OctetString OS_SEVEN = new OctetString(new byte[]{BYTE_SEVEN});
    private static final OctetString OS_EIGHT = new OctetString(new byte[]{BYTE_EIGHT});
    private static final OctetString OS_NINE = new OctetString(new byte[]{BYTE_NINE});
    private static final OctetString OS_TEN = new OctetString(new byte[]{BYTE_ONE, BYTE_ZERO});

    private static ThreadLocal perThreadBuffer = new ThreadLocal()
    {
        protected synchronized Object initialValue()
        {
            return new byte[24];
        }
    };

    /**
     * 追加整数
     *
     * @param ob OctetBuffer
     * @param i  整数
     * @return
     */
    static OctetBuffer append(OctetBuffer ob, int i)
    {
        switch (i) {
            case Integer.MIN_VALUE:
                ob.append(INT_MIN_VALUE);
                return ob;
            case -3:
                ob.append(NEG_THREE);
                return ob;
            case -2:
                ob.append(NEG_TWO);
                return ob;
            case -1:
                ob.append(NEG_ONE);
                return ob;
            case 0:
                ob.append(BYTE_ZERO);
                return ob;
            case 1:
                ob.append(BYTE_ONE);
                return ob;
            case 2:
                ob.append(BYTE_TWO);
                return ob;
            case 3:
                ob.append(BYTE_THREE);
                return ob;
            case 4:
                ob.append(BYTE_FOUR);
                return ob;
            case 5:
                ob.append(BYTE_FIVE);
                return ob;
            case 6:
                ob.append(BYTE_SIX);
                return ob;
            case 7:
                ob.append(BYTE_SEVEN);
                return ob;
            case 8:
                ob.append(BYTE_EIGHT);
                return ob;
            case 9:
                ob.append(BYTE_NINE);
                return ob;
            case 10:
                ob.append(OS_TEN);
                return ob;
        }
        byte[] buf = (byte[])(perThreadBuffer.get());
        int charPos = getBytes(i, buf);
        ob.append(buf, charPos, buf.length - charPos);
        return ob;
    }

    private static final byte[] LONG_MIN_VALUE = "-9223372036854775808".getBytes();
    private static final OctetString OS_LONG_MIN_VALUE = new OctetString(LONG_MIN_VALUE);

    public static OctetBuffer append(OctetBuffer ob, long l)
    {
        if (l == Long.MIN_VALUE) {
            ob.append(LONG_MIN_VALUE);
            return ob;
        }
        byte[] buf = (byte[])(perThreadBuffer.get());
        int charPos = getBytes(l, buf);
        ob.append(buf, charPos, (buf.length - charPos));
        return ob;
    }

    private static int getBytes(long i, byte[] buf)
    {
        long q;
        int r;
        int charPos = buf.length;
        byte sign = 0;

        if (i < 0) {
            sign = '-';
            i = -i;
        }

        // Get 2 digits/iteration using longs until quotient fits into an int
        while (i > Integer.MAX_VALUE) {
            q = i / 100;
            // really: r = i - (q * 100);
            r = (int)(i - ((q << 6) + (q << 5) + (q << 2)));
            i = q;
            buf[--charPos] = DIGIT_ONES[r];
            buf[--charPos] = DIGIT_TENS[r];
        }

        // Get 2 digits/iteration using ints
        int q2;
        int i2 = (int)i;
        while (i2 >= 65536) {
            q2 = i2 / 100;
            // really: r = i2 - (q * 100);
            r = i2 - ((q2 << 6) + (q2 << 5) + (q2 << 2));
            i2 = q2;
            buf[--charPos] = DIGIT_ONES[r];
            buf[--charPos] = DIGIT_TENS[r];
        }

        // Fall thru to fast mode for smaller numbers
        // assert(i2 <= 65536, i2);
        for (; ;) {
            q2 = (i2 * 52429) >>> (16 + 3);
            r = i2 - ((q2 << 3) + (q2 << 1));  // r = i2-(q2*10) ...
            buf[--charPos] = DIGITS[r];
            i2 = q2;
            if (i2 == 0) {
                break;
            }
        }
        if (sign != 0) {
            buf[--charPos] = sign;
        }
        return charPos;
    }

    /**
     * All possible chars for representing a number as a String
     */
    static final byte[] DIGITS = {
        '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'a', 'b',
        'c', 'd', 'e', 'f', 'g', 'h',
        'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't',
        'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static int getBytes(int i, byte[] buf)
    {
        int q, r;
        int charPos = buf.length;
        byte sign = 0;

        if (i < 0) {
            sign = '-';
            i = -i;
        }

        // Generate two digits per iteration
        while (i >= 65536) {
            q = i / 100;
            // really: r = i - (q * 100);
            r = i - ((q << 6) + (q << 5) + (q << 2));
            i = q;
            buf[--charPos] = DIGIT_ONES[r];
            buf[--charPos] = DIGIT_TENS[r];
        }

        // Fall thru to fast mode for smaller numbers
        // assert(i <= 65536, i);
        for (; ;) {
            q = (i * 52429) >>> (16 + 3);
            r = i - ((q << 3) + (q << 1));  // r = i-(q*10) ...
            buf[--charPos] = DIGITS[r];
            i = q;
            if (i == 0) {
                break;
            }
        }
        if (sign != 0) {
            buf[--charPos] = sign;
        }
        return charPos;
    }

    public static OctetBuffer append(OctetBuffer ob,
                                     float f,
                                     int fraction)
    {
        int i = (int)f;
        ob = append(ob, i);
        if (fraction == 0) {
            return ob;
        }
        double d = f - i;
        return append0(ob, d, fraction);
    }

    /**
     *
     */
    public static OctetBuffer append(OctetBuffer ob,
                                     double d,
                                     int fraction)
    {
        long l = (long)d;
        ob = append(ob, l);
        if (fraction == 0) {
            return ob;
        }

        double f = d - l;
        return append0(ob, f, fraction);
    }

    private static OctetBuffer append0(OctetBuffer ob,
                                       double d,
                                       int fraction)
    {
        if (fraction > 22) {
            throw new IllegalArgumentException("Can't format with so long number after fraction:" + fraction);
        }
        byte[] buf = (byte[])(perThreadBuffer.get());
        buf[0] = (byte)'.';
        int charPos = 1;
        do {
            d = d * 100;
            int digit = (int)d;
            buf[charPos++] = DIGIT_TENS[digit];
            buf[charPos++] = DIGIT_ONES[digit];
            d = d - digit;
        }
        while (charPos <= fraction);
        ob.append(buf, 0, fraction + 1);
        return ob;
    }


    public static OctetString toOctetString(int i)
    {
        switch (i) {
            case Integer.MIN_VALUE:
                return OS_INT_MIN_VALUE;
            case -3:
                return OS_NEG_THREE;
            case -2:
                return OS_NEG_TWO;
            case -1:
                return OS_NEG_ONE;
            case 0:
                return OS_ZERO;
            case 1:
                return OS_ONE;
            case 2:
                return OS_TWO;
            case 3:
                return OS_THREE;
            case 4:
                return OS_FOUR;
            case 5:
                return OS_FIVE;
            case 6:
                return OS_SIX;
            case 7:
                return OS_SEVEN;
            case 8:
                return OS_EIGHT;
            case 9:
                return OS_NINE;
            case 10:
                return OS_TEN;
        }
        byte[] buf = (byte[])(perThreadBuffer.get());
        int charPos = getBytes(i, buf);
        return new OctetString(buf, charPos, buf.length - charPos);
    }

    public static OctetString toOctetString(long l)
    {
        if (l == Long.MIN_VALUE) {
            return OS_LONG_MIN_VALUE;
        }
        byte[] buf = (byte[])(perThreadBuffer.get());
        int charPos = getBytes(l, buf);
        return new OctetString(buf, charPos, (buf.length - charPos));
    }

    /**
     * 保留2位小数
     */
    public static OctetString toOctetString(float f)
    {
        return toOctetString(f, 2);
    }

    public static OctetString toOctetString(float f, int fraction)
    {
        if (fraction < 0) {
            fraction = 0;
        }
        OctetBuffer ob = new OctetBuffer(13 + fraction);
        append(ob, f, fraction);
        return ob.toOctetString();
    }

    /**
     * 保留4位小数
     */
    public static OctetString toOctetString(double d)
    {
        return toOctetString(d, 4);
    }

    public static OctetString toOctetString(double d, int fraction)
    {
        if (fraction < 0) {
            fraction = 0;
        }
        OctetBuffer ob = new OctetBuffer(21 + fraction);
        append(ob, d, fraction);
        return ob.toOctetString();
    }
}
