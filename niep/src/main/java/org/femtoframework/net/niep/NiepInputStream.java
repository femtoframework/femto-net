package org.femtoframework.net.niep;

import java.io.DataInputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;

import org.femtoframework.io.CodecUtil;

/**
 * Niep InputStream
 *
 * @author fengyun
 * @version 1.00 Jul 2, 2003 11:08:41 AM
 */
public class NiepInputStream
    extends DataInputStream
    implements NiepConstants
{
    /**
     * 构造
     *
     * @param in 输入流
     */
    public NiepInputStream(InputStream in)
    {
        super(in);
    }

    /**
     * 读取数据类型
     *
     * @return 数据类型
     */
    public int readType() throws IOException
    {
        return read();
    }

    /**
     * 读取Null
     */
    public int readNull() throws IOException
    {
        return readType();
    }

    /**
     * 读取对象
     *
     * @throws IOException
     */
    public Object readObject() throws IOException
    {
        int type = readType();
        return readObject(type);
    }

    /**
     * 读取对象
     *
     * @throws IOException
     */
    Object readObject(int type) throws IOException
    {
        Object obj;
        switch (type) {
            case TYPE_NULL:
                obj = null;
                break;
            case TYPE_STRING:
                obj = readString();
                break;
            case TYPE_BYTE_ARRAY:
                obj = readBytes();
                break;
            case TYPE_CHAR_ARRAY:
                obj = readChars();
                break;
            case TYPE_CHARACTER:
                obj = readChar();
                break;
            case TYPE_INTEGER:
                obj = readInt();
                break;
            case TYPE_LONG:
                obj = readLong();
                break;
            case TYPE_BYTE:
                obj = readByte();
                break;
            case TYPE_FLOAT:
                obj = readFloat();
                break;
            case TYPE_DOUBLE:
                obj = readDouble();
                break;
            case TYPE_SHORT:
                obj = readShort();
                break;
            case TYPE_BOOLEAN:
                obj = readBoolean();
                break;
            case TYPE_STRING_ARRAY:
                obj = readStringArray();
                break;
            case TYPE_BOOLEAN_ARRAY:
                obj = readBooleanArray();
                break;
            case TYPE_INTEGER_ARRAY:
                obj = readIntArray();
                break;
            case TYPE_LONG_ARRAY:
                obj = readLongArray();
                break;
            case TYPE_FLOAT_ARRAY:
                obj = readFloatArray();
                break;
            case TYPE_DOUBLE_ARRAY:
                obj = readDoubleArray();
                break;
            case TYPE_SHORT_ARRAY:
                obj = readShortArray();
                break;
            case TYPE_OBJECT_ARRAY:
                obj = readObjectArray();
                break;
            case TYPE_ASCII:
                obj = readAscii();
                break;
            case TYPE_ASCII_ARRAY:
                obj = readAsciiArray();
                break;
            case TYPE_CLASS:
                obj = readClass();
                break;
            case TYPE_EXTERNALIZABLE:
                obj = readExternalizable();
                break;
            default:
                throw new IOException("Unsupported type:" + type);
        }
        return obj;
    }

    /**
     * 读取字符串数组
     *
     * @throws IOException
     */
    public String[] readStringArray()
        throws IOException
    {
        int len = readInt();
        if (len < 0 || len > MAX_ARRAY_LENGTH) {
            throw new IOException("Invalid length:" + len);
        }

        String[] array = new String[len];
        for (int i = 0; i < len; i++) {
            array[i] = readString();
        }
        return array;
    }


    /**
     * 读取对象数组
     *
     * @throws IOException
     */
    public Object[] readObjectArray()
        throws IOException
    {
        int len = readInt();
        if (len < 0 || len > MAX_ARRAY_LENGTH) {
            throw new IOException("Invalid length:" + len);
        }

        Object[] array = new Object[len];
        for (int i = 0; i < len; i++) {
            array[i] = readObject();
        }
        return array;
    }

    /**
     * 读取类
     *
     * @return 类名
     */
    public Class readClass() throws IOException
    {
        String className = readAscii();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        try {
            Class clazz = loader.loadClass(className);
            if (clazz == null) {
                throw new IOException("No such class:" + className);
            }
            return clazz;
        }
        catch (ClassNotFoundException cnfe) {
            throw new IOException("Class not found:" + className);
        }
    }

    /**
     * 读取Externalizable的对象
     */
    public Externalizable readExternalizable()
        throws IOException
    {
        Class clazz = readClass();
        Externalizable obj;
        try {
            obj = (Externalizable) clazz.newInstance();

            NiepDemarshalStream nds = new NiepDemarshalStream(this, false);
            obj.readExternal(nds);
            nds.close();
            return obj;
        }
        catch (ClassNotFoundException cnfe) {
            throw new IOException("Class not found exception:" + cnfe.getMessage());
        }
        catch (IllegalAccessException e) {
            throw new IOException("Can't access the constructor");
        }
        catch (InstantiationException e) {
            throw new IOException("Instantiation exception");
        }
    }


    /**
     * 读取字节数组
     */
    public byte[] readBytes() throws IOException
    {
        int len = readInt();
        if (len < 0 || len > MAX_ARRAY_LENGTH) {
            throw new IOException("Invalid length:" + len);
        }

        byte[] bytes = new byte[len];
        readFully(bytes);
        return bytes;
    }

    /**
     * 读取字符数组
     */
    public char[] readChars() throws IOException
    {
        int len = readInt();
        if (len < 0) {
            return null;
        }
        else if (len == 0) {
            return new char[0];
        }

        int ch1, ch2;
        byte[] bytes;
        if (len >= 512) {
            bytes = new byte[1024];
        }
        else {
            bytes = new byte[128];
        }

        int read;
        char[] chars = new char[len];
        int i = 0;
        while (len > 0) {
            read = bytes.length;
            read = read > len * 2 ? len * 2 : read;
            readFully(bytes, 0, read);
            if (read <= 0) {
                break;
            }
            for (int j = 0; j < read;) {
                ch1 = (bytes[j++] & 0xFF);
                ch2 = (bytes[j++] & 0xFF);
                chars[i++] = (char) ((ch1 << 8) + (ch2));
            }
            len -= read / 2;
        }

        return chars;
    }

    /**
     * 读取Ascii字符串
     */
    public String readAscii() throws IOException
    {
        int len = readInt();
        if (len < 0) {
            return null;
        }
        else if (len == 0) {
            return "";
        }

        byte[] bytes = new byte[len > 1024 ? 1024 : 128];
        int ch1;
        int read;
        StringBuilder sb = new StringBuilder(len);
        while (len > 0) {
            read = bytes.length > len ? len : bytes.length;
            readFully(bytes, 0, read);
            if (read <= 0) {
                break;
            }
            for (int i = 0, j = 0; i < read; i++) {
                ch1 = (bytes[j++] & 0xFF);
                sb.append((char) (ch1));
            }
            len -= read;
        }
        return sb.toString();
    }

    /**
     * 读取Ascii字符串数组
     */
    public String[] readAsciiArray() throws IOException
    {
        int len = readInt();
        if (len < 0 || len > MAX_ARRAY_LENGTH) {
            throw new IOException("Invalid length:" + len);
        }

        String[] array = new String[len];
        for (int i = 0; i < len; i++) {
            array[i] = readAscii();
        }
        return array;
    }

    /**
     * 读取字符串
     */
    public String readString() throws IOException
    {
        int len = readInt();

        if (len < 0) {
            return null;
        }
        else if (len == 0) {
            return "";
        }

        byte[] buf = new byte[len > 1024 ? 1024 : 128];
        int ch1, ch2;
        int read;
        StringBuilder sb = new StringBuilder(len);
        while (len > 0) {
            read = buf.length > len * 2 ? len * 2 : buf.length;
            readFully(buf, 0, read);
            if (read <= 0) {
                break;
            }
            for (int j = 0; j < read;) {
                ch1 = (buf[j++] & 0xFF);
                ch2 = (buf[j++] & 0xFF);
                sb.append((char) ((ch1 << 8) + (ch2)));
            }
            len -= (read / 2);
        }
        return sb.toString();
    }

    /**
     * 读取boolean[]
     */
    public boolean[] readBooleanArray()
        throws IOException
    {
        int len = readInt();
        if (len < 0 || len > MAX_ARRAY_LENGTH) {
            throw new IOException("Invalid length:" + len);
        }

        boolean[] array = new boolean[len];
        for (int i = 0; i < len; i++) {
            array[i] = readBoolean();
        }
        return array;
    }

    /**
     * 读取int[]
     *
     * @return 数组
     */
    public int[] readIntArray()
        throws IOException
    {
        int len = readInt();
        if (len < 0 || len > MAX_ARRAY_LENGTH) {
            throw new IOException("Invalid length:" + len);
        }

        int[] array = new int[len];
        for (int i = 0; i < len; i++) {
            array[i] = readInt();
        }
        return array;
    }

    /**
     * 读取long[]
     *
     * @return 数组
     */
    public long[] readLongArray()
        throws IOException
    {
        int len = readInt();
        if (len < 0 || len > MAX_ARRAY_LENGTH) {
            throw new IOException("Invalid length:" + len);
        }

        long[] array = new long[len];
        for (int i = 0; i < len; i++) {
            array[i] = readLong();
        }
        return array;
    }

    /**
     * 读取float[]
     *
     * @return 数组
     */
    public float[] readFloatArray()
        throws IOException
    {
        int len = readInt();
        if (len < 0 || len > MAX_ARRAY_LENGTH) {
            throw new IOException("Invalid length:" + len);
        }

        float[] array = new float[len];
        for (int i = 0; i < len; i++) {
            array[i] = readFloat();
        }
        return array;
    }

    /**
     * 读取short[]
     *
     * @return 数组
     */
    public short[] readShortArray()
        throws IOException
    {
        int len = readInt();
        if (len < 0 || len > MAX_ARRAY_LENGTH) {
            throw new IOException("Invalid length:" + len);
        }

        short[] array = new short[len];
        for (int i = 0; i < len; i++) {
            array[i] = readShort();
        }
        return array;
    }

    /**
     * 读取double[]
     *
     * @return 数组
     */
    public double[] readDoubleArray()
        throws IOException
    {
        int len = readInt();
        if (len < 0 || len > MAX_ARRAY_LENGTH) {
            throw new IOException("Invalid length:" + len);
        }

        double[] array = new double[len];
        for (int i = 0; i < len; i++) {
            array[i] = readDouble();
        }
        return array;
    }
}
