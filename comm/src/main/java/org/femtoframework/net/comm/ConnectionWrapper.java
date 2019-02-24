package org.femtoframework.net.comm;

/**
 * 连接封装器
 *
 * @author fengyun
 * @version 1.00 2005-6-2 22:17:25
 */
public interface ConnectionWrapper
{
    /**
     * 返回原始的连接
     *
     * @return
     */
    Connection getConnection();
}
