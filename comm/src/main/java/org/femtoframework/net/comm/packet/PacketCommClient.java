package org.femtoframework.net.comm.packet;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.util.ArrayList;
import java.util.List;

import org.femtoframework.bean.InitializableMBean;
import org.femtoframework.bean.annotation.Property;
import org.femtoframework.bean.exception.InitializeException;
import org.femtoframework.coin.metrics.annotations.Metric;
import org.femtoframework.coin.metrics.annotations.MetricType;
import org.femtoframework.coin.metrics.annotations.Tag;
import org.femtoframework.io.IOUtil;
import org.femtoframework.net.comm.*;
import org.femtoframework.lang.reflect.Reflection;
import org.femtoframework.parameters.Parameters;
import org.femtoframework.pattern.Loggable;
import org.femtoframework.util.queue.LinkedQueue;
import org.femtoframework.util.queue.Queue;
import org.femtoframework.util.status.StatusChangeListener;
import org.femtoframework.util.status.StatusChangeSensor;
import org.femtoframework.util.status.StatusChangeSupport;
import org.femtoframework.util.status.StatusEvent;
import org.femtoframework.util.thread.ErrorHandler;
import org.slf4j.Logger;

/**
 * 报文通讯通讯客户端
 * <p/>
 * <pre>
 * 主要功能：
 * 1. 提供了自动连接机制
 * 2. 提供ConnectionFactory的扩展机制，提供不同的连接创建方式扩展，并且能够由CommClient像Connection传递参数
 * 3. 提供ConnectionListener的扩展，方便在连接完成之后，能够从连接获取相应的信息
 * 4. 提供长连接和非长连接的选择
 * 5. 提供扩展的参数选项(比如说适用于Tcp连接的, connect_timeout so_timeout keep_alive等）
 * </pre>
 *
 * @author fengyun
 * @version 2.00 2005-5-5 16:21:27
 *          1.00 Mar 20, 2002 6:34:05 PM
 */
public class PacketCommClient
    implements CommClient, PacketLayer, Loggable, ConnectionListener,
               Runnable, PacketListener, CommConstants, StatusChangeSensor, InitializableMBean
{
    /**
     * 状态
     */
    private int status = STATUS_CREATED;

    /**
     * 消息包发送队列
     */
    private Queue<PacketWrapper> queue;

    /**
     * 是否主动连接
     */
    private boolean autoConnect = true;

    /**
     * 连接周期
     */
    private int connectPeriod = 20000;

    /**
     * 是否是长连接
     */
    private boolean longTerm = true;

    /**
     * 连接数组
     */
    private final List<ThreadConnection> connections = new ArrayList<ThreadConnection>(4);

    /**
     * 连接工厂
     */
    private ConnectionFactory connectionFactory = PacketConnectionFactory.getInstance();

    /**
     * 允许连接的最大连接数
     */
    private int maxConnCount = 1;

    /**
     * 日志
     */
    protected Logger logger;

    /**
     * 连接侦听者
     */
    private ConnectionListener connectionListener;

    /**
     * 错误处理
     */
    private ErrorHandler errorHandler = CommErrorHandler.getInstance();

    /**
     * Ping周期
     */
    private int pingPeriod;

    /**
     * 线程是否采用DAEMON方式运行
     */
    private boolean daemon = false;

    /**
     * 目标主机地址
     */
    private String host;

    /**
     * 目标主机端口
     */
    private int port;

    /**
     * 报文处理器
     */
    private PacketListener packetListener;

    /**
     * 自动连接处理器
     */
    private static AutoConnector autoConnector = new AutoConnector();

    /**
     * 创建连接需要的参数，为了提供扩展的方式，可以让构建连接的时候能够获取CommClient相关的信息
     */
    private Parameters connectionParameters = new ConnectionParameters(this);

    /**
     * 连接类
     */
    private Class connectionClass;

    /**
     * 协议类
     */
    private Class protocolClass;

    /**
     * 构造
     */
    public PacketCommClient()
    {
        queue = new LinkedQueue<>();
    }

    /**
     * 返回属性集合
     *
     * @return 属性集合
     */
    protected Parameters getProperties0()
    {
        return connectionParameters;
    }

    /**
     * 返回是否是长连接
     *
     * @return 是否是长连接
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

    /**
     * 是否自动连接
     *
     * @return 是否自动连接
     */
    public boolean isAutoConnect()
    {
        return autoConnect;
    }

    /**
     * 返回自动连接的周期（毫秒）
     *
     * @return 自动连接的周期
     */
    public int getConnectPeriod()
    {
        return connectPeriod;
    }

    /**
     * 设置自动连接的周期（毫秒）
     *
     * @param connectPeriod 自动连接周期（毫秒）
     */
    public void setConnectPeriod(int connectPeriod)
    {
        int oldPeriod = this.connectPeriod;
        this.connectPeriod = connectPeriod;
        //重写设置连接检查周期
        if (isAutoConnect() && oldPeriod != connectPeriod) {
            autoConnector.addClient(this);
        }
    }

    /**
     * 强制建立连接，确保通道畅通
     *
     * @return 有效的连接个数
     * @throws java.io.IOException 异常
     */
    public synchronized int connect() throws IOException
    {
        for (int i = connections.size() - 1; i >= 0; i--) {
            Connection conn = connections.get(i);
            if (!conn.isAlive()) {
                synchronized (connections) {
                    connections.remove(i);
                }
                IOUtil.close(conn);
            }
        }

        //Reconnect
        int liveCount = connections.size();
        for (int i = liveCount; i < maxConnCount; i++) {
            Connection conn = null;
            try {
                conn = createConnection();
                logger.info("Connection created:" + conn);
                liveCount++;
            }
            catch (IOException ex) {
                logger.warn("Can't create connection", ex);
                IOUtil.close(conn);
            }
            catch (InitializeException ie) {
                Throwable cause = ie.getCause();
                if (cause instanceof NoRouteToHostException) {
                    logger.warn("No Route To Host Exception, closing this client...." + cause.getMessage());
                    //The client should be closed
                    close();
                }
                else {
                    logger.warn("Can't create connection:" + ie.getMessage());
                    IOUtil.close(conn);
                }
            }
        }
        return liveCount;
    }


    /**
     * 创建连接
     *
     * @return 连接
     */
    public Connection createConnection() throws IOException
    {
        Connection conn = connectionFactory.createConnection(host, port, connectionParameters);
        EventConnection sconn = new EventConnection();
        sconn.setConnection(conn);
        sconn.setEventListener(this);
        sconn.connect();
        return sconn;
    }

    /**
     * 关闭通讯客户端
     *
     * @throws java.io.IOException 关闭的时候异常
     */
    public void close() throws IOException
    {
        //Remove from autoConnector anyway
        if (isAutoConnect()) {
            autoConnector.removeClient(this);
        }

        if (status != STATUS_CLOSED) {
            this.status = STATUS_CLOSED;

            doClose();

            List<ThreadConnection> copy = new ArrayList<>(connections);
            for (int i = 0, size = copy.size(); i < size; i++) {
                ThreadConnection conn = copy.get(i);
                IOUtil.close(conn);
            }

            setStatus(STATUS_CLOSED);
            logger.info("Client closed:" + toString());
        }
    }

    /**
     * 关闭其它部分，方便扩展
     */
    protected void doClose() throws IOException
    {
    }

    /**
     * 添加连接
     *
     * @param conn 连接
     * @return 是否添加成功
     */
    public boolean addConnection(Connection conn)
    {
        if (!(conn instanceof EventConnection)) {
            EventConnection econn = new EventConnection();
            econn.setConnection(conn);
            econn.setEventListener(this);
            conn = econn;
        }
        ThreadConnection wrapper = new ThreadConnection(conn, this, queue);
        wrapper.setErrorHandler(errorHandler);
        wrapper.setLongTerm(longTerm);
        wrapper.setPingPeriod(pingPeriod);
        wrapper.setDaemon(daemon);
        try {
            wrapper.connect();
        }
        catch (IOException e) {
            logger.warn("", e);
            return false;
        }
        synchronized (connections) {
            connections.add(wrapper);
        }
        if (!isAlive()) {
            setStatus(STATUS_ALIVE);
        }
        return true;
    }

    /**
     * 删除连接
     *
     * @param conn 连接
     * @return 是否已经删除
     */
    public boolean removeConnection(Connection conn)
    {
        //Reconnection
        synchronized (connections) {
            if (!connections.isEmpty()) {
                int size = connections.size();
                for (int i = size - 1; i >= 0; i--) {
                    ThreadConnection thread = connections.get(i);
                    if (thread.equals(conn)) {
                        connections.remove(i);
                        IOUtil.close(thread);
                        if (connections.isEmpty()) { //暂时没有连接并不代表服务器死了
                            logger.warn("No connection alive");
                            //等待若干秒
                            setStatus(STATUS_NO_CONNECTION);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //事件侦听者
    private StatusChangeListener listener = null;

    /**
     * 设置事件源和侦听者
     *
     * @param listener 侦听者
     */
    public void addStatusChangeListener(StatusChangeListener listener)
    {
        if (this.listener == null) {
            this.listener = listener;
        }
        else if (this.listener instanceof StatusChangeSupport) {
            ((StatusChangeSupport) this.listener).addStatusChangeListener(listener);
        }
        else {
            StatusChangeSupport support = new StatusChangeSupport();
            support.addStatusChangeListener(this.listener);
            support.addStatusChangeListener(listener);
            this.listener = support;
        }
    }

    /**
     * 删除状态改变侦听者
     *
     * @param listener 侦听者
     */
    public void removeStatusChangeListener(StatusChangeListener listener)
    {
        if (listener == null) {
            return;
        }
        if (this.listener instanceof StatusChangeSupport) {
            ((StatusChangeSupport) this.listener).removeStatusChangeListener(listener);
        }
        else if (this.listener == listener) {
            this.listener = null;
        }
    }

    /**
     * 设置状态
     *
     * @param status
     */
    private void setStatus(int status)
    {
        this.status = status;

        //分发事件
        if (listener != null) {
            dispatchEvent(new StatusEvent(status, this));
        }
    }

    /**
     * 分发事件
     *
     * @param event 事件
     */
    private void dispatchEvent(StatusEvent event)
    {
        event.dispatch(listener);
    }

    /**
     * 返回该通讯客户端是否存在Alive的连接
     *
     * @return 是否存在Alive的连接
     */
    public boolean isAlive()
    {
        return status == STATUS_ALIVE;
    }

    /**
     * 客户端是否已经关闭
     *
     * @return 是否已经关闭
     */
    public boolean isClosed()
    {
        return status == STATUS_CLOSED;
    }

    /**
     * 返回客户端状态
     *
     * @return 客户端状态
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * 返回当前连接数
     *
     * @return 连接数
     */
    @Metric(type= MetricType.GAUGE,
     name="femto_comm_client_connection_count",
    description = "Client connection count",
    tags = {
            @Tag(name="host", value="${host}"),
            @Tag(name="port", value="${port}")
    })
    @Property
    public int getConnCount()
    {
        return connections.size();
    }


    /**
     * Return queue size of this client
     *
     * @return Queue Size
     */
    @Metric(type= MetricType.GAUGE,
            name="femto_comm_client_queue_size",
            description = "Client queue size",
            tags = {
                    @Tag(name="host", value="${host}"),
                    @Tag(name="port", value="${port}")
            })
    @Property
    public int getQueueSize() {
        return queue.size();
    }

    /**
     * 返回允许连接的最大连接数
     *
     * @return 允许连接的最大连接数
     */
    @Metric(type= MetricType.GAUGE,
            name="femto_comm_client_max_connection_count",
            description = "Client maximum connection count",
            tags = {
                    @Tag(name="host", value="${host}"),
                    @Tag(name="port", value="${port}")
            })
    @Property
    public int getMaxConnCount()
    {
        return maxConnCount;
    }

    /**
     * 设置最大连接数
     *
     * @param maxConnCount 最大连接数
     */
    public void setMaxConnCount(int maxConnCount)
    {
        this.maxConnCount = maxConnCount;
    }

    /**
     * 设置日志处理器
     *
     * @param logger 日志处理器
     */
    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    /**
     * 返回日志处理器
     *
     * @return 日志处理器
     */
    public Logger getLogger()
    {
        return logger;
    }

    /**
     * 已经连接
     *
     * @param e 连接事件
     */
    public void connected(ConnectionEvent e)
    {
        Connection conn = (Connection) e.getSource();
        addConnection(conn);
        if (connectionListener != null) {
            connectionListener.connected(e);
        }
    }

    /**
     * 连接超时
     *
     * @param e 连接事件
     */
    public void timeout(ConnectionEvent e)
    {
        Connection conn = (Connection) e.getSource();
        if (removeConnection(conn)) {
            logger.info("Connection timeout:" + conn);
        }
        if (connectionListener != null) {
            connectionListener.timeout(e);
        }
    }

    /**
     * 连接断了
     *
     * @param e 连接事件
     */
    public void closed(ConnectionEvent e)
    {
        Connection conn = (Connection) e.getSource();
        if (removeConnection(conn)) {
            logger.info("Connection closed:" + conn);
        }
        if (connectionListener != null) {
            connectionListener.closed(e);
        }
    }

    /**
     * 返回连接侦听者
     *
     * @return 连接侦听者
     */
    public ConnectionListener getConnectionListener()
    {
        return connectionListener;
    }

    /**
     * 设置连接侦听者
     *
     * @param connectionListener 连接侦听者
     */
    public void setConnectionListener(ConnectionListener connectionListener)
    {
        this.connectionListener = connectionListener;
    }

    /**
     * 设置是否自动连接
     *
     * @param autoConnect 是否自动连接
     */
    public void setAutoConnect(boolean autoConnect)
    {
        if (autoConnect && !this.autoConnect) {
            autoConnector.addClient(this);
        }
        this.autoConnect = autoConnect;
    }

    /**
     * 设置Ping周期
     *
     * @return Ping周期
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

    /**
     * 返回连接工厂
     *
     * @return 连接工厂
     */
    public ConnectionFactory getConnectionFactory()
    {
        return connectionFactory;
    }

    /**
     * 设置连接工厂
     *
     * @param connectionFactory 连接工厂
     */
    public void setConnectionFactory(ConnectionFactory connectionFactory)
    {
        this.connectionFactory = connectionFactory;
    }

    /**
     * 返回目标主机地址
     *
     * @return 目标主机地址
     */
    public String getHost()
    {
        return host;
    }

    /**
     * 设置目标主机地址
     *
     * @param host 主机地址
     */
    public void setHost(String host)
    {
        this.host = host;
    }

    /**
     * 返回目标主机端口
     *
     * @return 目标主机端口
     */
    public int getPort()
    {
        return port;
    }

    /**
     * 设置目标主机端口
     *
     * @param port 主机端口
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * 返回实际的报文处理器
     *
     * @return 实际的报文处理器
     */
    public PacketListener getPacketListener()
    {
        return packetListener;
    }

    /**
     * 设置实际的报文处理器
     *
     * @param packetListener 报文处理器
     */
    public void setPacketListener(PacketListener packetListener)
    {
        this.packetListener = packetListener;
    }

    /**
     * 处理信息包
     *
     * @param packet 信息包
     */
    public void onPacket(Packet packet)
    {
        if (packetListener != null) {
            packetListener.onPacket(packet);
        }
        else {
            throw new IllegalStateException("No packet listener set");
        }
    }

    /**
     * 字符串信息
     *
     * @return 字符串信息
     */
    public String toString()
    {
        return "PacketCommClient{" +
               "host='" + host + "'" +
               ", port=" + port +
               "}";
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run()
    {
        try {
            connect();
        }
        catch (Throwable t) {
        }
        //如果是非长连接，那么停止自动连接
        if (!isLongTerm()) {
            autoConnector.removeClient(this);
        }
    }

    /**
     * 返回连接对应的类
     *
     * @return 连接对应的类
     */
    public Class getConnectionClass()
    {
        return connectionClass;
    }

    /**
     * 设置连接对应的类
     *
     * @param connectionClass
     */
    public void setConnectionClass(Class connectionClass)
    {
        this.connectionClass = connectionClass;
    }

//    /**
//     * 设置连接对应的类
//     *
//     * @param connectionClass
//     */
//    public void setConnectionClass(String connectionClass) throws ClassNotFoundException
//    {
//        setConnectionClass(Reflection.loadClass(connectionClass));
//    }

    /**
     * 返回协议类
     *
     * @return 协议类
     */
    public Class getProtocolClass()
    {
        return protocolClass;
    }

    /**
     * 设置协议类
     *
     * @param protocolClass
     */
    public void setProtocolClass(Class protocolClass)
    {
        this.protocolClass = protocolClass;
    }

//    /**
//     * 设置协议类
//     *
//     * @param protocolClass
//     */
//    public void setProtocolClass(String protocolClass) throws ClassNotFoundException
//    {
//        setProtocolClass(Reflection.loadClass(protocolClass));
//    }

    /**
     * 设置新的属性集合
     *
     * @param params 新的属性集合
     */
    public void setProperties(Parameters params)
    {
    }

    /**
     * 返回属性集合
     *
     * @return 属性集合
     */
    public Parameters getProperties()
    {
        return connectionParameters;
    }

    /**
     * 删除指定的属性
     *
     * @param name 属性名称
     * @return 指定的属性
     */
    public Object removeProperty(String name)
    {
        return null;
    }

    /**
     * 清除所有的属性
     */
    public void clearProperties()
    {
    }

    /**
     * 返回属性的总数
     *
     * @return 属性的总数
     */
    public int getPropertySize()
    {
        return 0;
    }

    /**
     * 判断是否有属性
     *
     * @return 是否有属性
     */
    public boolean hasProperty()
    {
        return true;
    }

    /**
     * 判断是否拥有指定名称的属性
     *
     * @param name 指定名称的属性
     * @return 是否有属性
     */
    public boolean hasProperty(String name)
    {
        return connectionParameters.containsKey(name);
    }

    /**
     * 发送报文给下一层
     *
     * @param packet 报文
     */
    public PacketFuture send(Packet packet)
    {
        if (packet == null) {
            throw new IllegalArgumentException("Null packet");
        }

        PacketWrapper pw = new PacketWrapper(queue, packet);
        queue.offer(pw);
        return pw;
    }

    /**
     * 初始化
     */
    @Override
    public void _doInit()
    {
        if (isAutoConnect()) {
            autoConnector.addClient(this);
        }
    }

    private boolean initialized = false;
    /**
     * Return whether it is initialized
     *
     * @return whether it is initialized
     */
    @Override
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Initialized setter for internal
     *
     * @param initialized BeanPhase
     */
    @Override
    public void _doSetInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}
