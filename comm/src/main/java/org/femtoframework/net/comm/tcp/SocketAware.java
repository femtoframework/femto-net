package org.femtoframework.net.comm.tcp;

import java.io.IOException;
import java.net.Socket;

/**
 * 需要Socket注入
 *
 * @author fengyun
 * @version 1.00 2005-5-7 17:37:11
 */
public interface SocketAware
{
    /**
     * 设置Socket
     *
     * @param socket Socket
     */
    void setSocket(Socket socket)
        throws IOException;
}
