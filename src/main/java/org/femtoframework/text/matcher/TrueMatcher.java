package org.femtoframework.text.matcher;

import java.io.Serializable;

/**
 * 所有的都匹配
 *
 * @author fengyun
 * @version 1.00 Aug 9, 2003 5:48:29 AM
 */
public class TrueMatcher<O>
    implements Matcher<O>, Serializable
{
    private static final TrueMatcher INSTANCE = new TrueMatcher();

    private TrueMatcher()
    {
    }

    public static <O> Matcher<O> getInstance()
    {
        return (Matcher<O>)INSTANCE;
    }

    /**
     * 判断给定的字符是否匹配
     *
     * @param c 字符
     * @return 判断给定的字符是否匹配
     */
    public boolean isMatch(O c)
    {
        return true;
    }

    private Object readResolve()
    {
        return INSTANCE;
    }
}
