package org.femtoframework.net.nio;

/**
 * Socket Channel Event
 *
 * @author fengyun
 * @version 1.00 2007-6-21 9:28:29
 */
public class SocketChannelEvent
{
    private SocketChannelContext context;

    public SocketChannelEvent(SocketChannelContext context)
    {
        this.context = context;
    }

    /**
     * 返回上下文
     *
     * @return 上下文
     */
    public SocketChannelContext getContext()
    {
        return context;
    }

    /**
     * 设置上下文
     *
     * @param context 上下文
     */
    public void setContext(SocketChannelContext context)
    {
        this.context = context;
    }

    /**
     * 获取事件回调
     *
     * @return 事件回调
     */
    public SocketChannelCallback getCallback()
    {
        return context != null ? context.getCallback() : null;
    }

    /**
     * 设置事件回调
     *
     * @param callback 事件回调
     */
    public void setCallback(SocketChannelCallback callback)
    {
        if (context != null) {
            context.setCallback(callback);
        }
    }
}
