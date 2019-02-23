package org.femtoframework.net.nio.cmd;

import java.net.SocketException;

/**
 * Socket CLosed Exception
 *
 * @author fengyun
 * @version 1.00 2005-12-15 22:23:01
 */
public class SocketClosedException extends SocketException
{
    /**
     * Constructs a new <code>SocketException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public SocketClosedException(String msg)
    {
        super(msg);
    }

    /**
     * Constructs a new <code>SocketException</code> with no detail message.
     */
    public SocketClosedException()
    {
    }
}
