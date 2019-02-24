package org.femtoframework.net.nio;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Server Socket Channel Factory
 *
 * @author fengyun
 * @version 1.00 2005-7-3 3:33:41
 */
public interface SocketChannelFactory
{
    ServerSocketChannel createServerSocketChannel() throws IOException;

    SocketChannel createSocketChannel() throws IOException;

    void initChannel(SocketChannel channel) throws IOException;
}
