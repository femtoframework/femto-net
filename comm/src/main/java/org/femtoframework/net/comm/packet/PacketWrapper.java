package org.femtoframework.net.comm.packet;

import org.femtoframework.bean.Destroyable;
import org.femtoframework.net.comm.Packet;
import org.femtoframework.net.comm.PacketFuture;
import org.femtoframework.util.queue.Queue;

/**
 * 报文封装器
 *
 * @author fengyun
 * @version 1.00 2005-5-21 22:04:13
 */
public class PacketWrapper implements PacketFuture
{
    Packet packet;

    /**
     * 任务队列
     */
    private Queue<PacketWrapper> queue;

    /**
     * 是否已经取消
     */
    private boolean cancelled = false;

    /**
     * 构造
     *
     * @param queue  队列
     * @param packet 报文
     */
    public PacketWrapper(Queue<PacketWrapper> queue, Packet packet)
    {
        this.queue = queue;
        this.packet = packet;
    }

    /**
     * 是否取消报文，如果报文还在队列中的化那么删除该报文，如果报文
     * 正在处理，那么根据mayInterruptIfRunning参数来决定是否中断处理
     *
     * @param mayInterruptIfRunning 如果已经在处理是否中断该报文
     * @return 是否取消成功
     */
    public synchronized boolean cancel(boolean mayInterruptIfRunning)
    {
        if (isDone()) {
            return false;
        }
        cancelled = queue.remove(this);
        return cancelled;
    }

    /**
     * 是否已经取消的
     *
     * @return 是否已经取消
     */
    public boolean isCancelled()
    {
        return cancelled;
    }

    /**
     * 报文是否已经完成发送
     *
     * @return 是否已经完成发送
     */
    public boolean isDone()
    {
        return packet == null;
    }

    /**
     * 报文发送完备
     */
    public void done()
    {
        if (packet instanceof Destroyable) {
            ((Destroyable)packet).destroy();
        }
        packet = null;
    }
}
