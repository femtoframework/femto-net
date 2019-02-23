package org.femtoframework.net.nio.cmd;

import org.bolango.tools.nutlet.Nutlet;
import org.bolango.tools.nutlet.NutletUtil;
import org.bolango.jade.AbstractLifecycle;

/**
 * 命令上下文侦听者测试
 *
 * @author fengyun
 * @version 1.00 2005-1-2 22:28:15
 */

public class CommandContextListenersTest extends Nutlet
{
    private SimpleContextListener listener = new SimpleContextListener();

    /**
     * 构造
     *
     * @throws Exception
     */
    public void testCommandContextListeners0() throws Exception
    {
        CommandContextListeners listeners = new CommandContextListeners();
        assertEquals(0, listeners.getListenerCount());
    }

    /**
     * 构造
     *
     * @throws Exception
     */
    public void testCommandContextListeners1() throws Exception
    {
        CommandContextListeners listeners = new CommandContextListeners(listener);
        assertEquals(1, listeners.getListenerCount());
        CommandContextListener[] array = listeners.getListeners();
        assertEquals(1, array.length);
        assertEquals(new CommandContextListener[]{listener}, array);
    }

    /**
     * 测试添加侦听者（无法添加null)
     *
     * @throws Exception
     */
    public void testAddListener0() throws Exception
    {
        CommandContextListeners listeners = new CommandContextListeners();
        assertEquals(0, listeners.getListenerCount());
        listeners.addListener(null);
        assertEquals(0, listeners.getListenerCount());
    }

    /**
     * 测试添加侦听者
     *
     * @throws Exception
     */
    public void testAddListener1() throws Exception
    {
        CommandContextListeners listeners = new CommandContextListeners();
        assertEquals(0, listeners.getListenerCount());
        listeners.addListener(listener);
        assertEquals(1, listeners.getListenerCount());
    }

    public static class SimpleLifeCycle
        extends AbstractLifecycle
        implements CommandContextListener
    {

        protected void doInit()
        {
        }

        protected void doStart()
        {
        }

        protected void doStop()
        {
        }

        protected void doDestroy()
        {
        }

        /**
         * 处理BaseContext来的事件
         *
         * @param context 上下文
         * @param action  动作种类
         * @return 是否结束处理
         */
        public boolean handle(CommandContext context, int action)
        {
            return false;
        }
    }

//    /**
//     * 测试添加侦听者（LifeCycle，容器已经初始化)
//     *
//     * @throws Exception
//     */
//    public void testAddListener2() throws Exception
//    {
//        CommandContextListeners listeners = new CommandContextListeners();
//        assertEquals(0, listeners.getListenerCount());
////        listeners.init();
//        SimpleLifeCycle listener = new SimpleLifeCycle();
//        assertFalse(listener.isStarted());
//        listeners.addListener(listener);
//        assertEquals(1, listeners.getListenerCount());
//        assertTrue(listener.isInited());
//    }
//
//    /**
//     * 测试添加侦听者（LifeCycle，容器已经启动了)
//     *
//     * @throws Exception
//     */
//    public void testAddListener3() throws Exception
//    {
//        CommandContextListeners listeners = new CommandContextListeners();
//        assertEquals(0, listeners.getListenerCount());
////        listeners.start();
//        SimpleLifeCycle listener = new SimpleLifeCycle();
//        assertFalse(listener.isStarted());
//        listeners.addListener(listener);
//        assertEquals(1, listeners.getListenerCount());
//        assertTrue(listener.isStarted());
//    }

    /**
     * 测试删除侦听者
     *
     * @throws Exception
     */
    public void testRemoveListener() throws Exception
    {
        CommandContextListeners listeners = new CommandContextListeners();
        //试图删除不存在的侦听者
        listeners.removeListener(listener);
        //删除null
        listeners.removeListener(null);

        //增加一个
        listeners.addListener(listener);
        assertEquals(1, listeners.getListenerCount());
        listeners.removeListener(listener);
        assertEquals(0, listeners.getListenerCount());

        //增加多个
        listeners.addListener(listener);
        CommandContextListener listener2 = new SimpleLifeCycle();
        listeners.addListener(listener2);
        assertEquals(2, listeners.getListenerCount());
        listeners.removeListener(listener2);
        assertEquals(1, listeners.getListenerCount());
        listeners.removeListener(listener);
        assertEquals(0, listeners.getListenerCount());
    }

    /**
     * 测试返回侦听者数组
     *
     * @throws Exception
     */
    public void testGetListeners() throws Exception
    {
        CommandContextListeners listeners = new CommandContextListeners();

        assertEquals(0, listeners.getListeners().length);

        //增加一个
        listeners.addListener(listener);
        assertEquals(1, listeners.getListeners().length);
        listeners.removeListener(listener);
        assertEquals(0, listeners.getListeners().length);

        //增加多个
        listeners.addListener(listener);
        CommandContextListener listener2 = new SimpleLifeCycle();
        listeners.addListener(listener2);
        assertEquals(2, listeners.getListeners().length);
        assertEquals(new CommandContextListener[]{listener, listener2},
            listeners.getListeners());
        listeners.removeListener(listener2);
        assertEquals(new CommandContextListener[]{listener},
            listeners.getListeners());
        assertEquals(1, listeners.getListeners().length);
        listeners.removeListener(listener);
        assertEquals(0, listeners.getListeners().length);
    }

    /**
     * 测试返回侦听者总数，所有之前不少测试方法中有调用相关的总数所以这里不做重点测试
     *
     * @throws Exception
     */
    public void testGetListenerCount() throws Exception
    {
        CommandContextListeners listeners = new CommandContextListeners();
        assertEquals(0, listeners.getListenerCount());
    }

    public void testHandle() throws Exception
    {
        final int[] count = new int[1];
        SimpleLifeCycle lifeCycle1 = new SimpleLifeCycle()
        {
            /**
             * 处理BaseContext来的事件
             *
             * @param context 上下文
             * @param action  动作种类
             * @return 是否结束处理
             */
            public boolean handle(CommandContext context, int action)
            {
                count[0] += action;
                return false;
            }

        };

        SimpleLifeCycle lifeCycle2 = new SimpleLifeCycle()
        {
            /**
             * 处理BaseContext来的事件
             *
             * @param context 上下文
             * @param action  动作种类
             * @return 是否结束处理
             */
            public boolean handle(CommandContext context, int action)
            {
                count[0] += 2 * action;
                return false;
            }
        };

        CommandContextListeners listeners = new CommandContextListeners();
        listeners.addListener(lifeCycle1);
        listeners.addListener(lifeCycle2);

//        listeners.setConfig(new SimpleConfig());
//        listeners.start();
        assertEquals(0, count[0]);
        int action = NutletUtil.getInt(0xFFFF);
        listeners.handle(null, action);
        assertEquals(3 * action, count[0]);
    }

//    /**
//     * 测试初始化
//     *
//     * @throws Exception
//     */
//    public void testDoInit() throws Exception
//    {
//        final int[] count = new int[1];
//        SimpleLifeCycle lifeCycle1 = new SimpleLifeCycle()
//        {
//            protected void doInit()
//            {
//                count[0] += 1;
//            }
//        };
//
//        SimpleLifeCycle lifeCycle2 = new SimpleLifeCycle()
//        {
//            protected void doInit()
//            {
//                count[0] += 2;
//            }
//        };
//
//        CommandContextListeners listeners = new CommandContextListeners();
//        listeners.addListener(lifeCycle1);
//        listeners.addListener(lifeCycle2);
//
////        listeners.setConfig(new SimpleConfig());
//        assertEquals(0, count[0]);
////        listeners.doInit();
//        assertEquals(3, count[0]);
//    }
//
//    public void testDoStart() throws Exception
//    {
//        final int[] count = new int[1];
//        SimpleLifeCycle lifeCycle1 = new SimpleLifeCycle()
//        {
//            protected void doStart()
//            {
//                count[0] += 1;
//            }
//        };
//
//        SimpleLifeCycle lifeCycle2 = new SimpleLifeCycle()
//        {
//            protected void doStart()
//            {
//                count[0] += 2;
//            }
//        };
//
//        CommandContextListeners listeners = new CommandContextListeners();
//        listeners.addListener(lifeCycle1);
//
////        listeners.setConfig(new SimpleConfig());
////        listeners.init();
//        assertEquals(0, count[0]);
////        listeners.start();
//        assertEquals(1, count[0]);
//        listeners.addListener(lifeCycle2);
//        assertEquals(3, count[0]);
//    }
//
//    public void testDoStop() throws Exception
//    {
//        final int[] count = new int[1];
//        SimpleLifeCycle lifeCycle1 = new SimpleLifeCycle()
//        {
//            protected void doStop()
//            {
//                count[0] += 1;
//            }
//        };
//
//        SimpleLifeCycle lifeCycle2 = new SimpleLifeCycle()
//        {
//            protected void doStop()
//            {
//                count[0] += 2;
//            }
//        };
//
//        CommandContextListeners listeners = new CommandContextListeners();
//        listeners.addListener(lifeCycle1);
//        listeners.addListener(lifeCycle2);
//
////        listeners.setConfig(new SimpleConfig());
////        listeners.start();
//        assertEquals(0, count[0]);
////        listeners.doStop();
//        assertEquals(3, count[0]);
//    }
//
//    public void testDoDestroy() throws Exception
//    {
//        final int[] count = new int[1];
//        SimpleLifeCycle lifeCycle1 = new SimpleLifeCycle()
//        {
//            protected void doDestroy()
//            {
//                count[0] += 1;
//            }
//        };
//
//        SimpleLifeCycle lifeCycle2 = new SimpleLifeCycle()
//        {
//            protected void doDestroy()
//            {
//                count[0] += 2;
//            }
//        };
//
//        CommandContextListeners listeners = new CommandContextListeners();
//        listeners.addListener(lifeCycle1);
//        listeners.addListener(lifeCycle2);
//
////        listeners.setConfig(new SimpleConfig());
////        listeners.start();
////        listeners.stop();
//        assertEquals(0, count[0]);
////        listeners.doDestroy();
//        assertEquals(3, count[0]);
//    }
}