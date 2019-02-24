package org.femtoframework.net.comm.packet;

import org.femtoframework.net.comm.CommUtil;
import org.femtoframework.net.comm.Connection;
import org.femtoframework.util.queue.Queue;
import org.femtoframework.util.thread.LifecycleThread;

/**
 * 写报文线程
 *
 * @author fengyun
 * @version 1.00 2005-5-5 16:04:37
 */
public class PacketWriterThread
    extends LifecycleThread
{
    /**
     * 连接
     */
    private Connection conn;

    /**
     * 信息包发送队列
     */
    private Queue queue;

    /**
     * 是否长连接
     */
    private boolean longTerm = true;

    /**
     * 上一次对象发送时间
     */
    private long lastSent = System.currentTimeMillis();

    /**
     * 当没有对象被写出时，Ping命令发送的周期
     */
    private int pingPeriod = 20000;

    /**
     * 信息包协议Writer
     *
     * @param queue 信息包写队列
     * @param conn  连接
     */
    public PacketWriterThread(Queue queue, Connection conn)
    {
        this.queue = queue;
        this.conn = conn;
    }

    /**
     * 创建线程
     *
     * @return 线程
     */
    protected Thread createThread()
    {
        return new Thread(this, "packet_writer");
    }


    /**
     * 实际要执行的任务
     *
     * @throws java.lang.Exception 各类执行异常
     * @see #run()
     */
    protected void doRun() throws Exception
    {
        PacketWrapper wrapper = (PacketWrapper) queue.poll(pingPeriod);
        if (!isRunning()) {
            return;
        }
        try {
            if (wrapper != null) {
                synchronized (wrapper) {
                    try {
                        conn.writePacket(wrapper.packet);
                    }
                    finally {
                        wrapper.done();
                    }
                }
                lastSent = System.currentTimeMillis();
                return;
            }
            if (System.currentTimeMillis() - lastSent > pingPeriod) {
                if (longTerm) {
                    conn.ping();
                }
                else {
                    //非长连接 PingPeriod以后超时
                    conn.timeout();
                }
            }
        }
        catch (Exception e) {
            if (handleException(e)) {
                CommUtil.close(conn);
            }
            throw e;
        }
    }

    /**
     * 设置Ping周期
     *
     * @return
     */
    public int getPingPeriod()
    {
        return pingPeriod;
    }

    /**
     * 设置Ping周期
     *
     * @param pingPeriod Ping周期
     */
    public void setPingPeriod(int pingPeriod)
    {
        this.pingPeriod = pingPeriod;
    }

    /**
     * 返回是否是长连接
     *
     * @return
     */
    public boolean isLongTerm()
    {
        return longTerm;
    }

    /**
     * 设置是否是长连接
     *
     * @param longTerm 是否是长连接
     */
    public void setLongTerm(boolean longTerm)
    {
        this.longTerm = longTerm;
    }
}
