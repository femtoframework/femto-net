package org.femtoframework.net.nio;

import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

import org.femtoframework.bean.Startable;
import org.femtoframework.io.IOUtil;
import org.femtoframework.util.nutlet.NutletUtil;
import org.femtoframework.util.thread.ExecutorUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author fengyun
 * @version 1.00 2004-12-27 13:17:17
 */

public class SocketChannelEndpointTest
{
    @Test
    public void testSocketChannelEndpoint() throws Exception
    {
        new SocketChannelEndpoint();
    }

    @Test
    public void testGetBufferSize() throws Exception
    {
        SocketChannelEndpoint endpoint = new SocketChannelEndpoint();
        assertEquals(SocketChannelEndpoint.DEFAULT_BUFFER_SIZE, endpoint.getBufferSize());
    }

    @Test
    public void testSetBufferSize() throws Exception
    {
        SocketChannelEndpoint endpoint = new SocketChannelEndpoint();
        int bufferSize = NutletUtil.getInt();
        endpoint.setBufferSize(bufferSize);
        assertEquals(bufferSize, endpoint.getBufferSize());
    }

    @Test
    public void testGetHandler() throws Exception
    {
        SocketChannelEndpoint endpoint = new SocketChannelEndpoint();
        assertNull(endpoint.getHandler());
    }

    @Test
    public void testSetHandler() throws Exception
    {
        SocketChannelEndpoint endpoint = new SocketChannelEndpoint();
        endpoint.setPort(48888);
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
                IOUtil.close(socket);
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
        IOUtil.close(socket);

        endpoint.stop();
        endpoint.destroy();
    }
}