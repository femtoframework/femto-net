package org.femtoframework.net.comm.packet;

import org.femtoframework.net.comm.CommUtil;
import org.femtoframework.net.comm.Packet;
import org.femtoframework.net.comm.PacketListener;
import org.femtoframework.net.comm.PacketProtocol;
import org.femtoframework.util.thread.LifecycleThread;

/**
 * 报文读取线程
 *
 * @author fengyun
 * @version 1.00 2005-5-5 16:01:39
 */
public class PacketReaderThread extends LifecycleThread
{
    /**
     * 连接
     */
    private PacketProtocol protocol;

    /**
     * 报文侦听者
     */
    private PacketListener listener;

    /**
     * 构造信息包Reader
     *
     * @param protocol 连接
     * @param listener  报文侦听者
     */
    public PacketReaderThread(PacketProtocol protocol,
                              PacketListener listener)
    {
        this.protocol = protocol;
        this.listener = listener;
    }

    /**
     * 创建线程
     *
     * @return 线程
     */
    protected Thread createThread()
    {
        return new Thread(this, "packet_reader");
    }

    /**
     * 实际要执行的任务方法。<br>
     * 通过这个方法，来执行实际的程序<br>
     * 如果出现异常，ErrorHandler的错误处理返回<code>true</code>，<br>
     * 那么该循环线程就终止循环。
     *
     * @throws Exception 各类执行异常
     * @see #run()
     */
    protected void doRun() throws Exception
    {
        try {
            Packet packet = protocol.readPacket();
            listener.onPacket(packet);
        }
        catch (Exception e) {
            if (handleException(e)) {
                CommUtil.close(protocol);
            }
            throw e;
        }
    }
}
