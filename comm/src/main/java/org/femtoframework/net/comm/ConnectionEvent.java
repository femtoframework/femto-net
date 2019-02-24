package org.femtoframework.net.comm;

import java.util.EventObject;

/**
 * 连接事件
 *
 * @author fengyun
 * @version 1.00 Mar 14, 2002 2:21:10 PM
 * @see Connection
 */
public class ConnectionEvent
    extends EventObject
{
    /**
     * 刚创建状态
     */
    public static final int STATUS_CREATED = 0;

    /**
     * 已连接状态
     */
    public static final int STATUS_CONNECTED = 1;

    /**
     * 超时状态
     */
    public static final int STATUS_TIMEOUT = 2;

    /**
     * 连接断开状态
     */
    public static final int STATUS_CLOSED = 4;

    /**
     * 连接状态
     */
    protected int status;

    /**
     * 连接
     */
    private transient Connection connection;

    /**
     * 构造
     *
     * @param connection 连接
     * @param status     状态
     */
    public ConnectionEvent(Connection connection, int status)
    {
        super(connection);
        this.status = status;
        this.connection = connection;
    }

    /**
     * 返回状态
     *
     * @return status 状态
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * 分发事件
     *
     * @param listener 事件侦听者
     */
    public void dispatch(ConnectionListener listener)
    {
        if (status == STATUS_CONNECTED) {
            listener.connected(this);
        }
        else if (status == STATUS_TIMEOUT) {
            listener.timeout(this);
        }
        else if (status == STATUS_CLOSED) {
            listener.closed(this);
        }
    }

    /**
     * 返回连接
     *
     * @return
     */
    public Connection getConnection()
    {
        return connection;
    }
}
