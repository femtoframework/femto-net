package org.femtoframework.net.nio;

/**
 * 异常事件
 *
 * @author fengyun
 */
public class ExceptionEvent extends SocketChannelEvent
{
    private Exception exception;

    public ExceptionEvent(SocketChannelContext context)
    {
        super(context);
    }

    public ExceptionEvent(SocketChannelContext context, Exception exception)
    {
        super(context);
        setException(exception);
    }

    public Exception getException()
    {
        return exception;
    }

    public void setException(Exception exception)
    {
        this.exception = exception;
    }
}
