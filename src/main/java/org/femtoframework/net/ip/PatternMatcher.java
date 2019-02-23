package org.femtoframework.net.ip;

import org.femtoframework.text.matcher.*;
import org.femtoframework.util.ArrayUtil;
import org.femtoframework.util.CharUtil;
import org.femtoframework.util.DataUtil;
import org.femtoframework.util.StringUtil;

import java.text.ParseException;

/**
 * 抽象的IP地址匹配器
 * <p/>
 * <pre>
 * IP地址规则匹配工具
 * 说明：
 * 多个IP地址规则用';'分隔
 * 允许模式：
 * 211.95.116.131        完整IP地址
 * 下面针对最后一个字节的规则同样可以应用于任何一个字节，任何字节都是相互独立的
 *                     所有的IP地址
 * 211.95.116.*          IP子网
 * 211.95.116            IP子网
 * 211.95.116.           IP子网
 * 211.95.116.7?         单个字符匹配
 * 211.95.116.131-133    区段IP
 * 211.95.116.1,3,5      多个IP
 * <p/>
 * IPV6
 * <p/>
 * 如果是IPV6，那么都采用':'分隔的模式，字符采用16进制，如果用'::'进行缩写，那么不能省略后面的部分
 * 1080::8:800:200C:*          IP子网
 * 1080:0:0:0:8:800:200C       IP子网
 * 1080:0:0:0:8:800:20??       IP子网
 * 1080::8:800:200C,200A       多个IP
 * 1080::8:800:2000-200A       区段IP
 * <p/>
 * </pre>
 *
 * @author fengyun
 * @version 2.0 增加了IPV6的支持
 *          1.00 Aug 9, 2003 5:40:34 AM
 */
public class PatternMatcher implements IPMatcher
{
    protected Matcher<Integer>[] matchers = null;

    /**
     * IP地址模式匹配器
     *
     * @param pattern IP地址模式
     */
    public PatternMatcher(String pattern)
        throws ParseException
    {
        parse0(pattern);
    }

    /**
     * 解析模式
     *
     * @param pattern 模式
     */
    protected void parse0(String pattern)
        throws ParseException
    {
        matchers = toIntMatchers(pattern);
    }

    private static EqualMatcher<Integer> ZERO = new EqualMatcher<Integer>(0);

    private static void fillZero(Matcher<Integer>[] matchers, int off, int end)
    {
        for (int i = off; i < end; i++) {
            matchers[i] = ZERO;
        }
    }

    private static void fillTrue(Matcher<Integer>[] matchers, int off, int end)
    {
        for (int i = off; i < end; i++) {
            matchers[i] = TrueMatcher.getInstance();
        }
    }

    private static void fillInt(Matcher<Integer>[] matchers, int end, String[] array)
        throws ParseException
    {
        fillInt(matchers, 0, end, array, 0);
    }

    private static void fillInt(Matcher<Integer>[] matchers, int off, int end, String[] array, int s)
        throws ParseException
    {
        for (int i = off; i < end; i++) {
            matchers[i] = toIntMatcher(array[s++], true);
        }
    }

    private static void check(String[] array, String pattern)
        throws ParseException
    {
        if (array.length == 3) {
            return;
        }
        int index = -1;
        boolean found = false;
        for (int i = 0; i < array.length; i++) {
            if (array[i].length() == 0) {
                if (found) {
                    throw new ParseException("Invalid pattern:" + pattern, 0);
                }
                if (index == 0 || index == array.length - 2) {
                    if (i == 1 || i == array.length - 1) {
                        found = true;
                    }
                    else {
                        throw new ParseException("Invalid pattern:" + pattern, 0);
                    }
                }
                else {
                    index = i;
                    found = true;
                }
            }
        }

        if (array.length == 8 && found) {
            throw new ParseException("Invalid pattern:" + pattern, 0);
        }
    }

    /**
     * 将IP地址模式变成整数匹配器
     *
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static Matcher<Integer>[] toIntMatchers(String pattern)
        throws ParseException
    {
        if (StringUtil.isInvalid(pattern)) {
            throw new ParseException("Invalid pattern:" + pattern, 0);
        }
        if (pattern.indexOf(':') >= 0) {
            //IPV6
            String[] array = DataUtil.toStrings(pattern, ':');
            if (array.length > 8 || array.length < 3) {
                throw new ParseException("Invalid pattern:" + pattern, 0);
            }

            check(array, pattern);

            Matcher<Integer>[] matchers = new Matcher[8];
            int len = array.length;
            if (len == 3) {
                if (array[0].length() == 0) {
                    if (array[1].length() == 0) { //::X
                        fillZero(matchers, 0, 7);
                        matchers[7] = toIntMatcher(array[2], true);
                    }
                    else {
                        throw new ParseException("Invalid pattern:" + pattern, 1);
                    }
                }
                else if (array[2].length() == 0) {
                    if (array[1].length() == 0) { //X::
                        matchers[0] = toIntMatcher(array[0], true);
                        fillZero(matchers, 1, 8);
                    }
                    else {
                        throw new ParseException("Invalid pattern:" + pattern, 0);
                    }
                }
                else { //X:X:X
                    fillInt(matchers, 3, array);
                    fillTrue(matchers, 3, 8);
                }
            }
            else if (len == 4) {
                if (array[0].length() == 0) {
                    if (array[1].length() == 0) { //::X:X
                        fillZero(matchers, 0, 6);
                        fillInt(matchers, 6, 8, array, 2);
                    }
                    else {
                        throw new ParseException("Invalid pattern:" + pattern, 1);
                    }
                }
                else if (array[1].length() == 0) { //X::X:X
                    matchers[0] = toIntMatcher(array[0], true);
                    fillZero(matchers, 1, 6);
                    fillInt(matchers, 6, 8, array, 2);
                }
                else if (array[3].length() == 0) {
                    if (array[2].length() == 0) { //X:X::
                        fillInt(matchers, 2, array);
                        fillZero(matchers, 2, 8);
                    }
                    else {
                        throw new ParseException("Invalid pattern:" + pattern, 1);
                    }
                }
                else if (array[2].length() == 0) { //X:X::X
                    fillInt(matchers, 2, array);
                    fillZero(matchers, 2, 7);
                    matchers[7] = toIntMatcher(array[3], true);
                }
                else { //X:X:X:X
                    fillInt(matchers, 4, array);
                    fillTrue(matchers, 4, 8);
                }
            }
            else if (len == 5) {
                if (array[0].length() == 0) {
                    if (array[1].length() == 0) { //::X:X:X
                        fillZero(matchers, 0, 5);
                        fillInt(matchers, 5, 8, array, 2);
                    }
                    else {
                        throw new ParseException("Invalid pattern:" + pattern, 1);
                    }
                }
                else if (array[1].length() == 0) { //X::X:X:X
                    matchers[0] = toIntMatcher(array[0], true);
                    fillZero(matchers, 1, 5);
                    fillInt(matchers, 5, 8, array, 2);
                }
                else if (array[2].length() == 0) { //X:X::X:X
                    fillInt(matchers, 2, array);
                    fillZero(matchers, 2, 6);
                    fillInt(matchers, 6, 8, array, 3);
                }
                else if (array[4].length() == 0) {
                    if (array[3].length() == 0) { //X:X:X::
                        fillInt(matchers, 3, array);
                        fillZero(matchers, 3, 8);
                    }
                    else {
                        throw new ParseException("Invalid pattern:" + pattern, 1);
                    }
                }
                else if (array[3].length() == 0) { //X:X:X::X
                    fillInt(matchers, 3, array);
                    fillZero(matchers, 3, 7);
                    matchers[7] = toIntMatcher(array[4], true);
                }
                else { //X:X:X:X:X
                    fillInt(matchers, 5, array);
                    fillTrue(matchers, 5, 8);
                }
            }
            else if (len == 6) {
                if (array[0].length() == 0) {
                    if (array[1].length() == 0) { //::X:X:X:X
                        fillZero(matchers, 0, 4);
                        fillInt(matchers, 4, 8, array, 2);
                    }
                    else {
                        throw new ParseException("Invalid pattern:" + pattern, 1);
                    }
                }
                else if (array[1].length() == 0) { //X::X:X:X:X
                    matchers[0] = toIntMatcher(array[0], true);
                    fillZero(matchers, 1, 4);
                    fillInt(matchers, 4, 8, array, 2);
                }
                else if (array[2].length() == 0) { //X:X::X:X:X
                    fillInt(matchers, 2, array);
                    fillZero(matchers, 2, 5);
                    fillInt(matchers, 5, 8, array, 3);
                }
                else if (array[3].length() == 0) { //X:X:X::X:X
                    fillInt(matchers, 3, array);
                    fillZero(matchers, 3, 6);
                    fillInt(matchers, 6, 8, array, 4);
                }
                else if (array[5].length() == 0) {
                    if (array[4].length() == 0) { //X:X:X:X::
                        fillInt(matchers, 4, array);
                        fillZero(matchers, 4, 8);
                    }
                    else {
                        throw new ParseException("Invalid pattern:" + pattern, 1);
                    }
                }
                else if (array[4].length() == 0) { //X:X:X:X::X
                    fillInt(matchers, 4, array);
                    fillZero(matchers, 4, 7);
                    matchers[7] = toIntMatcher(array[5], true);
                }
                else { //X:X:X:X:X:X
                    fillInt(matchers, 6, array);
                    fillTrue(matchers, 6, 8);
                }
            }
            else if (len == 7) {
                if (array[0].length() == 0) {
                    if (array[1].length() == 0) { //::X:X:X:X:X
                        fillZero(matchers, 0, 3);
                        fillInt(matchers, 3, 8, array, 2);
                    }
                    else {
                        throw new ParseException("Invalid pattern:" + pattern, 1);
                    }
                }
                else if (array[1].length() == 0) { //X::X:X:X:X:X
                    matchers[0] = toIntMatcher(array[0], true);
                    fillZero(matchers, 1, 3);
                    fillInt(matchers, 3, 8, array, 2);
                }
                else if (array[2].length() == 0) { //X:X::X:X:X:X
                    fillInt(matchers, 2, array);
                    fillZero(matchers, 2, 4);
                    fillInt(matchers, 4, 8, array, 3);
                }
                else if (array[3].length() == 0) { //X:X:X::X:X:X
                    fillInt(matchers, 3, array);
                    fillZero(matchers, 3, 5);
                    fillInt(matchers, 5, 8, array, 4);
                }
                else if (array[4].length() == 0) { //X:X:X:X::X:X
                    fillInt(matchers, 4, array);
                    fillZero(matchers, 4, 6);
                    fillInt(matchers, 6, 8, array, 5);
                }
                else if (array[6].length() == 0) {
                    if (array[5].length() == 0) { //X:X:X:X:X::
                        fillInt(matchers, 5, array);
                        fillZero(matchers, 5, 8);
                    }
                    else {
                        throw new ParseException("Invalid pattern:" + pattern, 1);
                    }
                }
                else if (array[5].length() == 0) { //X:X:X:X:X::X
                    fillInt(matchers, 5, array);
                    fillZero(matchers, 5, 7);
                    matchers[7] = toIntMatcher(array[6], true);
                }
                else { //X:X:X:X:X:X:X
                    fillInt(matchers, 7, array);
                    fillTrue(matchers, 7, 8);
                }
            }
            else {
                fillInt(matchers, 8, array);
            }
            return matchers;
        }
        else {
            String[] array = DataUtil.toStrings(pattern, '.');
            if (array.length > 4) {
                throw new ParseException("Invalid pattern:" + pattern, 0);
            }

            Matcher<Integer>[] matchers = new Matcher[4];
            int len = array.length;
            if (len == 1) {
                if ("*".equals(array[0])) {
                    matchers[0] = TrueMatcher.getInstance();
                }
                else {
                    matchers[0] = toIntMatcher(array[0], false);
                }
                fillTrue(matchers, 1, 4);
            }
            else if (len == 2) {
                matchers[0] = toIntMatcher(array[0], false);
                matchers[1] = toIntMatcher(array[1], false);
                fillTrue(matchers, 2, 4);
            }
            else if (len == 3) {
                matchers[0] = toIntMatcher(array[0], false);
                matchers[1] = toIntMatcher(array[1], false);
                matchers[2] = toIntMatcher(array[2], false);
                matchers[3] = TrueMatcher.getInstance();
            }
            else {
                matchers[0] = toIntMatcher(array[0], false);
                matchers[1] = toIntMatcher(array[1], false);
                matchers[2] = toIntMatcher(array[2], false);
                matchers[3] = toIntMatcher(array[3], false);
            }
            return matchers;
        }
    }

    /**
     * 将单个字段转成匹配器
     *
     * @param str 字符串
     * @return 匹配器
     */
    public static Matcher<Integer> toIntMatcher(String str)
        throws ParseException
    {
        return toIntMatcher(str, false);
    }

    private static int getInt(String str, int defaultValue, boolean hex)
    {
        if (hex) {
            try {
                return Integer.parseInt(str, 16);
            }
            catch (RuntimeException ioe) {
                return defaultValue;
            }
        }
        else {
            return DataUtil.getInt(str, defaultValue);
        }
    }

    /**
     * 将单个字段转成匹配器
     *
     * @param str 字符串
     * @return 匹配器
     */
    public static Matcher<Integer> toIntMatcher(String str, boolean hex)
        throws ParseException
    {
        if (str == null || str.length() == 0 || "*".equals(str)) {
            return TrueMatcher.getInstance();
        }

        int i = str.indexOf('-');
        if (i != -1) {
            //131-132
            int min = getInt(str.substring(0, i), -1, hex);
            if (!(min >= 0 &&  (hex || min <= 255))) {
                throw new ParseException("Invalid min of the range", 0);
            }
            int max = getInt(str.substring(i + 1), -1, hex);
            if (!(max >= min && (hex || min <= 255))) {
                throw new ParseException("Invalid max of the range", 0);
            }
            return new RangeMatcher<Integer>(min, max);
        }
        i = str.indexOf(',');
        if (i != -1) {
            //1,3,5
            int[] array = hex ? toHexIntArray(str, ',') : DataUtil.toInts(str, ',');
            if (array != null) {
                return new IntArrayMatcher(array);
            }
            else {
                throw new ParseException("Invalid separator format:" + str, 0);
            }
        }
        i = str.indexOf('?');
        if (i != -1) {
            return toIntMatcher0(str, hex);
        }
        i = str.indexOf('*');
        if (i != -1) {
            return toIntMatcher0(str, hex);
        }
        i = getInt(str, -1, hex);
        if (i == -1) {
            throw new ParseException("Invalid number:" + str, 0);
        }
        return new EqualMatcher<>(i);
    }

    /**
     * Separate the string to array
     * if src == null return null;
     * ""     --> {0}
     * ",1,2" --> {0, 1, 2}
     * "1,,2," --> {1,0,2,0}
     * "a,1,2," --> null (Exception)
     */
    public static int[] toHexIntArray(String src, char sep)
    {
        if (src == null) {
            return null;
        }
        int length = src.length();
        if (length == 0) {
            return new int[]{0};
        }

        int count = DataUtil.countChar(src, sep);
        int[] array = new int[count + 1];
        int i = 0;  //index of array
//        int begin = 0;
        int value = 0;
        boolean neg = false;
        int index = 0; //index of src
        char c;
        while (index < length) {
            c = src.charAt(index);
            if (CharUtil.isHex(c)) {
                value = value << 4 + Character.digit(c, 16);
            }
            else if (c == sep) {
                array[i++] = neg ? -value : value;
                value = 0;
                neg = false;
            }
            else if (c == '-') {
                if (value == 0) {
                    neg = true;
                }
                else {
                    //格式错误，返回
                    return null;
                }
            }
            else if (c == ' ' || c == '\t') {
                index++;
                continue;
            }
            else {
                return null;
            }
            index++;
        }
        array[i] = neg ? -value : value;
        return array;
    }

    private static class D implements Matcher<Integer>
    {
        private int[] array;

        public D(String pattern) throws ParseException
        {
            int l = pattern.length();
            if (l > 3) {
                throw new ParseException("Invalid character:" + pattern, 0);
            }
            array = new int[l];
            char ch;
            for (int i = 0; i < l; i++) {
                ch = pattern.charAt(i);
                if (ch == '?') {
                    array[i] = -1;
                }
                else if (ch == '*') {
                    array[i] = -2;
                }
                else if (Character.isDigit(ch)) {
                    array[i] = Character.digit(ch, 10);
                }
                else {
                    throw new ParseException("Invalid character:" + ch, i);
                }
            }
        }

        private boolean match(int n, Integer c)
        {
            return (n == -1 || n == -2 || n == c);
        }

        /**
         * 判断给定的字符是否匹配
         *
         * @param c 字符
         * @return 判断给定的字符是否匹配
         */
        public boolean isMatch(Integer c)
        {
            if (array.length == 1) {
                return (c >= 0 && c <= 9) && match(array[0], c);
            }
            else if (array.length == 2) {
                if (c >= 10 && c < 100) {
                    if (array[0] == -1) {
                        return match(array[1], c % 10);
                    }
                    else {
                        return match(array[0], (c / 10) % 10) && match(array[1], c % 10);
                    }
                }
                else if (c >= 100 && c <= 255) {
                    if (array[0] == -2) { // *X
                        return match(array[1], c % 10);
                    }
                    else if (array[1] == -2) { // X*
                        return match(array[0], c / 100);
                    }
                }
            }
            else if (array.length == 3) {
                if (c >= 100 && c <= 255) {
                    if (array[0] == -1) {
                        return match(array[1], (c / 10) % 10) && match(array[2], c % 10);
                    }
                    else {
                        return match(array[0], c / 100) && match(array[1], (c / 10) % 10) && match(array[2], c % 10);
                    }
                }
            }
            return false;
        }
    }

    private static class H implements Matcher<Integer>
    {
        private int[] array;

        public H(String pattern) throws ParseException
        {
            int l = pattern.length();
            if (l > 4) {
                throw new ParseException("Invalid character:" + pattern, 0);
            }
            array = new int[l];
            char ch;
            for (int i = 0; i < l; i++) {
                ch = pattern.charAt(i);
                if (ch == '?' || ch == '*') {
                    array[i] = -1;
                }
                else if (CharUtil.isHex(ch)) {
                    array[i] = Character.digit(ch, 16);
                }
                else {
                    throw new ParseException("Invalid character:" + ch, i);
                }
            }
        }

        private boolean match(int n, Integer c)
        {
            return (n == -1 || n == c);
        }

        /**
         * 判断给定的字符是否匹配
         *
         * @param c 字符
         * @return 判断给定的字符是否匹配
         */
        public boolean isMatch(Integer c)
        {
            if (array.length == 1) {
                return (c >= 0 && c <= 0xF) && match(array[0], c);
            }
            else if (array.length == 2) {
                if (c >= 0x10 && c < 0x100) {
                    return match(array[0], (c >> 4) & 0xF) && match(array[1], c & 0xF);
                }
                else if (c >= 0x100) {
                    if (array[0] == -2) { // *X
                        return match(array[1], c & 0xF);
                    }
                    else if (array[1] == -2) { // X*
                        return match(array[0], (c >> 8) & 0xF) || match(array[0], (c >> 12) & 0xF);
                    }
                }
            }
            else if (array.length == 3) {
                if (c >= 0x100 && c < 0x1000) {
                    return match(array[0], (c >> 8) & 0xF) && match(array[1], (c >> 4) & 0xF) &&
                           match(array[2], c & 0xF);

                }
                else if (c >= 0x1000) {
                    if (array[0] == -2) { // *XX
                        return match(array[1], (c >> 4) & 0xF) && match(array[2], c & 0xF);
                    }
                    else if (array[1] == -2) { // X*X
                        return match(array[0], (c >> 12) & 0xF) && match(array[2], c & 0xF);
                    }
                    else if (array[2] == -2) { // XX*
                        return match(array[0], (c >> 12) & 0xF) && match(array[1], (c >> 8) & 0xF);
                    }
                }
            }
            else if (array.length == 4) {
                if (c >= 0x1000 && c < 0x10000) {
                    return match(array[0], (c >> 12) & 0xF) && match(array[1], (c >> 8) & 0xF) &&
                           match(array[2], (c >> 4) & 0xF) && match(array[3], c & 0xF);
                }
            }
            return false;
        }
    }

    private static Matcher<Integer> toIntMatcher0(String str, boolean hex)
        throws ParseException
    {
        return hex ? new H(str) : new D(str);
    }

    /**
     * 判断指定的地址是否匹配
     *
     * @param address IP地址，长度必需为4或者16，否则返回<CODE>false</CODE>
     */
    public boolean isMatch(byte[] address)
    {
        if (address != null) {
            if (address.length == 4 && matchers.length == 4) {
                return matchers[0].isMatch((int) address[0] & 0xFF)
                       && matchers[1].isMatch((int) address[1] & 0xFF)
                       && matchers[2].isMatch((int) address[2] & 0xFF)
                       && matchers[3].isMatch((int) address[3] & 0xFF);
            }
            else {
                return IPMatcher.super.isMatch(address);
            }
        }
        else {
            return false;
        }
    }

    /**
     * 判断指定的地址是否匹配
     *
     * @param address IP地址，长度必需为4或者8，如果是8表示IPV6，否则返回<CODE>false</CODE>
     */
    public boolean isMatch(int[] address)
    {
        if (address != null) {
            if (address.length == matchers.length) {
                for (int i = 0; i < address.length; i++) {
                    if (!matchers[i].isMatch(address[i])) {
                        return false;
                    }
                }
                return true;
            }
            else if (address.length == 4 || address.length == 8) {
                if (address.length == 4) { // Matchers length is 8
                    return matchers[0].isMatch(0) && matchers[1].isMatch(0) && matchers[2].isMatch(0) &&
                           matchers[3].isMatch(0)
                           && matchers[4].isMatch(0) && (matchers[5].isMatch(0) || matchers[5].isMatch(0xFFFF))
                           && matchers[6].isMatch(((address[0] << 8) & 0xFF00) | (address[1] & 0xFF))
                           && matchers[7].isMatch(((address[2] << 8) & 0xFF00) | (address[3] & 0xFF));
                }
                else { //Matchers length is 4
                    return address[0] == 0 && address[1] == 0 && address[2] == 0
                           && address[3] == 0 && address[4] == 0
                           && (address[5] == 0 || address[5] == 0xFFFF) && matchers[0].isMatch((address[6] >> 8) & 0xFF)
                           && matchers[1].isMatch(address[6] & 0xFF)
                           && matchers[2].isMatch((address[7] >> 8) & 0xFF)
                           && matchers[3].isMatch(address[7] & 0xFF);
                }
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    /**
     * 解析字符串模式
     *
     * @param pattern 模式
     * @return 字符串模式
     */
    public static IPMatcher[] parse(String pattern)
        throws ParseException
    {
        return parse(pattern, ';');
    }

    /**
     * 解析字符串模式
     *
     * @param pattern 模式
     * @return 字符串模式
     */
    public static IPMatcher[] parse(String pattern, char sep)
        throws ParseException
    {
        String[] array = DataUtil.toStrings(pattern, sep);
        return parse(array);
    }

    /**
     * 解析字符串模式
     *
     * @param pattern 模式
     * @return 字符串模式
     */
    public static IPMatcher[] parse(String[] pattern)
        throws ParseException
    {
        if (ArrayUtil.isInvalid(pattern)) {
            return null;
        }
        IPMatcher[] matchers = new IPMatcher[pattern.length];
        for (int i = 0; i < matchers.length; i++) {
            matchers[i] = new PatternMatcher(pattern[i]);
        }
        return matchers;
    }


    public static void main(String[] args)
        throws Exception
    {
        PatternMatcher matcher = new PatternMatcher("202.22-99.22.1*");
        System.out.println(matcher.isMatch("202.98.22.199"));
        System.out.println(matcher.isMatch("203.98.22.199"));
        System.out.println(matcher.isMatch("202.97.22.199"));
    }
}
