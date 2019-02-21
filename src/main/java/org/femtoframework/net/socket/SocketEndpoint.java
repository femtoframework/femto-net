package org.femtoframework.net.socket;

import java.net.InetAddress;

/**
 * Socket Endpoint
 *
 * @author fengyun
 * @version 1.00 2005-2-28 0:22:16
 */
public interface SocketEndpoint
{
    /**
     * Returns is secure or not
     */
    boolean isSecure();

    /**
     * Returns current port
     *
     * @return the port
     */
    int getPort();

    /**
     * Current binding socket address
     *
     * @return Address
     */
    InetAddress getAddress();
}
