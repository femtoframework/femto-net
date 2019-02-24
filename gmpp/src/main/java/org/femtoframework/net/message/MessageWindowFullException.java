package org.femtoframework.net.message;

/**
 * 消息窗口满异常
 *
 * @author fengyun
 * @version 1.00 2005-5-21 22:41:25
 */
public class MessageWindowFullException extends RuntimeException
{
    /**
     * Constructs a new runtime exception with <code>null</code> as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public MessageWindowFullException()
    {
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public MessageWindowFullException(String message)
    {
        super(message);
    }
}
