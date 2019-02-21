package org.femtoframework.net.socket;

import java.net.Socket;

/**
 * Socket Handler
 *
 * @author fengyun
 * @version 1.00 Oct 23, 2003 7:48:44 PM
 */
public interface SocketHandler
{
    /**
     * Handle Socket
     *
     * @param socket Socket
     */
    void handle(Socket socket);
}
