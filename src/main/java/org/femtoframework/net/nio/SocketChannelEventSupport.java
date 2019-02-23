package org.femtoframework.net.nio;

/**
 * 支持事件处理机制的线程
 *
 * @author fengyun
 * @version 1.00 2007-6-21 9:32:51
 */
public interface SocketChannelEventSupport
{
    /**
     * 添加事件
     *
     * @param event 事件
     */
    void fireEvent(SocketChannelEvent event);

    /**
     * 返回当前处理的SocketChannelContext
     *
     * @return 当前处理的SocketChannelContext
     */
    SocketChannelContext getCurrentContext();
}
