package org.femtoframework.net.comm;


import org.femtoframework.io.IOUtil;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 数据报文输入流
 *
 * @author fengyun
 * @version 1.1 2005-3-30 17:06:50 增加了注释
 *          1.00 Mar 21, 2003 10:22:50 AM
 */
public class DataInputStream
    extends java.io.DataInputStream
{
    /**
     * 构造
     *
     * @param input 输入流
     */
    public DataInputStream(InputStream input)
    {
        super(input);
    }

    /**
     * 读取给定长度的byte数组
     *
     * @param len 长度
     * @return
     * @throws IOException
     */
    public byte[] readBytes(int len)
        throws IOException
    {
        byte[] bytes = new byte[len];
        IOUtil.readFully(this, bytes);
        return bytes;
    }

//    /**
//     * 读取给定长度的字符串
//     *
//     * @param len 长度
//     * @return
//     * @throws java.io.IOException
//     */
//    public OctetString readOctetString(int len)
//        throws IOException
//    {
//        return readOctetString(len, false);
//    }
//
//    /**
//     * 读取字符串，可以允许自动trim掉'\0'的数据
//     *
//     * @param len
//     * @param trim 是否自动Trim
//     * @return
//     * @throws IOException
//     */
//    public OctetString readOctetString(int len, boolean trim)
//        throws IOException
//    {
//        byte[] bytes = new byte[len];
//        StreamUtil.readFully(this, bytes);
//        int l = len;
//        if (trim) {
//            l = trim(bytes, len);
//        }
//        return new OctetString(bytes, 0, l);
//    }

    /**
     * 读取指定长度的字符串
     *
     * @param len 长度
     * @return
     */
    public String readString(int len)
        throws IOException
    {
        byte[] bytes = new byte[len];
        IOUtil.readFully(this, bytes);
        int l = trim(bytes, len);
        return new String(bytes, 0, l);
    }

    /**
     * See the general contract of the <code>readUnsignedShort</code>
     * method of <code>DataInput</code>.
     * <p/>
     * Bytes
     * for this operation are read from the contained
     * input stream.
     *
     * @return the next two bytes of this input stream, interpreted as an
     *         unsigned 16-bit integer.
     * @throws java.io.EOFException if this input stream reaches the end before
     *                              reading two bytes.
     * @throws IOException          if an I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public final long readUnsignedInt() throws IOException
    {
        long ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
    }

    /**
     * Trim Bytes
     *
     * @param bytes
     * @param len
     * @return 实际长度
     */
    private int trim(byte[] bytes, int len)
    {
        for (int i = 0; i < len; i++) {
            if (bytes[i] == 0) {
                return i;
            }
        }
        return len;
    }


    /**
     * 读取字符串，如果遇到'\0'结尾的，则结束
     *
     * @return
     */
    public String readCString()
        throws IOException
    {
        StringBuilder sb = new StringBuilder();
        int b = 0;
        while ((b = read()) != -1) {
            if (b == 0) {
                break;
            }
            sb.append((char) b);
        }
        return sb.toString();
    }
}
