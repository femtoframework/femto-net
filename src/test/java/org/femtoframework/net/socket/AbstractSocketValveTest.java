package org.femtoframework.net.socket;

import org.femtoframework.util.nutlet.NutletUtil;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * 测试AbstractSocketValve
 *
 * @author fengyun
 * @version 1.00 2005-1-6 18:37:23
 */

public class AbstractSocketValveTest
{
    private static class SimpleSocketValve extends AbstractSocketValve
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
    }

    /**
     * 添加Valve侦听者
     *
     * @throws Exception
     */
    public void testAddValveListener() throws Exception
    {
        SimpleSocketValve valve = new SimpleSocketValve();

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
        valve.addValveListener(listener);

        int action = NutletUtil.getInt();
        valve.fireValveEvent(action, null, null);
        assertEquals(action, events[0].getAction());
    }

    /**
     * 测试删除ValveListener
     *
     * @throws Exception
     */
    public void testRemoveValveListener() throws Exception
    {
        SimpleSocketValve valve = new SimpleSocketValve();

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
        valve.addValveListener(listener);

        int action = NutletUtil.getInt();
        valve.fireValveEvent(action, null, null);
        assertEquals(action, events[0].getAction());

        events[0] = null;
        valve.removeValveListener(listener);
        valve.fireValveEvent(action, null, null);
        assertNull(events[0]);
    }

    /**
     * 测试返回Listener
     *
     * @throws Exception
     */
    @Test
    public void testGetValveListener0() throws Exception
    {
        SimpleSocketValve valve = new SimpleSocketValve();

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
        valve.addValveListener(listener);
        assertEquals(listener, valve.getValveListener());
    }

    /**
     * 测试返回Listener
     *
     * @throws Exception
     */
    @Test
    public void testGetValveListener1() throws Exception
    {
        SimpleSocketValve valve = new SimpleSocketValve();
        SocketValveListener listener1 = new SocketValveListener()
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
        valve.addValveListener(listener1);
        SocketValveListener listener2 = new SocketValveListener()
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
        valve.addValveListener(listener2);
        assertTrue(valve.getValveListener() instanceof SocketValveListeners);
        SocketValveListeners listeners = (SocketValveListeners) valve.getValveListener();
        List<SocketValveListener> array = listeners.getListeners();
        assertEquals(2, array.size());
        assertEquals(listener1, array.get(0));
        assertEquals(listener2, array.get(1));
    }

    /**
     * @throws Exception
     */
    @Test
    public void testFireValveEvent0() throws Exception
    {
        SimpleSocketValve valve = new SimpleSocketValve();
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
        valve.addValveListener(listener);
        int action = NutletUtil.getInt();
        SimpleSocketContext context = new SimpleSocketContext();
        valve.fireValveEvent(action, context);

        assertEquals(action, events[0].getAction());
        assertNull(events[0].getArguments());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testFireValveEvent1() throws Exception
    {
        SimpleSocketValve valve = new SimpleSocketValve();
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
        valve.addValveListener(listener);
        int action = NutletUtil.getInt();
        SimpleSocketContext context = new SimpleSocketContext();
        valve.fireValveEvent(action, context, new String[]{"test1", "test2"});

        assertEquals(action, events[0].getAction());
        assertNotNull(events[0].getArguments());
        assertEquals(events[0].getArguments(), new String[]{"test1", "test2"});
    }

    /**
     * 测试添加ContextListener
     *
     * @throws Exception
     */
    public void testAddContextListener() throws Exception
    {
        SimpleSocketValve valve = new SimpleSocketValve();

        final int[] actions = new int[1];
        SocketContextListener listener = new SocketContextListener()
        {
            /**
             * 处理事件
             *
             * @param action  事件动作
             * @param context Socket上下文
             */
            public void handleEvent(int action, SocketContext context)
            {
                actions[0] = action;
            }
        };
        valve.addContextListener(listener);

        int action = NutletUtil.getInt();
        valve.fireContextEvent(action, null);
        assertEquals(action, actions[0]);
    }

    /**
     * 删除Context侦听者
     *
     * @throws Exception
     */
    public void testRemoveContextListener() throws Exception
    {
        SimpleSocketValve valve = new SimpleSocketValve();

        final int[] actions = new int[1];
        SocketContextListener listener = new SocketContextListener()
        {
            /**
             * 处理事件
             *
             * @param action  事件动作
             * @param context Socket上下文
             */
            public void handleEvent(int action, SocketContext context)
            {
                actions[0] = action;
            }
        };
        valve.addContextListener(listener);

        int action = NutletUtil.getInt();
        valve.fireContextEvent(action, null);
        assertEquals(action, actions[0]);

        actions[0] = 0;

        valve.removeContextListener(listener);
        valve.fireContextEvent(action, null);
        assertEquals(actions[0], 0);
    }

    public void testGetContextListener0() throws Exception
    {
        SimpleSocketValve valve = new SimpleSocketValve();

        SocketContextListener listener = new SocketContextListener()
        {
            /**
             * 处理事件
             *
             * @param action  事件动作
             * @param context Socket上下文
             */
            public void handleEvent(int action, SocketContext context)
            {
            }
        };
        valve.addContextListener(listener);
        assertEquals(listener, valve.getContextListener());
    }

    public void testGetContextListener1() throws Exception
    {
        SimpleSocketValve valve = new SimpleSocketValve();
        SocketContextListener listener1 = new SocketContextListener()
        {
            /**
             * 处理事件
             *
             * @param action  事件动作
             * @param context Socket上下文
             */
            public void handleEvent(int action, SocketContext context)
            {
            }
        };
        valve.addContextListener(listener1);
        SocketContextListener listener2 = new SocketContextListener()
        {
            /**
             * 处理事件
             *
             * @param action  事件动作
             * @param context Socket上下文
             */
            public void handleEvent(int action, SocketContext context)
            {
            }
        };
        valve.addContextListener(listener2);
        assertTrue(valve.getContextListener() instanceof SocketContextListeners);
        SocketContextListeners listeners = (SocketContextListeners) valve.getContextListener();
        List<SocketContextListener> array = listeners.getListeners();
        assertEquals(2, ((List) array).size());
        assertEquals(listener1, array.get(0));
        assertEquals(listener2, array.get(1));
    }

    /**
     * @throws Exception
     */
    public void testFireContextEvent() throws Exception
    {
        SimpleSocketValve valve = new SimpleSocketValve();
        final int[] actions = new int[1];
        SocketContextListener listener = new SocketContextListener()
        {
            /**
             * 处理事件
             *
             * @param action  事件动作
             * @param context Socket上下文
             */
            public void handleEvent(int action, SocketContext context)
            {
                actions[0] = action;
            }
        };
        valve.addContextListener(listener);
        int action = NutletUtil.getInt();
        SimpleSocketContext context = new SimpleSocketContext();
        valve.fireContextEvent(action, context);

        assertEquals(action, actions[0]);
    }
}