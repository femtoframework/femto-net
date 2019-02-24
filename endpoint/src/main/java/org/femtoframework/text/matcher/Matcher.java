package org.femtoframework.text.matcher;

/**
 * 匹配器
 *
 * @author fengyun
 * @version 1.00 Mar 28, 2006 5:32:14 PM
 */
public interface Matcher<O>
{
    /**
     * 判断给定的字符是否匹配
     *
     * @param c 字符
     * @return 判断给定的字符是否匹配
     */
    boolean isMatch(O c);
}
