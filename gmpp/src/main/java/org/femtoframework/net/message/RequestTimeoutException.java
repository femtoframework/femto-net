package org.femtoframework.net.message;

/**
 * 请求超时异常
 *
 * @author fengyun
 * @version 1.00 2005-5-21 20:58:22
 */
public class RequestTimeoutException extends RuntimeException
{
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public RequestTimeoutException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new runtime exception with <code>null</code> as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public RequestTimeoutException()
    {
    }
}
