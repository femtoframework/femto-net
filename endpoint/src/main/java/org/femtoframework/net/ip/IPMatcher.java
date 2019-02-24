package org.femtoframework.net.ip;

/**
 * IP地址匹配接口
 *
 * @author fengyun
 * @version 1.00 Aug 9, 2003 5:21:16 AM
 */
public interface IPMatcher
{
    /**
     * 判断指定的地址是否匹配
     *
     * @param address IP地址，如果不是合法的IP地址(xxx.xxx.xxx.xxx)，返回<CODE>fasle</CODE>
     * @return 是否匹配
     */
    default boolean isMatch(String address)
    {
        return isMatch(IPUtil.toInts(address));
    }

    /**
     * 判断指定的地址是否匹配
     *
     * @param address 用一个整数表示的IP地址（适用于报文检查）
     */
    default boolean isMatch(int address)
    {
        return isMatch(IPUtil.toBytes(address));
    }

    /**
     * 判断指定的地址是否匹配
     *
     * @param address IP地址，长度必需为4或者16，否则返回<CODE>false</CODE>
     */
    default boolean isMatch(byte[] address)
    {
        return isMatch(IPUtil.toInts(address));
    }

    /**
     * 判断指定的地址是否匹配
     *
     * @param address IP地址，长度必需为4或者8，如果是8表示IPV6，否则返回<CODE>false</CODE>
     */
    boolean isMatch(int[] address);
}
