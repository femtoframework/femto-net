package org.femtoframework.lang;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 类适于Byte
 * 处理8字节字符(0-255)
 */
public class Octet extends Number
    implements Comparable
{

    public static final int MIN_RADIX = 2;

    public static final int MAX_RADIX = 16;

    public static final int MIN_VALUE = 0;

    public static final int MAX_VALUE = 255;

    private static final int[] TYPE_TABLE = new int[256];

    public static final int UPPERCASE_LETTER = 1
    ,
    LOWERCASE_LETTER = 2
    ,
    DECIMAL_DIGIT_NUMBER = 9
    ,
    OTHER_NUMBER = 11
    ,
    SPACE_SEPARATOR = 12
    ,
    CONTROL = 15
    ,
    DASH_PUNCTUATION = 20
    ,
    START_PUNCTUATION = 21
    ,
    END_PUNCTUATION = 22
    ,
    CONNECTOR_PUNCTUATION = 23
    ,
    OTHER_PUNCTUATION = 24
    ,
    MATH_SYMBOL = 25
    ,
    CURRENCY_SYMBOL = 26
    ,
    MODIFIER_SYMBOL = 27
    ,
    OTHER_SYMBOL = 28;

    /*
     * Character translation tables.
     */

    private static final char[] UPPER = new char[256];
    private static final char[] LOWER = new char[256];
    private static final int[] DIGIT = new int[256];

    /*
     * Character type tables.
     */
    private static final boolean[] IS_LETTER = new boolean[256];
    private static final boolean[] IS_UPPER = new boolean[256];
    private static final boolean[] IS_LOWER = new boolean[256];
    private static final boolean[] IS_WHITE = new boolean[256];
    private static final boolean[] IS_DIGIT = new boolean[256];

    /*
     * Initialize character translation and type tables.
     */

    static {
        char c;
        for (int i = 0; i < 256; i++) {
            c = (char)i;
            TYPE_TABLE[i] = Character.getType(c);
            UPPER[i] = Character.toUpperCase(c);
            LOWER[i] = Character.toLowerCase(c);
            DIGIT[i] = Character.digit(c, 16);
            IS_DIGIT[i] = Character.isDigit(c);
            IS_LETTER[i] = Character.isLetter(c);
            IS_LOWER[i] = Character.isLowerCase(c);
            IS_UPPER[i] = Character.isUpperCase(c);
            IS_WHITE[i] = Character.isWhitespace(c);
        }
    }

    private int value;

    /**
     * 构造
     *
     * @param value Octet值
     */
    public Octet(byte value)
    {
        this((int)value & 0xFF);
    }

    /**
     * 构造
     *
     * @param value 整数
     */
    public Octet(int value)
    {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("Invalid octet:" + value);
        }
        this.value = value;
    }

    /**
     * Returns the value of this <code>Byte</code> as a
     * <code>byte</code>.
     */
    public byte byteValue()
    {
        return (byte)value;
    }

    /**
     * Returns the value of this <code>Byte</code> as a
     * <code>short</code>.
     */
    public short shortValue()
    {
        return (short)value;
    }

    /**
     * Returns the value of this <code>Byte</code> as an
     * <code>int</code>.
     */
    public int intValue()
    {
        return value;
    }

    /**
     * Returns the value of this <code>Byte</code> as a
     * <code>long</code>.
     */
    public long longValue()
    {
        return (long)value;
    }

    /**
     * Returns the value of this <code>Byte</code> as a
     * <code>float</code>.
     */
    public float floatValue()
    {
        return (float)value;
    }

    /**
     * Returns the value of this <code>Byte</code> as a
     * <code>double</code>.
     */
    public double doubleValue()
    {
        return (double)value;
    }

    public int hashCode()
    {
        return intValue();
    }

    public boolean equals(Object obj)
    {
        return obj instanceof Octet && value == ((Octet)obj).value;
    }

    public static boolean equals(byte b1, byte b2)
    {
        return b1 == b2;
    }

    public static boolean equalsIgnoreCase(byte b1, byte b2)
    {
        return b1 == b2 || toLower(b1) == toLower(b2);
    }

    /**
     * 字符形式
     */
    public String toString()
    {
        return String.valueOf(toChar());
    }

    public static boolean isLowerCase(byte b)
    {
        return isLower((int)b & 0xFF);
    }

    public static boolean isUpperCase(byte b)
    {
        return isUpper((int)b & 0xFF);
    }

    public static boolean isDigit(byte b)
    {
        return isDigit((int)b & 0xFF);
    }

    public static boolean isLetter(byte b)
    {
        return isLetter((int)b & 0xFF);
    }

    public static boolean isLetter(int ch)
    {
        return IS_LETTER[ch & 0xFF];
    }

    public static boolean isLetterOrDigit(byte b)
    {
        return isLetterOrDigit((int)b & 0xFF);
    }

    public static boolean isLetterOrDigit(int ch)
    {
        return isLetter(ch) || isDigit(ch);
    }

    public static byte toLowerCase(byte b)
    {
        return (byte)toLower((int)b & 0xFF);
    }

    public static byte toUpperCase(byte b)
    {
        return (byte)toUpper((int)b & 0xFF);
    }

    /**
     * Returns the upper case equivalent of the specified ASCII character.
     */

    public static int toUpper(int c)
    {
        return UPPER[c & 0xFF];
    }

    /**
     * Returns the lower case equivalent of the specified ASCII character.
     */

    public static int toLower(int c)
    {
        return LOWER[c & 0xFF];
    }

    /**
     * Returns true if the specified ASCII character is upper or lower case.
     */

    public static boolean isAlpha(int ch)
    {
        return isLetter(ch);
    }

    /**
     * Returns true if the specified ASCII character is upper case.
     */

    public static boolean isUpper(int c)
    {
        return IS_UPPER[c & 0xFF];
    }

    /**
     * Returns true if the specified ASCII character is lower case.
     */

    public static boolean isLower(int c)
    {
        return IS_LOWER[c & 0xFF];
    }

    /**
     * Returns true if the specified ASCII character is white space.
     */

    public static boolean isWhite(int c)
    {
        return IS_WHITE[c & 0xFF];
    }

    /**
     * Returns true if the specified ASCII character is a digit.
     */

    public static boolean isDigit(int c)
    {
        return IS_DIGIT[c & 0xFF];
    }

    public static int digit(byte b)
    {
        return digit(b, 10);
    }

    public static int digit(byte b, int radix)
    {
        return digit((int)b & 0xFF, radix);
    }

    public static int digit(int i)
    {
        return digit(i, 10);
    }

    public static int digit(int i, int radix)
    {
        int value = DIGIT[i & 0xFF];
        return (value < radix) ? value : -1;
    }

    /**
     * Determines if the specified byte is white space according to Java.
     * A byte is considered to be a Java whitespace byte if and only
     * if it satisfies one of the following criteria:
     * <ul>
     * <li> It is 0X20, SPACE.
     * <li> It is 0XA0, SPACE.
     * </ul>
     */
    public static boolean isSpaceChar(byte b)
    {
        int i = (int)b & 0xFF;
        return i == 0x20 || i == 0xA0;
    }

    /**
     * <ul>
     * <li> It is 0X09, HORIZONTAL TABULATION.
     * <li> It is 0X0A, LINE FEED.
     * <li> It is 0X0B, VERTICAL TABULATION.
     * <li> It is 0X0C, FORM FEED.
     * <li> It is 0X0D, CARRIAGE RETURN.
     * <li> It is 0X1C, FILE SEPARATOR.
     * <li> It is 0X1D, GROUP SEPARATOR.
     * <li> It is 0X1E, RECORD SEPARATOR.
     * <li> It is 0X1F, UNIT SEPARATOR.
     * <li> It is 0X20, SPACE.
     * </ul>
     */
    public static boolean isWhitespace(byte b)
    {
        return isWhite((int)b & 0xFF);
    }

    public static boolean isISOControl(byte b)
    {
        int i = (int)b & 0xFF;
        return (i <= 0x9F) && ((i <= 0x1F) || (i >= 0x7F));
    }

    public static boolean needTrim(byte b)
    {
        return b >= 0 && b <= 0x20;
    }

    public static int getType(int b)
    {
        return TYPE_TABLE[b & 0xFF];
    }

    public int compareTo(Octet octet)
    {
        return this.value - octet.value;
    }

    public int compareTo(Object o)
    {
        return compareTo((Octet)o);
    }

    public char toChar()
    {
        return (char)value;
    }

    private void writeObject(ObjectOutputStream oos)
        throws IOException
    {
        oos.write(value);
    }

    private void readObject(ObjectInputStream ois)
        throws IOException
    {
        value = ois.readUnsignedByte();
    }
}
