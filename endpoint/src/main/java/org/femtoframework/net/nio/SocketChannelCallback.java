package org.femtoframework.net.nio;

/**
 * Socket Channel 事件回调
 *
 * @author fengyun
 */
public interface SocketChannelCallback
{
    /**
     * 回调处理
     *
     * @param event 事件
     */
    void callback(SocketChannelEvent event);
}
