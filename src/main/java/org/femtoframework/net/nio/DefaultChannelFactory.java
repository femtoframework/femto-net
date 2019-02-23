package org.femtoframework.net.nio;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 默认ChannelFactory实现
 *
 * @author fengyun
 */
public class DefaultChannelFactory implements SocketChannelFactory
{
    /**
     * 创建服务器Socket通道
     *
     * @return
     */
    public ServerSocketChannel createServerSocketChannel()
        throws IOException
    {
        return ServerSocketChannel.open();
    }

    /**
     * 创建Socket通道
     *
     * @return
     */
    public SocketChannel createSocketChannel()
        throws IOException
    {
        return SocketChannel.open();
    }

    /**
     * 初始化通道
     *
     * @param channel 通道
     */
    public void initChannel(SocketChannel channel) throws IOException
    {
    }
}
