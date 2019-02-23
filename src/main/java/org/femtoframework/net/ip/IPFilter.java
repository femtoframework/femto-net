package org.femtoframework.net.ip;

import org.femtoframework.text.matcher.EqualMatcher;
import org.femtoframework.text.matcher.Matcher;
import org.femtoframework.util.DataUtil;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * IP过滤器节点
 *
 * @author fengyun
 * @version 1.00 Sep 30, 2003 11:58:24 AM
 */
public class IPFilter
    extends AbstractMatcher
{
    /**
     * 确定数字的序列
     */
    private EqualMatcher<Integer>[] equals;

    /**
     * 序列长度
     */
    private int equalCount = 0;


    /**
     * 需要匹配的数组
     */
    private Matcher<Integer>[] matchers;

    /**
     * 匹配器的长度
     */
    private int matcherCount = 0;


    /**
     * 匹配器到下一个节点的映射
     */
    private Map<Matcher<Integer>, IPFilter> matcher2Node = new HashMap<Matcher<Integer>, IPFilter>(4);

    /**
     * 增加相等的匹配器
     *
     * @param matcher
     */
    private synchronized void addEqual(EqualMatcher<Integer> matcher)
    {
        if (equals == null) {
            equals = (EqualMatcher<Integer>[])new EqualMatcher[8];
        }

        if (equalCount >= equals.length) {
            EqualMatcher<Integer>[] newEquals
                = (EqualMatcher<Integer>[])new EqualMatcher[equals.length * 2];
            System.arraycopy(equals, 0, newEquals, 0, equalCount);
            equals = newEquals;
        }
        int low = 0;
        int high = equalCount - 1;

        while (low <= high) {
            int mid = (low + high) >> 1;
            EqualMatcher<Integer> midVal = equals[mid];
            int cmp = midVal.compareTo(matcher);

            if (cmp < 0) {
                low = mid + 1;
            }
            else if (cmp > 0) {
                high = mid - 1;
            }
            else {
                return;
            }
        }
        //插入到low位置
        int left = equalCount - low;
        if (left > 0) {
            System.arraycopy(equals, low, equals, low + 1, left);
        }
        equals[low] = matcher;
        equalCount++;
    }

    /**
     * 增加相等的匹配器
     *
     * @param matcher
     */
    private synchronized void addMatcher0(Matcher<Integer> matcher)
    {
        if (matchers == null) {
            matchers = (Matcher<Integer>[])new Matcher[4];
        }

        if (matcherCount >= matchers.length) {
            Matcher<Integer>[] newMatchers = (Matcher<Integer>[])new Matcher[matchers.length * 2];
            System.arraycopy(matchers, 0, newMatchers, 0, matcherCount);
            matchers = newMatchers;
        }
        matchers[matcherCount++] = matcher;
    }

    /**
     * 增加相等的匹配器
     *
     * @param matcher
     */
    public IPFilter addMatcher(Matcher<Integer> matcher)
    {
        if (matcher instanceof EqualMatcher) {
            addEqual((EqualMatcher<Integer>)matcher);
        }
        else {
            addMatcher0(matcher);
        }
        synchronized (matcher2Node) {
            IPFilter node = (IPFilter)matcher2Node.get(matcher);
            if (node == null) {
                node = new IPFilter();
            }
            matcher2Node.put(matcher, node);
            return node;
        }
    }

    /**
     * 增加相等的匹配器
     *
     * @param im
     */
    public void addMatchers(Matcher<Integer>[] im)
    {
        addMatchers(im, 0);
    }

    /**
     * 增加相等的匹配器
     *
     * @param pattern IP地址模式（单
     */
    public void addPattern(String pattern)
        throws ParseException
    {
        if (pattern.indexOf(';') > 0) {
            String[] patterns = DataUtil.toStrings(pattern, ';');
            for (int i = 0, len = patterns.length; i < len; i++) {
                addPattern0(patterns[i]);
            }
        }
        else {
            addPattern0(pattern);
        }
    }

    private void addPattern0(String pattern)
        throws ParseException
    {
        Matcher<Integer>[] im = PatternMatcher.toIntMatchers(pattern);
        addMatchers(im);
    }

    /**
     * 增加相等的匹配器
     *
     * @param matchers
     * @param i        偏址
     */
    private void addMatchers(Matcher<Integer>[] matchers, int i)
    {
        IPFilter next = addMatcher(matchers[i++]);
        if (i < matchers.length) {
            next.addMatchers(matchers, i);
        }
    }

    /**
     * 判断指定的IP地址值是否有匹配的
     *
     * @param ip IP地址值
     * @return 是否有匹配的
     */
    public boolean match(String ip)
    {
        int[] values = IPUtil.toInts(ip);
        if (values == null) {
            return false;
        }
        return isMatch(values);
    }


    /**
     * 判断指定的地址是否匹配
     *
     * @param address IP地址，长度必需为4，否则返回<CODE>false</CODE>
     */
    public boolean isMatch(int[] address)
    {
        return match(address, 0);
    }

    /**
     * 判断指定的IP地址值是否有匹配的
     *
     * @param values IP地址值
     * @param i      偏址
     * @return 是否有匹配的
     */
    private boolean match(int[] values, int i)
    {
        Matcher<Integer> matcher = match(values[i++]);
        if (matcher != null) {
            if (i < values.length) {
                IPFilter node = (IPFilter)matcher2Node.get(matcher);
                if (node != null) {
                    return node.match(values, i);
                }
            }
            else {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断指定的值是否匹配
     *
     * @param value 值
     * @return 是否匹配
     */
    public Matcher<Integer> match(int value)
    {
        if (equals != null) {
            int low = 0;
            int high = equalCount - 1;
            EqualMatcher<Integer>[] em = equals;

            while (low <= high) {
                int mid = (low + high) >> 1;
                EqualMatcher<Integer> midVal = em[mid];
                int cmp = midVal.getValue() - value;
                if (cmp < 0) {
                    low = mid + 1;
                }
                else if (cmp > 0) {
                    high = mid - 1;
                }
                else {
                    return midVal;
                }
            }
        }

        if (matchers != null) {
            Matcher<Integer>[] im = matchers;
            int count = matcherCount;
            for (int i = 0; i < count; i++) {
                if (im[i].isMatch(value)) {
                    return im[i];
                }
            }
        }

        return null;
    }

//    public static void main(String[] args)
//        throws Exception
//    {
//        IPFilter filter = new IPFilter();
//        LineInputStream lis = new LineInputStream(new FileInputStream(args[0]));
//        String line = null;
//        while((line = lis.readLine()) != null) {
//            filter.addPattern(line);
//        }
//        System.out.println("Is Match:" + filter.isMatch(args[1]));
//        if (args.length > 3) {
//            System.out.println("Is Match:" + filter.isMatch(args[2]));
//        }
//    }
}
