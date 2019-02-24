package org.femtoframework.net.socket.bifurcation;



import org.femtoframework.net.socket.SocketHandler;

import java.net.Socket;

/**
 * BifurcationHandlerWrapper
 *
 * @author fengyun
 * @version 1.00 2005-3-12 19:30:21
 */
public class BifurcationHandlerWrapper extends BifurcatedSocketHandler
{
    private SocketHandler handler;

    public BifurcationHandlerWrapper(SocketHandler handler)
    {
        this.handler = handler;
    }

    /**
     * 处理Socket
     *
     * @param socket Socket
     */
    public void handle(Socket socket)
    {
        handler.handle(socket);
    }
}
