package org.femtoframework.net.socket;

import org.femtoframework.util.nutlet.NutletUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author fengyun
 * @version 1.00 2005-1-6 18:28:19
 */

public class SocketValveEventTest
{
    private SocketValve valve = new AbstractSocketValve()
    {

        /**
         * 处理连接
         *
         * @param context 连接上下文
         * @param chain   阀门控制链
         */
        public void handle(SocketContext context, SocketChain chain)
        {
        }
    };

    @Test
    public void testSocketValveEvent() throws Exception
    {
        SocketContext context = new SimpleSocketContext();
        SocketValveEvent event = new SocketValveEvent(valve, context, 0, new Object[0]);
        assertEquals(context, event.getContext());
        assertEquals(0, event.getArguments().length);
    }

    @Test
    public void testGetContext() throws Exception
    {
        SocketValveEvent event = new SocketValveEvent(valve, null, 0, new Object[0]);
        assertNull(event.getContext());
    }

    @Test
    public void testGetArguments() throws Exception
    {
        SocketContext context = new SimpleSocketContext();
        SocketValveEvent event = new SocketValveEvent(valve, context, 0, null);
        assertNull(event.getArguments());
    }

    @Test
    public void testGetValve() throws Exception
    {
        SocketContext context = new SimpleSocketContext();
        SocketValveEvent event = new SocketValveEvent(valve, context, 0, null);
        assertEquals(valve, event.getValve());
    }

    @Test
    public void testGetAction() throws Exception
    {
        SocketContext context = new SimpleSocketContext();
        int action = NutletUtil.getInt();
        SocketValveEvent event = new SocketValveEvent(valve, context, action, null);
        assertEquals(action, event.getAction());
    }
}