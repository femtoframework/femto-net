package org.femtoframework.net.niep;

/**
 * 常量定义
 *
 * @author fengyun
 * @version 1.00 Jul 2, 2003 2:01:56 PM
 */
public interface NiepConstants
{
    //UNDER 255
    public static final int TYPE_NULL = 0;
    public static final int TYPE_OBJECT = 1;
    public static final int TYPE_BYTE = 2;
    public static final int TYPE_BOOLEAN = 3;
    public static final int TYPE_CHARACTER = 4;
    public static final int TYPE_SHORT = 5;
    public static final int TYPE_INTEGER = 6;
    public static final int TYPE_LONG = 7;
    public static final int TYPE_FLOAT = 8;
    public static final int TYPE_DOUBLE = 9;
    public static final int TYPE_STRING = 10;
    public static final int TYPE_BYTE_ARRAY = 11;
    public static final int TYPE_CHAR_ARRAY = 12;
    public static final int TYPE_STRING_ARRAY = 13;

    public static final int TYPE_CLASS = 15;
    public static final int TYPE_STREAMBLE = 16;
    public static final int TYPE_OBJECT_ARRAY = 17;
    public static final int TYPE_EXTERNALIZABLE = 18;

    public static final int TYPE_BOOLEAN_ARRAY = 20;
    public static final int TYPE_SHORT_ARRAY = 21;
    public static final int TYPE_INTEGER_ARRAY = 22;
    public static final int TYPE_LONG_ARRAY = 23;
    public static final int TYPE_FLOAT_ARRAY = 24;
    public static final int TYPE_DOUBLE_ARRAY = 25;

    public static final int TYPE_ASCII = 32;
    public static final int TYPE_ASCII_ARRAY = 33;

    /**
     * 采用Serializer方式传递的对象
     */
    public static final int TYPE_SERIALIZER = 40;

    /**
     * 采用Serializer方式传递的对象
     */
    public static final int TYPE_SERIALIZER_ARRAY = 41;


    //数组最大长度
    public static final int MAX_ARRAY_LENGTH = 64 * 1024;

    /**
     * Magic number that is written to the stream header.
     */
    public static final short STREAM_MAGIC = (short) 0x8888;

    /**
     * Version number that is written to the stream header.
     */
    public static final short STREAM_VERSION = 1;

    /**
     * 协议名称
     */
    public static final String NIEP = "niep";
}
