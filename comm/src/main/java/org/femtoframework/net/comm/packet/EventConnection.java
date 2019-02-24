package org.femtoframework.net.comm.packet;

import java.io.IOException;

import org.femtoframework.net.comm.Connection;
import org.femtoframework.net.comm.ConnectionEvent;
import org.femtoframework.net.comm.ConnectionListener;
import org.femtoframework.net.comm.ConnectionMode;
import org.femtoframework.net.comm.ConnectionWrapper;
import org.femtoframework.net.comm.Packet;

/**
 * Socket连接
 *
 * @author fengyun
 * @version 1.00 2005-5-5 15:37:14
 */
public final class EventConnection //extends AbstractConnection
    implements ConnectionWrapper, Connection
{
    /**
     * 是否已经关闭
     */
    private boolean isClosed = false;

    /**
     * 连接侦听者
     */
    private ConnectionListener listener;

    /**
     * 信息包读写协议
     */
    private Connection connection;

    /**
     * 连接模式
     */
    private ConnectionMode mode = ConnectionMode.READ_WRITE;

    /**
     * 是否还处于激活状态
     *
     * @return 如果还在活动状态返回<code>true</code>，否则返回<code>false</code>
     */
    public boolean isAlive()
    {
        return connection.isAlive();
    }

    /**
     * 连接请求
     *
     * @throws org.femtoframework.net.comm.CommException 如果连接异常
     */
    public void connect() throws IOException
    {
        connection.connect();
        notifyEvent(ConnectionEvent.STATUS_CONNECTED);
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
        return connection.readPacket();
    }

    /**
     * 写出一个信息包
     *
     * @param packet 信息包
     * @throws IOException 如果出现异常
     */
    public void writePacket(Packet packet) throws IOException
    {
        connection.writePacket(packet);
    }

    /**
     * 设置事件侦听者
     *
     * @param listener 侦听者
     */
    public void setEventListener(ConnectionListener listener)
    {
        if (listener != null) {
            this.listener = listener;
        }
    }

    /**
     * 通知状态
     *
     * @param status 状态
     */
    protected void notifyEvent(int status)
    {
        //发送关闭事件
        if (listener != null) {
            ConnectionEvent event = new ConnectionEvent(this, status);
            event.dispatch(listener);
        }
    }


    /**
     * 字符串
     */
    public String toString()
    {
        return connection != null ? connection.toString() : null;
    }

    /**
     * 返回连接模式
     *
     * @return 连接模式
     */
    public ConnectionMode getMode()
    {
        return mode;
    }

    /**
     * 设置连接模式
     *
     * @param mode 连接模式
     */
    public void setMode(String mode)
    {
        this.mode = ConnectionMode.toMode(mode);
    }

    /**
     * 返回报文连接
     *
     * @return 报文连接
     */
    public Connection getConnection()
    {
        return connection;
    }

    /**
     * 设置报文连接
     *
     * @param connection 连接
     */
    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }

    /**
     * 连接超时，告诉另外一方，此连接已经超时
     *
     * @throws org.femtoframework.net.comm.CommException
     *          如果发送异常的时候抛出
     */
    public void timeout() throws IOException
    {
        connection.timeout();
        notifyEvent(ConnectionEvent.STATUS_TIMEOUT);
    }

    /**
     * 发送Ping命令，检查连接是否正常
     *
     * @throws org.femtoframework.net.comm.CommException
     *          如果发送异常的时候抛出
     */
    public void ping() throws IOException
    {
        connection.ping();
    }

    /**
     * 关闭通讯客户端
     *
     * @throws java.io.IOException 关闭的时候异常
     */
    public void close() throws IOException
    {
        if (!isClosed) {
            isClosed = true;

            try {
                connection.close();
            }
            catch (IOException e) {
            }
            finally {
                notifyEvent(ConnectionEvent.STATUS_CLOSED);
            }
        }
    }
}
