package org.femtoframework.net.nio;

import java.nio.channels.SocketChannel;

/**
 * Socket Channel 处理器
 *
 * @author fengyun
 * @version 1.00 Nov 11, 2003 4:33:51 PM
 */
public interface SocketChannelHandler
{
    /**
     * 处理接入的Socket
     *
     * @param socket Socket
     */
    void handle(SocketChannel socket);
}
