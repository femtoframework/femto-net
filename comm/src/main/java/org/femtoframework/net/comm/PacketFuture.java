package org.femtoframework.net.comm;

/**
 * 报文状态监控
 *
 * @author fengyun
 * @version 1.00 2005-5-21 22:02:10
 */
public interface PacketFuture
{
    /**
     * 是否取消报文，如果报文还在队列中的化那么删除该报文，如果报文
     * 正在处理，那么根据mayInterruptIfRunning参数来决定是否中断处理
     *
     * @param mayInterruptIfRunning 如果已经在处理是否中断该报文
     * @return 是否取消成功
     */
    boolean cancel(boolean mayInterruptIfRunning);

    /**
     * 是否已经取消的
     *
     * @return 是否已经取消
     */
    boolean isCancelled();

    /**
     * 报文是否已经完成发送
     *
     * @return 是否已经完成发送
     */
    boolean isDone();
}
