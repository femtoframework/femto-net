package org.femtoframework.net.comm.packet;

import java.io.IOException;

import org.femtoframework.net.comm.Connection;
import org.femtoframework.net.comm.Packet;
import org.femtoframework.net.comm.PacketListener;
import org.femtoframework.util.queue.Queue;
import org.femtoframework.util.thread.ErrorHandler;

/**
 * 连接封装器（拥有自动写报文和读取报文的线程）
 *
 * @author fengyun
 * @version 1.00 2005-5-5 16:16:45
 */
public class ThreadConnection
    implements Connection
{
    /**
     * 连接
     */
    private Connection conn;

    /**
     * 写消息包线程
     */
    private PacketWriterThread writer;

    /**
     * 读消息包线程
     */
    private PacketReaderThread reader;

    /**
     * 是否属于长连接
     */
    private boolean longTerm = true;

    /**
     * Ping周期
     */
    private int pingPeriod;

    /**
     * 错误处理器
     */
    private ErrorHandler errorHandler;

    /**
     * 线程是否采用DAEMON方式运行
     */
    private boolean daemon = false;

    /**
     * 已经连接上的连接
     *
     * @param conn          Connection
     * @param packetListener 报文处理器
     * @param sendingQueue  报文发送队列
     * @see Connection
     */
    public ThreadConnection(Connection conn,
                             PacketListener packetListener,
                             Queue sendingQueue)
    {
        this.conn = conn;
        this.reader = new PacketReaderThread(this, packetListener);
        this.writer = new PacketWriterThread(sendingQueue, this);
    }

    /**
     * 启动连接
     */
    public void connect() throws IOException
    {
        writer.setErrorHandler(errorHandler);
        writer.setLongTerm(longTerm);
        if (pingPeriod > 0) {
            writer.setPingPeriod(pingPeriod);
        }
        writer.setDaemon(daemon);
        writer.start();

        reader.setDaemon(daemon);
        reader.setErrorHandler(errorHandler);
        reader.start();
    }

    /**
     * 连接超时，告诉另外一方，此连接已经超时
     *
     * @throws org.femtoframework.net.comm.CommException
     *          如果发送异常的时候抛出
     */
    public void timeout() throws IOException
    {
        conn.timeout();
    }

    /**
     * 发送Ping命令，检查连接是否正常
     *
     * @throws org.femtoframework.net.comm.CommException
     *          如果发送异常的时候抛出
     */
    public void ping() throws IOException
    {
        conn.ping();
    }

    /**
     * 读取一个信息包
     *
     * @return 返回一个信息包
     * @throws org.femtoframework.net.comm.CommException
     *          如果出现异常
     */
    public Packet readPacket() throws IOException
    {
        return conn.readPacket();
    }

    /**
     * 写出一个信息包
     *
     * @param packet 信息包
     * @throws org.femtoframework.net.comm.CommException
     *          如果出现异常
     */
    public void writePacket(Packet packet) throws IOException
    {
        conn.writePacket(packet);
    }

    /**
     * 关闭连接
     */
    public void close() throws IOException
    {
        reader.stop();
        writer.stop();
        reader.destroy();
        writer.destroy();
        conn.close();
    }

    /**
     * 是否还活着
     */
    public boolean isAlive()
    {
        return conn.isAlive()
               && ((reader != null && reader.isInitialized()) || reader == null)
               && ((writer != null && writer.isInitialized()) || writer == null);
    }

    /**
     * 哈希码
     */
    public int hashCode()
    {
        return conn.hashCode();
    }

    /**
     * 是否等效
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof ThreadConnection) {
            return conn.equals(((ThreadConnection) obj).conn);
        }
        else if (obj instanceof Connection) {
            return conn.equals(obj);
        }
        return false;
    }

    /**
     * 是否属于长连接
     *
     * @return 是否属于长连接
     */
    public boolean isLongTerm()
    {
        return longTerm;
    }

    /**
     * 设置Ping周期
     *
     * @return 设置Ping周期
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
     * 设置是否是长连接
     *
     * @param longTerm 是否是长连接
     */
    public void setLongTerm(boolean longTerm)
    {
        this.longTerm = longTerm;
    }

    /**
     * 返回错误处理
     *
     * @return 错误处理
     */
    public ErrorHandler getErrorHandler()
    {
        return errorHandler;
    }

    /**
     * 设置错误处理
     *
     * @param errorHandler 错误处理
     */
    public void setErrorHandler(ErrorHandler errorHandler)
    {
        this.errorHandler = errorHandler;
    }

    /**
     * 线程是否采用Daemon方式运行
     *
     * @return 是否采用Daemon方式运行
     */
    public boolean isDaemon()
    {
        return daemon;
    }

    /**
     * 设置是否DAEMON方式运行
     *
     * @param daemon
     */
    public void setDaemon(boolean daemon)
    {
        this.daemon = daemon;
    }
}
