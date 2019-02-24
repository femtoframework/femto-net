package org.femtoframework.net.message;

/**
 * 当请求被取消之后，如果有线程仍然试图在等待结果的返回，<br>
 * 那么这些线程会获得一个RequestCancellationException异常。
 *
 * @author fengyun
 * @version 1.00 2005-5-21 21:03:36
 */
public class RequestCancellationException extends IllegalStateException
{
    /**
     * Constructs a <tt>CancellationException</tt> with no detail message.
     */
    public RequestCancellationException()
    {
    }

    /**
     * Constructs a <tt>CancellationException</tt> with the specified detail
     * message.
     *
     * @param message the detail message
     */
    public RequestCancellationException(String message)
    {
        super(message);
    }
}
