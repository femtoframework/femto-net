package org.femtoframework.net.nio.cmd;

import org.femtoframework.util.nutlet.NutletUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * 测试AbstractCommandHandler
 *
 * @author fengyun
 * @version 1.00 2005-1-2 22:22:25
 */

public class AbstractCommandHandlerTest
{
    AbstractCommandHandler handler = new SimpleCommandHandler();

    SimpleContextListener listener = new SimpleContextListener();

    /**
     * 测试添加Null的侦听者
     *
     * @throws Exception
     */
    @Test
    public void testAddContextListener0() throws Exception
    {
        handler.addContextListener(null);
    }

    /**
     * 测试添加Null的侦听者
     *
     * @throws Exception
     */
    @Test
    public void testAddContextListener1() throws Exception
    {
        handler.addContextListener(listener);
        assertEquals(1, handler.getContextListeners().size());
        handler.addContextListener(new SimpleContextListener());
        assertEquals(2, handler.getContextListeners().size());
    }

    /**
     * 测试返回ContextListeners
     *
     * @throws Exception
     */
    @Test
    public void testGetContextListeners0() throws Exception
    {
        assertEquals(0, handler.getContextListeners().size());
    }

    /**
     * 测试删除Null的侦听者
     *
     * @throws Exception
     */
    @Test
    public void testRemoveContextListener0() throws Exception
    {
        handler.removeContextListener(null);
    }

    /**
     * 测试删除侦听者
     *
     * @throws Exception
     */
    @Test
    public void testRemoveContextListener1() throws Exception
    {
        handler.addContextListener(listener);
        assertEquals(1, handler.getContextListeners().size());
        handler.removeContextListener(listener);
        assertEquals(0, handler.getContextListeners().size());
    }

    /**
     * 测试FireEvent（没有Listener的情况下)
     *
     * @throws Exception
     */
    @Test
    public void testFireEvent0() throws Exception
    {
        handler.fireEvent(null, 0);
    }

    /**
     * 测试FireEvent
     *
     * @throws Exception
     */
    @Test
    public void testFireEvent1() throws Exception
    {
        final int[] actions = new int[1];
        handler.addContextListener(new CommandContextListener()
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
                actions[0] = action;
                return false;
            }
        });
        int action = NutletUtil.getInt();
        handler.fireEvent(null, action);
        assertEquals(action, actions[0]);
    }
}