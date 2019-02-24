package org.femtoframework.net.socket;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 测试
 *
 * @author fengyun
 * @version 1.00 2005-1-6 17:33:37
 */

public class SocketValveListenersTest
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
    public void testSocketValveListeners() throws Exception
    {
        new SocketValveListeners();
        new SocketValveListeners(new SocketValveListener()
        {
            /**
             * 处理事件
             *
             * @param event SocketContextEvent
             */
            public void handleEvent(SocketValveEvent event)
            {
            }
        });
    }

    /**
     * 测试添加侦听者
     *
     * @throws Exception
     */
    @Test
    public void testAddListener() throws Exception
    {
        SocketValveListeners listeners = new SocketValveListeners();
        SocketValveListener listener = new SocketValveListener()
        {

            /**
             * 处理事件
             *
             * @param event SocketContextEvent
             */
            public void handleEvent(SocketValveEvent event)
            {
            }
        };
        listeners.addListener(listener);

        assertEquals(1, listeners.getListenerCount());
    }

    /**
     * 删除侦听者
     *
     * @throws Exception
     */
    @Test
    public void testRemoveListener() throws Exception
    {
        SocketValveListeners listeners = new SocketValveListeners();
        SocketValveListener listener = new SocketValveListener()
        {

            /**
             * 处理事件
             *
             * @param event SocketContextEvent
             */
            public void handleEvent(SocketValveEvent event)
            {
            }
        };
        listeners.addListener(listener);
        assertEquals(1, listeners.getListenerCount());
        listeners.removeListener(listener);
        assertEquals(0, listeners.getListenerCount());
    }

    /**
     * 处理事件
     *
     * @throws Exception
     */
    @Test
    public void testHandleEvent() throws Exception
    {
        SocketValveListeners listeners = new SocketValveListeners();
        final SocketValveEvent[] events = new SocketValveEvent[1];
        SocketValveListener listener = new SocketValveListener()
        {

            /**
             * 处理事件
             *
             * @param event SocketContextEvent
             */
            public void handleEvent(SocketValveEvent event)
            {
                events[0] = event;
            }
        };
        listeners.addListener(listener);
        assertEquals(1, listeners.getListenerCount());

        SocketValveEvent event = new SocketValveEvent(valve, null, 0, null);
        listeners.handleEvent(event);

        assertEquals(events[0], event);
    }

    /**
     * 返回事件侦听者数组
     *
     * @throws Exception
     */
    @Test
    public void testGetListeners() throws Exception
    {
        SocketValveListeners listeners = new SocketValveListeners();
        assertEquals(0, listeners.getListenerCount());
    }
}