package org.femtoframework.frame.niep;

import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Niep OutputStream
 *
 * @author fengyun
 * @version 1.00 Jul 2, 2003 11:06:37 AM
 */
public class NiepOutputStream
    extends DataOutputStream
    implements NiepConstants
{
    /**
     * 构造
     *
     * @param out
     */
    public NiepOutputStream(OutputStream out)
    {
        super(out);
    }

    /**
     * 输出数据类型
     *
     * @param type 数据类型
     */
    public void writeType(int type)
        throws IOException
    {
        write(type);
    }

    /**
     * 输出Null
     */
    public void writeNull()
        throws IOException
    {
        writeType(TYPE_NULL);
    }

    /**
     * 输出对象
     *
     * @param obj
     * @throws IOException
     */
    public void writeObject(Object obj)
        throws IOException
    {
        if (obj == null) {
            writeNull();
            return;
        }

        if (obj instanceof String) {
            writeType(TYPE_STRING);
            writeString((String) obj);
        }
        else if (obj instanceof byte[]) {
            writeType(TYPE_BYTE_ARRAY);
            writeBytes((byte[]) obj);
        }
        else if (obj instanceof char[]) {
            writeType(TYPE_CHAR_ARRAY);
            writeChars((char[]) obj);
        }
        else if (obj instanceof Character) {
            writeType(TYPE_CHARACTER);
            writeChar(((Character) obj).charValue());
        }
        else if (obj instanceof Number) {
            if (obj instanceof Integer) {
                writeType(TYPE_INTEGER);
                writeInt(((Integer) obj).intValue());
            }
            else if (obj instanceof Long) {
                writeType(TYPE_LONG);
                writeLong(((Long) obj).longValue());
            }
            else if (obj instanceof Byte) {
                writeType(TYPE_BYTE);
                writeByte(((Byte) obj).byteValue());
            }
            else if (obj instanceof Float) {
                writeType(TYPE_FLOAT);
                writeFloat(((Float) obj).floatValue());
            }
            else if (obj instanceof Double) {
                writeType(TYPE_DOUBLE);
                writeDouble(((Double) obj).doubleValue());
            }
            else if (obj instanceof Short) {
                writeType(TYPE_SHORT);
                writeDouble(((Short) obj).shortValue());
            }
            else {
                throw new IOException("Unsupported object:" + obj.getClass());
            }
        }
        else if (obj instanceof Boolean) {
            writeType(TYPE_BOOLEAN);
            writeBoolean(((Boolean) obj).booleanValue());
        }
        else if (obj instanceof String[]) {
            writeType(TYPE_STRING_ARRAY);
            writeStringArray((String[]) obj);
        }
        else if (obj instanceof boolean[]) {
            writeType(TYPE_BOOLEAN_ARRAY);
            writeBooleanArray((boolean[]) obj);
        }
        else if (obj instanceof int[]) {
            writeType(TYPE_INTEGER_ARRAY);
            writeIntArray((int[]) obj);
        }
        else if (obj instanceof long[]) {
            writeType(TYPE_LONG_ARRAY);
            writeLongArray((long[]) obj);
        }
        else if (obj instanceof float[]) {
            writeType(TYPE_FLOAT_ARRAY);
            writeFloatArray((float[]) obj);
        }
        else if (obj instanceof double[]) {
            writeType(TYPE_DOUBLE_ARRAY);
            writeDoubleArray((double[]) obj);
        }
        else if (obj instanceof short[]) {
            writeType(TYPE_SHORT_ARRAY);
            writeShortArray((short[]) obj);
        }
        else if (obj instanceof Class) {
            writeType(TYPE_CLASS);
            writeClass((Class) obj);
        }
        else if (obj instanceof Externalizable) {
            writeType(TYPE_EXTERNALIZABLE);
            writeExternalizable((Externalizable) obj);
        }
        else if (obj instanceof Object[]) {
            writeType(TYPE_OBJECT_ARRAY);
            writeObjectArray((Object[]) obj);
        }
        else {
            throw new IOException("Unsupported object:" + obj.getClass());
        }
    }

    /**
     * 输出字符串数组
     *
     * @param array 字符串数组
     * @throws IOException
     */
    public void writeStringArray(String[] array)
        throws IOException
    {
        int len = array.length;
        writeInt(len);
        for (int i = 0; i < len; i++) {
            writeString(array[i]);
        }
    }

    /**
     * 输出字符串数组
     *
     * @param array 字符串数组
     * @throws IOException
     */
    public void writeObjectArray(Object[] array)
        throws IOException
    {
        int len = array.length;
        writeInt(len);
        for (int i = 0; i < len; i++) {
            writeObject(array[i]);
        }
    }

    /**
     * 输出类
     *
     * @param clazz 类
     */
    public void writeClass(Class clazz)
        throws IOException
    {
        writeAscii(clazz.getName());
    }

    /**
     * 输出Externalizable对象
     *
     * @param obj
     * @throws IOException
     */
    public void writeExternalizable(Externalizable obj)
        throws IOException
    {
        writeClass(obj.getClass());

        NiepMarshalStream nms = new NiepMarshalStream(this, false);
        obj.writeExternal(nms);
        nms.flush();
        nms.close();
    }

    /**
     * 输出字节数组
     *
     * @param bytes 字节数组
     */
    public void writeBytes(byte[] bytes)
        throws IOException
    {
        int len = bytes.length;
        writeInt(len);
        write(bytes);
    }

    /**
     * 输出字符数组
     *
     * @param chars 字符数组
     */
    public void writeChars(char[] chars)
        throws IOException
    {
        int len = -1;
        if (chars != null) {
            len = chars.length;
        }
        writeInt(len);
        if (len <= 0) {
            return;
        }
        byte[] bytes = new byte[len > 1024 ? 1024 : len > 128 ? 128 : 8];
        int j = 0;
        int v;
        for (int i = 0; i < len; i++) {
            v = chars[i];
            bytes[j++] = (byte) ((v >>> 8) & 0xFF);
            bytes[j++] = (byte) ((v >>> 0) & 0xFF);
            if (j == bytes.length) { //Full
                out.write(bytes, 0, j);
                j = 0;
            }
        }
        if (j > 0) {
            out.write(bytes, 0, j);
        }
    }

    /**
     * 输出字符串
     *
     * @param str 字符串
     */
    public void writeString(String str)
        throws IOException
    {
        int len = -1;
        if (str != null) {
            len = str.length();
        }
        writeInt(len);

        if (len <= 0) {
            return;
        }
        int j = 0;
        int v;
        byte[] buf = new byte[len > 1024 ? 1024 : len > 128 ? 128 : 8];
        for (int i = 0; i < len; i++) {
            v = str.charAt(i);
            buf[j++] = (byte) ((v >>> 8) & 0xFF);
            buf[j++] = (byte) ((v >>> 0) & 0xFF);
            if (j == buf.length) { //Full
                out.write(buf, 0, j);
                j = 0;
            }
        }
        if (j > 0) {
            out.write(buf, 0, j);
        }
    }

    /**
     * 输出ASCII串
     */
    public void writeAscii(String str)
        throws IOException
    {
        int len = -1;
        if (str != null) {
            len = str.length();
        }
        writeInt(len);

        int v;
        if (len <= 0) {
            return;
        }
        else {
            byte[] bytes = new byte[len > 1024 ? 1024 : len > 128 ? 128 : 8];
            int j = 0;
            for (int i = 0; i < len; i++) {
                v = str.charAt(i);
                bytes[j++] = (byte) ((v >>> 0) & 0xFF);
                if (j == bytes.length) { //Full
                    out.write(bytes, 0, j);
                    j = 0;
                }
            }
            if (j > 0) {
                out.write(bytes, 0, j);
            }
        }
    }

    /**
     * 输出ASCII串
     */
    public void writeAsciiArray(String[] array)
        throws IOException
    {
        int len = array.length;
        writeInt(len);
        for (int i = 0; i < len; i++) {
            writeAscii(array[i]);
        }
    }

    /**
     * 输出boolean[]
     *
     * @param array 数组
     */
    public void writeBooleanArray(boolean[] array)
        throws IOException
    {
        int len = array.length;
        writeInt(len);
        for (int i = 0; i < len; i++) {
            writeBoolean(array[i]);
        }
    }

    /**
     * 输出int[]
     *
     * @param array 数组
     */
    public void writeIntArray(int[] array)
        throws IOException
    {
        int len = array.length;
        writeInt(len);
        for (int i = 0; i < len; i++) {
            writeInt(array[i]);
        }
    }

    /**
     * 输出long[]
     *
     * @param array 数组
     */
    public void writeLongArray(long[] array)
        throws IOException
    {
        int len = array.length;
        writeInt(len);
        for (int i = 0; i < len; i++) {
            writeLong(array[i]);
        }
    }

    /**
     * 输出float[]
     *
     * @param array 数组
     */
    public void writeFloatArray(float[] array)
        throws IOException
    {
        int len = array.length;
        writeInt(len);
        for (int i = 0; i < len; i++) {
            writeFloat(array[i]);
        }
    }

    /**
     * 输出short[]
     *
     * @param array 数组
     */
    public void writeShortArray(short[] array)
        throws IOException
    {
        int len = array.length;
        writeInt(len);
        for (int i = 0; i < len; i++) {
            writeShort(array[i]);
        }
    }

    /**
     * 输出double[]
     *
     * @param array 数组
     */
    public void writeDoubleArray(double[] array)
        throws IOException
    {
        int len = array.length;
        writeInt(len);
        for (int i = 0; i < len; i++) {
            writeDouble(array[i]);
        }
    }
}
