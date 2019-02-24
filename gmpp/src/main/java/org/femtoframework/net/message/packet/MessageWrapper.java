package org.femtoframework.net.message.packet;

import org.femtoframework.net.comm.PacketFuture;
import org.femtoframework.net.message.MessageFuture;

/**
 * 消息封装器
 *
 * @author fengyun
 * @version 1.00 2005-5-21 22:23:01
 */
public class MessageWrapper implements MessageFuture
{
    /**
     * 报文状态监控
     */
    private PacketFuture packetFuture;

    /**
     * 消息
     */
    private Object message;

    /**
     * 构造
     *
     * @param message 消息
     */
    public MessageWrapper(Object message)
    {
        this.message = message;
    }

    /**
     * 是否取消消息，如果消息还在队列中的化那么删除该消息，如果消息
     * 正在处理，那么根据mayInterruptIfRunning参数来决定是否中断处理
     *
     * @param mayInterruptIfRunning 如果已经在处理是否中断该消息
     * @return 是否取消成功
     */
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        boolean cancelled;
        if (packetFuture != null) {
            cancelled = packetFuture.cancel(mayInterruptIfRunning);
        }
        cancelled = doCancel();
        return cancelled;
    }

    /**
     * 取消
     *
     * @return
     */
    protected boolean doCancel()
    {
        return false;
    }

    /**
     * 是否已经取消的
     *
     * @return 是否已经取消
     */
    public boolean isCancelled()
    {
        return packetFuture.isCancelled();
    }

    /**
     * 消息是否已经完成发送
     *
     * @return 是否已经完成发送
     */
    public boolean isDone()
    {
        return packetFuture.isDone();
    }

    /**
     * 返回报文状态
     *
     * @return
     */
    public PacketFuture getPacketFuture()
    {
        return packetFuture;
    }

    /**
     * 设置报文状态
     *
     * @param packetFuture
     */
    public void setPacketFuture(PacketFuture packetFuture)
    {
        this.packetFuture = packetFuture;
    }

    /**
     * 返回消息
     *
     * @return
     */
    public Object getMessage()
    {
        return message;
    }
}
