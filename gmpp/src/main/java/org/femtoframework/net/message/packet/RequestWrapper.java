package org.femtoframework.net.message.packet;

import org.femtoframework.net.message.*;

/**
 * 请求封装
 *
 * @author fengyun
 * @version 1.00 2005-5-21 22:57:25
 */
public class RequestWrapper
    extends MessageWrapper
    implements RequestFuture, Timeoutable
{
    /**
     * 请求消息
     */
    private RequestMessage request;

    /**
     * 响应消息
     */
    private Object response;

    /**
     * 响应侦听者
     */
    private ResponseListener listener;

    /**
     * 有效期
     */
    protected int timeout;

    /**
     * 起始等待时间
     */
    private long startTime;

    /**
     * 标识号
     */
    private int id = -1;

    /**
     * 构造
     *
     * @param request 消息
     */
    public RequestWrapper(int msgId, RequestMessage request)
    {
        super(request);
        this.id = msgId;
        this.request = request;
        this.timeout = request.getTimeout();
        this.startTime = System.currentTimeMillis();
    }

    /**
     * 等待响应返回，直到有响应返回或者当前的线程被中断
     *
     * @return 响应
     * @throws RequestCancellationException
     *                              if the computation was cancelled
     * @throws InterruptedException if the current thread was interrupted
     *                              while waiting
     */
    public Object getResponse() throws InterruptedException
    {
        while (response == null) {
            synchronized (this) {
                wait(1000);
            }
        }
        Object result = doGetResponse();
        if (result instanceof RequestTimeoutException) {
            throw (RequestTimeoutException) result;
        }
        else {
            return result;
        }
    }

    /**
     * 返回当前响应
     *
     * @return [Response] 当前响应
     */
    protected Object doGetResponse()
    {
        if (response != null && response instanceof WrappedMessage) {
            Object message = response;
            try {
                response = ((WrappedMessage) message).getMessage();
            }
            finally {
                if (message instanceof MessagePacket) {
                    ((MessagePacket)message).destroy();
                }
            }
        }
        return response;
    }

    /**
     * 等待指定的时间，直到响应返回
     *
     * @param timeout the maximum time to wait（单位毫秒）
     * @return the computed result
     * @throws RequestCancellationException
     *                              if the computation was cancelled
     * @throws InterruptedException if the current thread was interrupted
     *                              while waiting
     */
    public Object getResponse(long timeout) throws InterruptedException, RequestTimeoutException
    {
        while (response == null) {
            synchronized (this) {
                wait(1000);
            }

            if (isTimeout()) {
                break;
            }
        }
        if (response == null) {
            throw new RequestTimeoutException("Timeout");
        }
        Object result = doGetResponse();
        if (result instanceof RequestTimeoutException) {
            throw new RequestTimeoutException(((RequestTimeoutException)result).getMessage());
        }
        else {
            return result;
        }
    }

    /**
     * 添加响应侦听者
     *
     * @param listener 响应侦听者
     */
    public void setResponseListener(ResponseListener listener)
    {
        this.listener = listener;
    }

    /**
     * 返回响应侦听者
     *
     * @return 响应侦听者
     */
    public ResponseListener getResponseListener()
    {
        return listener;
    }

    /**
     * 消息是否已经完成发送
     *
     * @return 是否已经完成发送
     */
    public boolean isDone()
    {
        return response != null;
    }

    /**
     * 设置响应
     *
     * @param response 响应或者异常
     */
    public void setResponse(Object response)
    {
        this.response = response;
        if (listener != null) {
            listener.onResponse(response);
        }
        synchronized (this) {
            notifyAll();
        }
    }

    /**
     * 判断请求是否超时
     *
     * @return 是否超时
     */
    public boolean isTimeout()
    {
        return timeout > 0 && (System.currentTimeMillis() - startTime) > timeout;
    }

    /**
     * 处理请求超时
     */
    public void timeout()
    {
        setResponse(new RequestTimeoutException("Request timeout"));
    }

    /**
     * 返回请求消息
     *
     * @return 请求消息
     */
    public RequestMessage getRequest()
    {
        return request;
    }

    /**
     * 返回实体唯一标识
     *
     * @return 标识
     */
    public int getId()
    {
        return id;
    }

    /**
     * 返回请求超时时间，如果请求超时时间是0，表示没有超时
     *
     * @return 请求超时时间
     */
    public int getTimeout()
    {
        return request != null ? request.getTimeout() : 0;
    }
}
