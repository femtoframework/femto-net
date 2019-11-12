package org.femtoframework.net.comm.tcp;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;

import org.femtoframework.bean.InitializableMBean;
import org.femtoframework.bean.annotation.Ignore;
import org.femtoframework.bean.exception.InitializeException;
import org.femtoframework.io.IOUtil;
import org.femtoframework.net.comm.AddressAware;
import org.femtoframework.net.comm.PacketProtocol;
import org.femtoframework.net.comm.packet.PacketConnection;
import org.femtoframework.parameters.Parameters;
import org.femtoframework.pattern.Loggable;
import org.slf4j.Logger;

/**
 * 基于Socket的连接
 *
 * @author fengyun
 * @version 1.00 2005-5-5 23:01:51
 */
public abstract class SocketConnection extends PacketConnection
    implements AddressAware, SocketAware, InitializableMBean, Loggable
{
    /**
     * 主机地址
     */
    private String host;

    /**
     * 主机端口
     */
    private int port;

    /**
     * Socket
     */
    private Socket socket;


    /**
     * 日志
     */
    @Ignore
    private Logger logger;

    /**
     * 设置日志
     *
     * @param log Logger
     */
    public void setLogger(Logger log)
    {
        this.logger = log;
    }

    /**
     * 返回日志
     *
     * @return Logger
     */
    public Logger getLogger()
    {
        return logger;
    }

    /**
     * 设置主机地址
     *
     * @param host 主机地址
     */
    public void setHost(String host)
    {
        this.host = host;
    }

    /**
     * 设置端口
     *
     * @param port 端口
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * 设置Socket
     *
     * @param socket Socket
     */
    public void setSocket(Socket socket)
        throws IOException
    {
        this.socket = socket;
        PacketProtocol protocol = getProtocol();
        if (protocol instanceof SocketAware) {
            ((SocketAware) protocol).setSocket(socket);
        }
    }

    /**
     * 返回主机名
     */
    public String getHost()
    {
        return host != null ? host : (socket != null ? socket.getInetAddress().getHostAddress() : null);
    }

    /**
     * 返回端口
     */
    public int getPort()
    {
        return port != 0 ? port : (socket != null ? socket.getPort() : 0);
    }

    /**
     * 设置报文协议
     *
     * @param protocol 报文协议
     */
    public void setProtocol(PacketProtocol protocol) throws IOException
    {
        super.setProtocol(protocol);
        //初始化Socket 和 连接参数
        if (socket != null && protocol instanceof SocketAware) {
            ((SocketAware) protocol).setSocket(socket);
        }
    }

    /**
     * 关闭连接
     *
     * @throws org.femtoframework.net.comm.CommException
     *          如果连接异常
     */
    protected void doClose() throws IOException
    {
        super.doClose();
        IOUtil.close(socket);
        socket = null;
    }

    private boolean initialized = false;

    /**
     * Return whether it is initialized
     *
     * @return whether it is initialized
     */
    public boolean isInitialized() {
        return this.initialized;
    }


    /**
     * Initialized setter for internal
     *
     * @param initialized BeanPhase
     */
    public void _doSetInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * 初始化（如果主机地址和端口已经赋值，那么进行构建Socket的过程）
     */
    public void _doInit()
    {
        if (host != null && port > 0) {
            try {
                Socket socket = createSocket(host, port, getParameters());
                if (socket != null) {
                    setSocket(socket);
                }
                else {
                    throw new InitializeException("Can't create socket to:[" + host + ":" + port + "]");
                }
            }
            catch (IOException e) {
                throw new InitializeException("Can't create socket to:[" + host + ":" + port + "]", e);
            }
        }
    }

    /**
     * 初始化Socket
     *
     * @param socket
     * @param parameters 创建连接需要的参数
     * @return 是否关闭该连接
     */
    protected boolean initSocket(Socket socket, Parameters parameters)
    {
        //为了扩展
        return false;
    }

    /**
     * 根据连接参数创建Socket
     *
     * @param host       主机地址
     * @param port       主机端口
     * @param parameters 创建连接需要的参数
     * @return
     * @throws IOException
     */
    protected Socket createSocket(String host, int port, Parameters parameters)
        throws IOException
    {
        Socket socket = null;
        int connectTimeout = parameters.getInt("connect_timeout", 60000);
        int soTimeout = parameters.getInt("so_timeout", 300000);
        boolean keepAlive = parameters.getBoolean("keep_alive", true);
        try {
            //增加了连接超时的设置，在JDK1.4中会起作用
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), connectTimeout);
            socket.setSoTimeout(soTimeout);
            socket.setKeepAlive(keepAlive);

            if (initSocket(socket, parameters)) {
                IOUtil.close(socket);
                socket = null;
                return null;
            }
            if (logger.isDebugEnabled()) {
                logger.debug(socket + " connected");
            }
        }
        catch (ConnectException ce) {
            logger.warn("Can't connect to " + host + ":" + port + " (" + ce.getMessage() + ")");
        }
        catch (NoRouteToHostException ne) {
            if (logger.isDebugEnabled()) {
                logger.debug("No route to host " + host + ":" + port);
            }
            throw ne;
        }
        catch (Throwable e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Connection broken! When connect to " + host + ":" + port, e);
            }
        }
        return socket;
    }
}
