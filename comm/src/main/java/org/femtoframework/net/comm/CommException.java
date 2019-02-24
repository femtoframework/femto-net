package org.femtoframework.net.comm;

import java.io.IOException;

/**
 * Comm相关的异常
 *
 * @author fengyun
 * @version 1.1 2005-3-30 17:06:50 扩展IOException
 *          1.00 Mar 13, 2002 7:05:42 PM
 */
public class CommException
    extends IOException
{
    /**
     * Constructs an <code>IOException</code> with the specified detail
     * message. The error message string <code>s</code> can later be
     * retrieved by the <code>{@link java.lang.Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
     *
     * @param s the detail message.
     */
    public CommException(String s)
    {
        super(s);
    }
}
