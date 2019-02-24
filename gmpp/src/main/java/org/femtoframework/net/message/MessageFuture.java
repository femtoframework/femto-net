package org.femtoframework.net.message;

/**
 * 消息发送之后的状态跟踪接口
 *
 * @author fengyun
 * @version 1.00 2005-5-21 20:53:00
 */
public interface MessageFuture
{
    /**
     * 是否取消消息，如果消息还在队列中的化那么删除该消息，如果消息
     * 正在处理，那么根据mayInterruptIfRunning参数来决定是否中断处理
     *
     * @param mayInterruptIfRunning 如果已经在处理是否中断该消息
     * @return 是否取消成功
     */
    public boolean cancel(boolean mayInterruptIfRunning);

    /**
     * 是否已经取消的
     *
     * @return 是否已经取消
     */
    public boolean isCancelled();

    /**
     * 消息是否已经完成发送
     *
     * @return 是否已经完成发送
     */
    public boolean isDone();
}
