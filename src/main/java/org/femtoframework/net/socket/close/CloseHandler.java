package org.femtoframework.net.socket.close;

import java.net.Socket;

import org.femtoframework.net.socket.SocketHandler;

/**
 * Socket close handler
 *
 * @author fengyun
 * @version 1.00 Oct 23, 2003 8:04:32 PM
 */
public interface CloseHandler extends SocketHandler
{
    void handle(Socket socket, int keepTime, byte[] error);
}
