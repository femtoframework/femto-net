package org.femtoframework.net.nio;

import java.nio.channels.SocketChannel;

/**
 * SocketChannelWrapper，用来标示扩展的SocketChannel，当
 *
 * @author fengyun
 * @version 1.00 2005-12-31 17:04:15
 */
public interface SocketChannelWrapper
{
    /**
     * 返回被封装的SocketChannel
     *
     * @return 被封装的SocketChannel
     */
    SocketChannel getChannel();

    /**
     * 判断是否还有数据可以读取
     *
     * @return 是否还有数据可以读取
     */
    boolean hasReadable();
}
