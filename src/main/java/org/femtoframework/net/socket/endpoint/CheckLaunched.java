package org.femtoframework.net.socket.endpoint;

import org.femtoframework.net.socket.SocketHandler;

/**
 * Check Launched Handler
 * 
 * @author fengyun
 * @version 1.00 11-8-6 上午12:40
 */
public interface CheckLaunched extends SocketHandler {

    /**
     * 设置下一级Socket处理器
     *
     * @param handler
     */
    public void setHandler(SocketHandler handler);
}
