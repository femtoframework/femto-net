package org.femtoframework.net.comm;

import java.io.IOException;
import java.io.OutputStream;

import org.femtoframework.util.StringUtil;

/**
 * 数据报文输出流
 *
 * @author fengyun
 * @version 1.1 2005-3-30 17:06:50 增加了注释
 *          1.00 Mar 21, 2003 10:29:49 AM
 */
public class DataOutputStream
    extends java.io.DataOutputStream
{
    public static final byte[] FILL_BYTES = new byte[]{
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    /**
     * 构造
     *
     * @param output 输出流
     */
    public DataOutputStream(OutputStream output)
    {
        super(output);
    }

//    /**
//     * 输出字符串
//     *
//     * @param str 字符串
//     * @return
//     * @throws IOException
//     */
//    public int writeString(OctetString str)
//        throws IOException
//    {
//        if (str == null || str.length() == 0) {
//            return 0;
//        }
//        str.writeTo(this);
//        return str.length();
//    }
//
//    /**
//     * 输出字符串，如果长度没有到达给定的长度，那么用 '\0'来补齐
//     *
//     * @param str 字符串
//     * @param len 长度
//     * @return
//     * @throws IOException
//     */
//    public int writeString(OctetString str, int len)
//        throws IOException
//    {
//        int strLen = writeString(str);
//        if (strLen < len) {
//            fill(len - strLen);
//        }
//        return len;
//    }

    /**
     * 写出字符串
     *
     * @param str
     */
    public int writeString(String str)
        throws IOException
    {
        if (StringUtil.isInvalid(str)) {
            return 0;
        }

        byte[] bytes = str.getBytes();
        write(bytes);
        return bytes.length;
    }

    /**
     * 输出无符号整数
     *
     * @param l
     * @throws IOException
     */
    public void writeUnsignedInt(long l)
        throws IOException
    {
        write((int) (l >>> 24) & 0xFF);
        write((int) (l >>> 16) & 0xFF);
        write((int) (l >>> 8) & 0xFF);
        write((int) (l) & 0xFF);
    }

    /**
     * 输出字符串，如果长度没有到达给定的长度，那么用 '\0'来补齐
     *
     * @param str 字符串
     * @param len 长度
     */
    public int writeString(String str, int len)
        throws IOException
    {
        int strLen = writeString(str);
        if (strLen < len) {
            fill(len - strLen);
        }
        return len;
    }


    /**
     * 输出字符串，在结束的地方加一个'\0'
     *
     * @param str 字符串
     */
    public void writeCString(String str)
        throws IOException
    {
        if (str != null) {
            byte[] bytes = str.getBytes();
            write(bytes);
        }
        write(0);
    }

    /**
     * 用0来填充
     *
     * @param len 长度
     * @throws java.io.IOException
     */
    public void fill(int len) throws IOException
    {
        int l = len;
        while (l > 0) {
            if (l <= FILL_BYTES.length) {
                write(FILL_BYTES, 0, l);
                break;
            }
            else {
                write(FILL_BYTES);
                l -= FILL_BYTES.length;
            }
        }
    }
}
