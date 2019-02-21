package org.femtoframework.net.socket.endpoint;

import org.femtoframework.net.socket.SocketHandler;

/**
 * SocketHandler工厂
 *
 * @author fengyun
 * @version 1.00 2005-3-14 20:26:44
 */
public interface SocketHandlerFactory
{
    /**
     * 根据Scheme返回相应的处理器
     *
     * @param scheme Scheme
     */
    public SocketHandler getHandler(int scheme);

    /**
     * 添加处理器
     *
     * @param handler Socket处理器
     */
    public void addHandler(SocketHandler handler);
}
