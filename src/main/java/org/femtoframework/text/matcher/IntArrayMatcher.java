package org.femtoframework.text.matcher;

import org.femtoframework.util.ArrayUtil;

/**
 * 整数数组匹配器
 *
 * @author fengyun
 * @version 1.00 Mar 28, 2006 6:35:57 PM
 */
public class IntArrayMatcher
    implements Matcher<Integer>
{
    private int[] array;

    /**
     * 构造
     */
    public IntArrayMatcher()
    {
    }

    /**
     * 构造
     *
     * @param array
     */
    public IntArrayMatcher(int[] array)
    {
        this.array = array;
    }

    /**
     * 判断给定的整数是否匹配
     *
     * @param i 整数
     * @return 是否匹配
     */
    public boolean isMatch(Integer i)
    {
        return ArrayUtil.search(array, i) >= 0;
    }

    public int[] getArray()
    {
        return array;
    }

    public void setArray(int[] array)
    {
        this.array = array;
    }
}
