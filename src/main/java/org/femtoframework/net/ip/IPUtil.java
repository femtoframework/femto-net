package org.femtoframework.net.ip;

import org.femtoframework.lang.Binary;
import org.femtoframework.util.StringUtil;
import org.femtoframework.util.crypto.Hex;

import java.text.ParseException;
import java.util.regex.Pattern;

/**
 * IP地址工具类
 *
 * @author fengyun
 * @version 2.00 增加了IPV6的支持
 *          1.00 Aug 9, 2003 5:25:15 AM
 */
public class IPUtil
{
    /**
     * 将数组表示的IP地址变成字符串形式
     *
     * @param addr IP地址，长度必需为4或者16，否则返回<CODE>null</CODE>
     * @return 字符串形式
     */
    public static String toString(byte[] addr)
    {
        return toString(addr, 0, addr.length == 16);
    }

    /**
     * 将数组表示的IP地址变成字符串形式
     *
     * @param addr IP地址，长度必需为4，否则返回<CODE>null</CODE>
     * @param off  其实位置
     * @return 字符串形式
     */
    public static String toString(byte[] addr, int off)
    {
        return toString(addr, off, false);
    }

    /**
     * 将数组表示的IP地址变成字符串形式
     *
     * @param addr IP地址，长度必需为4或者16，否则返回<CODE>null</CODE>
     * @param off  其实位置
     * @return 字符串形式
     */
    public static String toString(byte[] addr, int off, boolean ipv6)
    {
        if (addr == null || addr.length - off < 4 || (ipv6 && addr.length - off < 16)) {
            return null;
        }

        if (ipv6) {
            if (isIPv4(addr)) {
                byte[] newAddr = new byte[INADDR4SZ];
                System.arraycopy(addr, 12, newAddr, 0, INADDR4SZ);
                addr = newAddr;
            }
            else {
                return numericToTextFormat(addr, off);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append((int)addr[off++]).append('.');
        sb.append((int)addr[off++]).append('.');
        sb.append((int)addr[off++]).append('.');
        sb.append((int)addr[off]);
        return sb.toString();
    }

    /*
    * Convert IPv6 binary address into presentation (printable) format.
    *
    * @param src a byte array representing the IPv6 numeric address
    * @return a String representing an IPv6 address in
    *         textual representation format
    * @since 1.4
    */
    private static String numericToTextFormat(byte[] src, int off)
    {
        StringBuilder sb = new StringBuilder(39);
        int end = off + INADDR16SZ;
        int v = ((src[off++] << 8) & 0xff00) | (src[off++] & 0xff);
        append(sb, v);
        for (int i = off; i < end;) {
            sb.append(':');
            v = ((src[i++] << 8) & 0xff00) | (src[i++] & 0xff);
            append(sb, v);
        }
        return sb.toString();
    }

    private static StringBuilder append(StringBuilder sb, int c)
    {
        int v = (c >> 12) & 0xF;
        if (v > 0) {
            sb.append(Hex.HEX_CHARS[v]);
            sb.append(Hex.HEX_CHARS[(c >> 8) & 0xF]);
            sb.append(Hex.HEX_CHARS[(c >> 4) & 0xF]);
            sb.append(Hex.HEX_CHARS[c & 0xF]);
            return sb;
        }
        v = (c >> 8) & 0xF;
        if (v > 0) {
            sb.append(Hex.HEX_CHARS[v]);
            sb.append(Hex.HEX_CHARS[(c >> 4) & 0xF]);
            sb.append(Hex.HEX_CHARS[c & 0xF]);
            return sb;
        }
        v = (c >> 4) & 0xF;
        if (v > 0) {
            sb.append(Hex.HEX_CHARS[v]);
            sb.append(Hex.HEX_CHARS[c & 0xF]);
        }
        else {
            sb.append(Hex.HEX_CHARS[c & 0xF]);
        }
        return sb;
    }

    /*
    * Convert IPv6 binary address into presentation (printable) format.
    *
    * @param src a byte array representing the IPv6 numeric address
    * @return a String representing an IPv6 address in
    *         textual representation format
    * @since 1.4
    */
    private static String numericToTextFormat(int[] src)
    {
        StringBuilder sb = new StringBuilder(39);
        append(sb, src[0]);
        for (int i = 1; i < 8;) {
            sb.append(':');
            append(sb, src[i]);
        }
        return sb.toString();
    }

    /**
     * 将数组表示的IP地址变成字符串形式
     *
     * @param addr IP地址，长度必需为4或者8，否则返回<CODE>null</CODE>
     * @return 字符串形式
     */
    public static String toString(int[] addr)
    {
        if (addr == null || (addr.length != 4 && addr.length != 8)) {
            return null;
        }

        int off = 0;
        if (addr.length == 8) {
            if (addr[0] == 0 && addr[1] == 0 && addr[2] == 0 && (addr[3] == 0 || addr[3] == 0xFFFF)) {
                off = 4;
            }
            else {
                return numericToTextFormat(addr);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(addr[off++]).append('.');
        sb.append(addr[off++]).append('.');
        sb.append(addr[off++]).append('.');
        sb.append(addr[off++]);
        return sb.toString();
    }

    /**
     * 将整数表示的IP地址变成字符串形式
     *
     * @param addr
     * @return 字符串形式
     */
    public static String toString(int addr)
    {
        return toString(Binary.toBytes(addr));
    }

    // 0 - 255
    private static final String NUMBER
        = "(0|(1[0-9]?[0-9]?)|(2(([0-4][0-9]?)|(5[0-5]?)|[6-9]?))|([3-9][0-9]?))";

    public static final String PATTERN
        = "((" + NUMBER + "\\.){3}" + NUMBER + ")";

    private static final String HEX_NUEMBER
        = "(([0-9]|[A-F]|[a-f]){0,4})";

    public static final String IPV6_PATTERN
        = "((" + HEX_NUEMBER + ":){2,6}((" + HEX_NUEMBER + "(:" + HEX_NUEMBER + ")?)|" + PATTERN + "))";

    private static Pattern ipv4 = Pattern.compile(PATTERN);
    private static Pattern ipv6 = Pattern.compile(IPV6_PATTERN);

    /**
     * 将字符串形式的IP地址变成Byte数组
     *
     * @param addr 如果不是合法的IP地址，返回<CODE>null</CODE>
     */
    public static byte[] toBytes(String addr)
    {
        if (StringUtil.isValid(addr)) {
            boolean ipv6Expected = false;
            if (addr.charAt(0) == '[') {
                // This is supposed to be an IPv6 litteral
                if (addr.length() > 2 && addr.charAt(addr.length() - 1) == ']') {
                    addr = addr.substring(1, addr.length() - 1);
                    ipv6Expected = true;
                }
                else {
                    return null;
                }
            }

            if (ipv6Expected || addr.indexOf(':') >= 0) {
                if (ipv6.matcher(addr).matches()) {
                    return textToNumericFormatV6(addr);
                }
            }
            else {
                if (ipv4.matcher(addr).matches()) {
                    return textToNumericFormatV4(addr);
                }
            }
        }
        return null;
    }

    /**
     * 将Int数组表示的IP地址变成Byte数组
     *
     * @param addr 如果不是合法的IP地址，返回<CODE>null</CODE>
     * @return Byte数组
     */
    public static byte[] toBytes(int[] addr)
    {
        if (addr == null || (addr.length != 4 && addr.length != 8)) {
            return null;
        }

        if (addr.length == 4) {
            byte[] bytes = new byte[4];
            bytes[0] = (byte)addr[0];
            bytes[1] = (byte)addr[1];
            bytes[2] = (byte)addr[2];
            bytes[3] = (byte)addr[3];
            return bytes;
        }
        else {
            byte[] bytes = new byte[16];
            bytes[0] = (byte)((addr[0] >> 8) & 0xFF);
            bytes[1] = (byte)(addr[0] & 0xFF);
            bytes[2] = (byte)((addr[1] >> 8) & 0xFF);
            bytes[3] = (byte)(addr[1] & 0xFF);
            bytes[4] = (byte)((addr[2] >> 8) & 0xFF);
            bytes[5] = (byte)(addr[2] & 0xFF);
            bytes[6] = (byte)((addr[3] >> 8) & 0xFF);
            bytes[7] = (byte)(addr[3] & 0xFF);
            bytes[8] = (byte)((addr[4] >> 8) & 0xFF);
            bytes[9] = (byte)(addr[4] & 0xFF);
            bytes[10] = (byte)((addr[5] >> 8) & 0xFF);
            bytes[11] = (byte)(addr[5] & 0xFF);
            bytes[12] = (byte)((addr[6] >> 8) & 0xFF);
            bytes[13] = (byte)(addr[6] & 0xFF);
            bytes[14] = (byte)((addr[7] >> 8) & 0xFF);
            bytes[15] = (byte)(addr[7] & 0xFF);
            return bytes;
        }
    }

    /**
     * 将整数表示的IP地址变成字节数组的IP地址
     *
     * @param addr IP地址
     */
    public static byte[] toBytes(int addr)
    {
        return Binary.toBytes(addr);
    }

    /**
     * 将字符串形式的IP地址变成Byte数组
     *
     * @param addr 如果不是合法的IP地址，返回<CODE>null</CODE>
     */
    public static int[] toInts(String addr)
    {
        byte[] array = toBytes(addr);
        if (array != null) {
            return toInts(array);
        }
        return null;
    }

    /**
     * 将字符串形式的IP地址变成Byte数组
     *
     * @param addrs 如果不是合法的IP地址，返回<CODE>null</CODE>
     */
    public static int[] toInts(byte[] addrs)
    {
        int len = addrs.length;
        if (len == INADDR4SZ) {
            int[] ints = new int[len];
            for (int i = 0; i < len; i++) {
                ints[i] = addrs[i] & 0xFF;
            }
            return ints;
        }
        else if (len == INADDR16SZ) {
            int[] ints = new int[INADDR16SZ / INT16SZ];
            for (int i = 0, j = 0; i < len;) {
                ints[j++] = ((addrs[i++] << 8) & 0xFF00) | (addrs[i++] & 0xFF);
            }
            return ints;
        }
        else {
            throw new IllegalArgumentException("Invalid length of array:" + len);
        }
    }

    /**
     * 将字符串表示的IP地址变成整数的IP地址
     *
     * @param addr IP地址，如果是非法地址，返回<CODE>-1</CODE>
     * @return IP地址
     */
    public static int toInt(String addr)
    {
        byte[] bytes = toBytes(addr);
        if (bytes != null) {
            if (bytes.length == 4) {
                return Binary.toInt(bytes);
            }
            else {
                return Binary.toInt(bytes, bytes.length - 4);
            }
        }
        return -1;
    }

    /**
     * 将IP地址Reverse
     *
     * @param ip IP地址
     * @return 调转的序列地址
     * @throws IllegalArgumentException 无效的IP地址
     */
    public static String toReverse(String ip)
    {
        int[] ints = toInts(ip);
        if (ints == null) {
            throw new IllegalArgumentException("Invalid ip address:" + ip);
        }
        int len = ints.length;
        int[] reverseIP = new int[len];
        int temp;
        int r;
        for (int i = 0; i < len; i++) {
            temp = ints[i];
            r = (len - 1) - i;
            reverseIP[i] = ints[r];
            reverseIP[r] = temp;
        }
        return toString(reverseIP);
    }

    /**
     * Strip the last char of a string when it ends with a dot
     *
     * @param data The String where the dot should removed
     * @return modified The Given String with last char stripped
     */
    public static String stripDot(String data)
    {
        data = data.trim();

        if (data.endsWith(".")) {
            return data.substring(0, data.length() - 1);
        }
        else {
            return data;
        }
    }

    /**
     * 是否是有效的IP地址
     *
     * @param addr
     * @return IP地址
     */
    public static boolean isValid(String addr)
    {
        if (StringUtil.isValid(addr)) {
            boolean ipv6Expected = false;
            if (addr.charAt(0) == '[') {
                // This is supposed to be an IPv6 litteral
                if (addr.length() > 2 && addr.charAt(addr.length() - 1) == ']') {
                    addr = addr.substring(1, addr.length() - 1);
                    ipv6Expected = true;
                }
                else {
                    return false;
                }
            }

            if (ipv6Expected || addr.indexOf(':') >= 0) {
                return ipv6.matcher(addr).matches();
            }
            else {
                return ipv4.matcher(addr).matches();
            }
        }
        return false;
    }

    /**
     * 是否是有效的IPV4地址
     *
     * @param addr
     * @return IP地址
     */
    public static boolean isValidIPv4(String addr)
    {
        return StringUtil.isValid(addr) && ipv4.matcher(addr).matches();
    }

    /**
     * 是否是有效的IPV6地址
     *
     * @param addr
     * @return IP地址
     */
    public static boolean isValidIPv6(String addr)
    {
        if (StringUtil.isValid(addr)) {
            boolean ipv6Expected = false;
            if (addr.charAt(0) == '[') {
                // This is supposed to be an IPv6 litteral
                if (addr.length() > 2 && addr.charAt(addr.length() - 1) == ']') {
                    addr = addr.substring(1, addr.length() - 1);
                    ipv6Expected = true;
                }
                else {
                    return false;
                }
            }

            return ipv6Expected || addr.indexOf(':') >= 0 && ipv6.matcher(addr).matches();
        }
        return false;
    }

    /**
     * 是否是有效的IP地址
     *
     * @param pattern 模式
     * @param addr    地址
     * @return IP地址
     */
    public static boolean isMatch(String pattern, String addr)
        throws ParseException
    {
        return new PatternMatcher(pattern).isMatch(addr);
    }

    /**
     * 是否是有效的IP地址
     *
     * @param matcher 模式
     * @param addr    地址
     * @return IP地址
     */
    public static boolean isMatch(IPMatcher matcher, String addr)
    {
        return matcher.isMatch(addr);
    }

    /**
     * 是否是有效的IP地址
     *
     * @param matcher 模式
     * @param addr    地址
     * @return IP地址
     */
    public static boolean isMatch(IPMatcher[] matcher, String addr)
    {
        for (int i = 0; i < matcher.length; i++) {
            if (matcher[i].isMatch(addr)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解析字符串模式
     *
     * @param pattern 模式
     * @return 字符串模式
     */
    public static IPMatcher[] toMatchers(String pattern)
        throws ParseException
    {
        return PatternMatcher.parse(pattern);
    }

    /**
     * 解析字符串模式
     *
     * @param pattern 模式
     * @return 字符串模式
     */
    public static IPMatcher[] toMatchers(String pattern, char sep)
        throws ParseException
    {
        return PatternMatcher.parse(pattern, sep);
    }

    /**
     * 解析字符串模式
     *
     * @param pattern 模式
     * @return 字符串模式
     */
    public static IPMatcher[] toMatchers(String[] pattern)
        throws ParseException
    {
        return PatternMatcher.parse(pattern);
    }

    //Import from IPAddressUtil
    private final static int INADDR4SZ = 4;
    private final static int INADDR16SZ = 16;
    private final static int INT16SZ = 2;

    /*
     * Converts IPv4 address in its textual presentation form
     * into its numeric binary form.
     *
     * @param src a String representing an IPv4 address in standard format
     * @return a byte array representing the IPv4 numeric address
     */
    public static byte[] textToNumericFormatV4(String src)
    {
        if (src.length() == 0) {
            return null;
        }

        byte[] res = new byte[INADDR4SZ];
        String[] s = src.split("\\.");
        long val;
        try {
            switch (s.length) {
                case 1:
                    /*
              * When only one part is given, the value is stored directly in
              * the network address without any byte rearrangement.
              */

                    val = Long.parseLong(s[0]);
                    if (val < 0 || val > 0xffffffffL) {
                        return null;
                    }
                    res[0] = (byte)((val >> 24) & 0xff);
                    res[1] = (byte)(((val & 0xffffff) >> 16) & 0xff);
                    res[2] = (byte)(((val & 0xffff) >> 8) & 0xff);
                    res[3] = (byte)(val & 0xff);
                    break;
                case 2:
                    /*
              * When a two part address is supplied, the last part is
              * interpreted as a 24-bit quantity and placed in the right
              * most three bytes of the network address. This makes the
              * two part address format convenient for specifying Class A
              * network addresses as net.host.
              */

                    val = Integer.parseInt(s[0]);
                    if (val < 0 || val > 0xff) {
                        return null;
                    }
                    res[0] = (byte)(val & 0xff);
                    val = Integer.parseInt(s[1]);
                    if (val < 0 || val > 0xffffff) {
                        return null;
                    }
                    res[1] = (byte)((val >> 16) & 0xff);
                    res[2] = (byte)(((val & 0xffff) >> 8) & 0xff);
                    res[3] = (byte)(val & 0xff);
                    break;
                case 3:
                    /*
              * When a three part address is specified, the last part is
              * interpreted as a 16-bit quantity and placed in the right
              * most two bytes of the network address. This makes the
              * three part address format convenient for specifying
              * Class B net- work addresses as 128.net.host.
              */
                    for (int i = 0; i < 2; i++) {
                        val = Integer.parseInt(s[i]);
                        if (val < 0 || val > 0xff) {
                            return null;
                        }
                        res[i] = (byte)(val & 0xff);
                    }
                    val = Integer.parseInt(s[2]);
                    if (val < 0 || val > 0xffff) {
                        return null;
                    }
                    res[2] = (byte)((val >> 8) & 0xff);
                    res[3] = (byte)(val & 0xff);
                    break;
                case 4:
                    /*
              * When four parts are specified, each is interpreted as a
              * byte of data and assigned, from left to right, to the
              * four bytes of an IPv4 address.
              */
                    for (int i = 0; i < 4; i++) {
                        val = Integer.parseInt(s[i]);
                        if (val < 0 || val > 0xff) {
                            return null;
                        }
                        res[i] = (byte)(val & 0xff);
                    }
                    break;
                default:
                    return null;
            }
        }
        catch (NumberFormatException e) {
            return null;
        }
        return res;
    }

    /*
    * Convert IPv6 presentation level address to network order binary form.
    * credit:
    *  Converted from C code from Solaris 8 (inet_pton)
    *
    * Any component of the string following a per-cent % is ignored.
    *
    * @param src a String representing an IPv6 address in textual format
    * @return a byte array representing the IPv6 numeric address
    */
    public static byte[] textToNumericFormatV6(String src)
    {
        // Shortest valid string is "::", hence at least 2 chars
        if (src.length() < 2) {
            return null;
        }

        int colonp;
        char ch;
        boolean saw_xdigit;
        int val;
        char[] srcb = src.toCharArray();
        byte[] dst = new byte[INADDR16SZ];

        int srcb_length = srcb.length;
        int pc = src.indexOf("%");
        if (pc == srcb_length - 1) {
            return null;
        }

        if (pc != -1) {
            srcb_length = pc;
        }

        colonp = -1;
        int i = 0, j = 0;
        /* Leading :: requires some special handling. */
        if (srcb[i] == ':') {
            if (srcb[++i] != ':') {
                return null;
            }
        }
        int curtok = i;
        saw_xdigit = false;
        val = 0;
        while (i < srcb_length) {
            ch = srcb[i++];
            int chval = Character.digit(ch, 16);
            if (chval != -1) {
                val <<= 4;
                val |= chval;
                if (val > 0xffff) {
                    return null;
                }
                saw_xdigit = true;
                continue;
            }
            if (ch == ':') {
                curtok = i;
                if (!saw_xdigit) {
                    if (colonp != -1) {
                        return null;
                    }
                    colonp = j;
                    continue;
                }
                else if (i == srcb_length) {
                    return null;
                }
                if (j + INT16SZ > INADDR16SZ) {
                    return null;
                }
                dst[j++] = (byte)((val >> 8) & 0xff);
                dst[j++] = (byte)(val & 0xff);
                saw_xdigit = false;
                val = 0;
                continue;
            }
            if (ch == '.' && ((j + INADDR4SZ) <= INADDR16SZ)) {
                String ia4 = src.substring(curtok, srcb_length);
                /* check this IPv4 address has 3 dots, ie. A.B.C.D */
                int dot_count = 0, index = 0;
                while ((index = ia4.indexOf('.', index)) != -1) {
                    dot_count++;
                    index++;
                }
                if (dot_count != 3) {
                    return null;
                }
                byte[] v4addr = textToNumericFormatV4(ia4);
                if (v4addr == null) {
                    return null;
                }
                for (int k = 0; k < INADDR4SZ; k++) {
                    dst[j++] = v4addr[k];
                }
                saw_xdigit = false;
                break;    /* '\0' was seen by inet_pton4(). */
            }
            return null;
        }
        if (saw_xdigit) {
            if (j + INT16SZ > INADDR16SZ) {
                return null;
            }
            dst[j++] = (byte)((val >> 8) & 0xff);
            dst[j++] = (byte)(val & 0xff);
        }

        if (colonp != -1) {
            int n = j - colonp;

            if (j == INADDR16SZ) {
                return null;
            }
            for (i = 1; i <= n; i++) {
                dst[INADDR16SZ - i] = dst[colonp + n - i];
                dst[colonp + n - i] = 0;
            }
            j = INADDR16SZ;
        }
        if (j != INADDR16SZ) {
            return null;
        }
        byte[] newdst = convertFromIPv4MappedAddress(dst);
        if (newdst != null) {
            return newdst;
        }
        else {
            return dst;
        }
    }

    /**
     * @param src a String representing an IPv4 address in textual format
     * @return a boolean indicating whether src is an IPv4 literal address
     */
    public static boolean isIPv4LiteralAddress(String src)
    {
        return textToNumericFormatV4(src) != null;
    }

    /**
     * @param src a String representing an IPv6 address in textual format
     * @return a boolean indicating whether src is an IPv6 literal address
     */
    public static boolean isIPv6LiteralAddress(String src)
    {
        return textToNumericFormatV6(src) != null;
    }

    /*
     * Convert IPv4-Mapped address to IPv4 address. Both input and
     * returned value are in network order binary form.
     *
     * @param src a String representing an IPv4-Mapped address in textual format
     * @return a byte array representing the IPv4 numeric address
     */
    public static byte[] convertFromIPv4MappedAddress(byte[] addr)
    {
        if (isIPv4MappedAddress(addr)) {
            byte[] newAddr = new byte[INADDR4SZ];
            System.arraycopy(addr, 12, newAddr, 0, INADDR4SZ);
            return newAddr;
        }
        return null;
    }

    /**
     * Utility routine to check if the InetAddress is an
     * IPv4 mapped IPv6 address.
     *
     * @return a <code>boolean</code> indicating if the InetAddress is
     *         an IPv4 mapped IPv6 address; or false if address is IPv4 address.
     */
    public static boolean isIPv4MappedAddress(byte[] addr)
    {
        if (addr.length < INADDR16SZ) {
            return false;
        }
        if ((addr[0] == 0x00) && (addr[1] == 0x00) &&
            (addr[2] == 0x00) && (addr[3] == 0x00) &&
            (addr[4] == 0x00) && (addr[5] == 0x00) &&
            (addr[6] == 0x00) && (addr[7] == 0x00) &&
            (addr[8] == 0x00) && (addr[9] == 0x00) &&
            (addr[10] == (byte)0xff) &&
            (addr[11] == (byte)0xff)) {
            return true;
        }
        return false;
    }

    /**
     * Utility routine to check if the InetAddress is an
     * IPv4 mapped IPv6 address.
     *
     * @return a <code>boolean</code> indicating if the InetAddress is
     *         an IPv4 mapped IPv6 address; or false if address is IPv4 address.
     */
    public static boolean isIPv4(byte[] addr)
    {
        if (addr.length == INADDR4SZ) {
            return true;
        }
        else if (addr.length < INADDR16SZ) {
            return false;
        }
        if ((addr[0] == 0x00) && (addr[1] == 0x00) &&
            (addr[2] == 0x00) && (addr[3] == 0x00) &&
            (addr[4] == 0x00) && (addr[5] == 0x00) &&
            (addr[6] == 0x00) && (addr[7] == 0x00) &&
            (addr[8] == 0x00) && (addr[9] == 0x00) &&
            (((addr[10] == (byte)0xff) &&
              (addr[11] == (byte)0xff)) || ((addr[10] == 0x00) &&
                                            (addr[11] == 0x00)))) {
            return true;
        }
        return false;
    }

    public static void main(String[] args)
    {
        System.out.println(toInts("192.168.6.233"));
    }
}
