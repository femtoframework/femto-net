package org.femtoframework.text.matcher;

import java.io.Serializable;

/**
 * 等效匹配器
 *
 * @author fengyun
 * @version 1.00 Mar 28, 2006 6:19:59 PM
 */
public class EqualMatcher<O extends Comparable>
    implements Matcher<O>, Serializable, Comparable<EqualMatcher<O>>
{
    private O value;

    public EqualMatcher(O v)
    {
        setValue(v);
    }

    public EqualMatcher()
    {
    }

    /**
     * 判断给定的字符是否匹配
     *
     * @param c 字符
     * @return 判断给定的字符是否匹配
     */
    public boolean isMatch(O c)
    {
        return c.equals(value);
    }

    /**
     * 返回值
     *
     * @return 值
     */
    public O getValue()
    {
        return value;
    }

    /**
     * 设置值
     *
     * @param value 值
     */
    public void setValue(O value)
    {
        this.value = value;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.<p>
     * <p/>
     * In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of <i>expression</i>
     * is negative, zero or positive.
     * <p/>
     * The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)<p>
     * <p/>
     * The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.<p>
     * <p/>
     * Finally, the implementer must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.<p>
     * <p/>
     * It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * @param m the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     * @throws ClassCastException if the specified object's type prevents it
     *                            from being compared to this Object.
     */
    public int compareTo(EqualMatcher<O> m)
    {
        return value.compareTo(m.value);
    }
}
