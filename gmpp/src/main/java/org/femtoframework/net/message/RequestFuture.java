package org.femtoframework.net.message;

/**
 * 请求消息发送之后的状态跟踪接口
 *
 * @author fengyun
 * @version 1.00 2005-5-21 20:56:50
 */
public interface RequestFuture extends MessageFuture
{
    /**
     * 等待响应返回，直到有响应返回或者当前的线程被中断
     *
     * @return 响应
     * @throws RequestCancellationException if the computation was cancelled
     * @throws InterruptedException         if the current thread was interrupted
     *                                      while waiting
     */
    public Object getResponse() throws InterruptedException;

    /**
     * 等待指定的时间，直到响应返回
     *
     * @param timeout the maximum time to wait（单位毫秒）
     * @return the computed result
     * @throws RequestCancellationException if the computation was cancelled
     * @throws InterruptedException         if the current thread was interrupted
     *                                      while waiting
     */
    public Object getResponse(long timeout) throws InterruptedException, RequestTimeoutException;

    /**
     * 添加响应侦听者
     *
     * @param listener 响应侦听者
     */
    public void setResponseListener(ResponseListener listener);

    /**
     * 返回响应侦听者
     *
     * @return 响应侦听者
     */
    public ResponseListener getResponseListener();
}
