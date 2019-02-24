package org.femtoframework.net.comm;



/**
 * 连接事件侦听者
 *
 * @author fengyun
 * @version 1.00 Mar 14, 2002 2:35:03 PM
 * @see ConnectionEvent
 */
public interface ConnectionListener
{
    /**
     * 已经连接
     *
     * @param e 连接事件
     */
    public void connected(ConnectionEvent e);

    /**
     * 连接超时
     *
     * @param e 连接事件
     */
    public void timeout(ConnectionEvent e);

    /**
     * 连接断了
     *
     * @param e 连接事件
     */
    public void closed(ConnectionEvent e);

}
