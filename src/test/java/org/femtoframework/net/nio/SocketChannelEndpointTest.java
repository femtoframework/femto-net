package org.femtoframework.net.nio;

import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

import org.bolango.net.SocketUtil;
import org.bolango.nio.NioUtil;
import org.bolango.tools.nutlet.Nutlet;
import org.bolango.tools.nutlet.NutletUtil;
import org.bolango.jade.thread.ExecutorUtil;
import org.bolango.jade.Startable;

/**
 * @author fengyun
 * @version 1.00 2004-12-27 13:17:17
 */

public class SocketChannelEndpointTest extends Nutlet
{
    public void testSocketChannelEndpoint() throws Exception
    {
        new SocketChannelEndpoint();
    }

    public void testGetBufferSize() throws Exception
    {
        SocketChannelEndpoint endpoint = new SocketChannelEndpoint();
        assertEquals(SocketChannelEndpoint.DEFAULT_BUFFER_SIZE, endpoint.getBufferSize());
    }

    public void testSetBufferSize() throws Exception
    {
        SocketChannelEndpoint endpoint = new SocketChannelEndpoint();
        int bufferSize = NutletUtil.getInt();
        endpoint.setBufferSize(bufferSize);
        assertEquals(bufferSize, endpoint.getBufferSize());
    }

    public void testGetHandler() throws Exception
    {
        SocketChannelEndpoint endpoint = new SocketChannelEndpoint();
        assertNull(endpoint.getHandler());
    }

    public void testSetHandler() throws Exception
    {
        SocketChannelEndpoint endpoint = new SocketChannelEndpoint();
        endpoint.setPort(8888);
        endpoint.setHandler(new SocketChannelHandler()
        {

            /**
             * 处理接入的Socket
             *
             * @param socket Socket
             */
            public void handle(SocketChannel socket)
            {
                System.out.println(socket);
                NioUtil.close(socket);
            }
        });
        assertNotNull(endpoint.getHandler());

        ExecutorService executor = ExecutorUtil.newSingleThreadExecutor();
        if (executor instanceof Startable) {
            ((Startable)executor).start();
        }
        endpoint.setExecutor(executor);
        endpoint.start();

        Socket socket = new Socket("127.0.0.1", 8888);
        socket.getInputStream();
        SocketUtil.close(socket);

        endpoint.stop();
        endpoint.destroy();
    }
}