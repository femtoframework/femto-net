package org.femtoframework.net.nio;

import java.nio.channels.SocketChannel;

import org.femtoframework.net.socket.SimpleSocketPipeline;
import org.femtoframework.net.socket.SocketContext;
import org.femtoframework.net.socket.SocketEndpoint;

/**
 * Socket Channel管道
 *
 * @author fengyun
 * @version 1.00 Nov 11, 2003 4:47:30 PM
 */
public class SocketChannelPipeline
    extends SimpleSocketPipeline
    implements SocketChannelHandler
{
    private SocketChannelEndpoint endpoint;

    /**
     * 处理接入的Socket
     *
     * @param channel Socket
     */
    public void handle(SocketChannel channel)
    {
        SocketContext context = getSocketContext(channel);
        context.setSecure(endpoint.isSecure());
        handle(context);
    }

    /**
     * 根据Socket返回上下文
     *
     * @param channel SocketChannel
     * @return Socket返回上下文
     */
    protected SocketContext getSocketContext(SocketChannel channel)
    {
        return new SimpleChannelContext(channel);
    }

    /**
     * 设置SocketEndpoint
     *
     * @param endpoint SocketEndpoint
     */
    public void setEndpoint(SocketEndpoint endpoint)
    {
        super.setEndpoint(endpoint);
        this.endpoint = (SocketChannelEndpoint)endpoint;
    }
}
