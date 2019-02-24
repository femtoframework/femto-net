package org.femtoframework.text.matcher;

import java.io.Serializable;

/**
 * 字节区段匹配
 *
 * @author fengyun
 * @version 1.00 Aug 9, 2003 5:10:03 AM
 */
public class RangeMatcher<N extends Comparable>
    implements Matcher<N>, Serializable
{
    private N min;
    private N max;

    /**
     * 构造
     */
    public RangeMatcher()
    {
    }

    /**
     * 构造
     *
     * @param min 最小值
     * @param max
     */
    public RangeMatcher(N min, N max)
    {
        this.min = min;
        this.max = max;
    }

    /**
     * HashCode
     *
     * @return HashCode
     */
    public int hashCode()
    {
        return min.hashCode() + max.hashCode();
    }

    /**
     * 是否等效
     *
     * @param obj
     * @return 是否等效
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof RangeMatcher) {
            RangeMatcher matcher = (RangeMatcher)obj;
            return min == matcher.min && max == matcher.max;
        }
        return false;
    }

    public N getMin()
    {
        return min;
    }

    public void setMin(N min)
    {
        this.min = min;
    }

    public N getMax()
    {
        return max;
    }

    public void setMax(N max)
    {
        this.max = max;
    }

    /**
     * 判断给定的字符是否匹配
     *
     * @param c 字符
     * @return 判断给定的字符是否匹配
     */
    public boolean isMatch(N c)
    {
        int i = c.compareTo(min);
        if (i >= 0) {
            i = c.compareTo(max);
            return i <= 0;
        }
        return false;
    }
}
