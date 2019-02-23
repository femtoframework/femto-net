package org.femtoframework.net.nio;

import java.nio.channels.Selector;

/**
 * SocketChannelContext注册管理<br>
 * <p/>
 * 注册管理器的作用是让处理程序能够不断的改变敢兴趣的操作集合<br>
 *
 * @author fengyun
 * @version 1.00 2004-8-6 22:01:49
 */
public interface SocketChannelRegistry
{
    /**
     * 注册SocketChannelContext
     *
     * @param context 上下文
     */
    void register(SocketChannelContext context);

    /**
     * 取消注册SocketChannelContext
     *
     * @param context 上下文
     */
    void unregister(SocketChannelContext context);

    /**
     * 返回选取器
     *
     * @return 选取器
     */
    Selector getSelector();
}
