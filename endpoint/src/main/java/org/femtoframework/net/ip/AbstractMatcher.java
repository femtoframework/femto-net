package org.femtoframework.net.ip;

/**
 * @author fengyun
 * @version 1.00 Sep 30, 2003 1:56:15 PM
 */
public abstract class AbstractMatcher
    implements IPMatcher
{
    /**
     * 判断指定的地址是否匹配
     *
     * @param address IP地址，如果不是合法的IP地址(xxx.xxx.xxx.xxx)，返回<CODE>fasle</CODE>
     * @return 是否匹配
     */
    public boolean isMatch(String address)
    {
        return isMatch(IPUtil.toInts(address));
    }

    /**
     * 判断指定的地址是否匹配
     *
     * @param address 用一个整数表示的IP地址（适用于报文检查）
     */
    public boolean isMatch(int address)
    {
        return isMatch(IPUtil.toBytes(address));
    }

    /**
     * 判断指定的地址是否匹配
     *
     * @param address IP地址，长度必需为4或者16，否则返回<CODE>false</CODE>
     */
    public boolean isMatch(byte[] address)
    {
        return isMatch(IPUtil.toInts(address));
    }
}
