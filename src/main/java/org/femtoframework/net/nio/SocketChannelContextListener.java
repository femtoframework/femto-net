package org.femtoframework.net.nio;

import org.femtoframework.net.socket.SocketContext;
import org.femtoframework.net.socket.SocketContextListener;

/**
 * 抽象的SocketChannelContext侦听者
 *
 * @author fengyun
 * @version 1.00 2004-8-7 0:04:04
 */
public abstract class SocketChannelContextListener
    implements SocketContextListener
{
    /**
     * 处理事件
     *
     * @param action  事件动作
     * @param context Socket上下文
     */
    public void handleEvent(int action, SocketContext context)
    {
        if (context instanceof SocketChannelContext) {
            handleEvent(action, (SocketChannelContext)context);
        }
        else {
            throw new IllegalArgumentException("Can't handle socket context:" + context);
        }
    }

    /**
     * 处理事件
     *
     * @param action  事件动作
     * @param context Socket上下文
     */
    protected abstract void handleEvent(int action, SocketChannelContext context);

    /**
     * 注册Context
     *
     * @param context
     */
    protected void register(SocketChannelContext context)
    {
        SocketChannelRegistry registry = context.getRegistry();
        registry.register(context);
    }

    /**
     * 注销Context
     *
     * @param context
     */
    protected void unregister(SocketChannelContext context)
    {
        SocketChannelRegistry registry = context.getRegistry();
        registry.unregister(context);
    }

}
