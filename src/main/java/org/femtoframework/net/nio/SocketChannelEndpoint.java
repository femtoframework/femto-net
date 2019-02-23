package org.femtoframework.net.nio;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImplFactory;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.femtoframework.bean.exception.InitializeException;
import org.femtoframework.lang.reflect.Reflection;
import org.femtoframework.lang.reflect.ReflectionException;
import org.femtoframework.net.socket.SocketEndpointAware;
import org.femtoframework.net.socket.endpoint.AbstractEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 非阻塞的Socket通道侦听者（采用非阻塞的方式Accept性能跟阻塞式差不多）
 *
 * @author fengyun
 */
public class SocketChannelEndpoint extends AbstractEndpoint
{
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * 端口侦听
     */
    private ServerSocketChannel serverSocket;

    /**
     * 是否采用PoolSocket
     */
    private boolean poolSocket = false;

    /**
     * InetSocketAddress
     */
    private InetSocketAddress socketAddr;

    /**
     * 处理器
     */
    private SocketChannelHandler handler;

    /**
     * Socket
     */
    private int bufferSize = DEFAULT_BUFFER_SIZE;


    /**
     * 设置Logger
     */
    private Logger log = LoggerFactory.getLogger(SocketChannelEndpoint.class);

    private SocketChannelFactory factory;

    /**
     * 返回日志
     *
     * @return Logger
     */
    public Logger getLogger()
    {
        return log;
    }

    /**
     * 创建服务器通道
     *
     * @param addr 地址
     * @return
     * @throws IOException
     */
    protected ServerSocketChannel createServerSocket(InetSocketAddress addr)
        throws IOException
    {
        ServerSocketChannel channel = factory.createServerSocketChannel();
        // Get the associated ServerSocket to bind it with
        ServerSocket serverSocket = channel.socket();
        // Set the port the server channel will listen to
        serverSocket.bind(addr, backlog);
        serverSocket.setReceiveBufferSize(bufferSize);
        // Set nonblocking mode for the listening socket
//        channel.configureBlocking(false);
//        this.selector = Selector.open();
        // Register the ServerSocketChannel with the Selector
//        channel.register(selector, SelectionKey.OP_ACCEPT);
        return channel;
    }

    /**
     * 出始化
     */
    public void _doInitialize()
    {
        super._doInitialize();
        if (factory == null) {
            factory = new DefaultChannelFactory();
        }
        if (inet == null) {
            this.socketAddr = new InetSocketAddress(port);
        }
        else {
            this.socketAddr = new InetSocketAddress(inet, port);
        }

        if (serverSocket == null) {
            try {
                this.serverSocket = createServerSocket(socketAddr);
            }
            catch (IOException ioe) {
                log.warn("Can't start server socket", ioe);
                throw new InitializeException("Start server socket exception:", ioe);
            }
        }

        if (poolSocket) {
            try {
                SocketImplFactory factory = (SocketImplFactory) Reflection.newInstance("java.net.PoolSocketImplFactory");
                Socket.setSocketImplFactory(factory);
            }
            catch (IOException e) {
            }
            catch (ReflectionException e) {
            }
        }
    }

    /**
     * 实际启动实现
     */
    public void _doStart()
    {
        super._doStart();

        SocketChannelWorker worker = new SocketChannelWorker(this);
        execute(worker);
    }

    /**
     * 获取SocketChannel
     *
     * @return SocketChannel
     */
    protected SocketChannel acceptSocket()
    {
        SocketChannel accepted = null;
        try {
            if (running) {
                accepted = serverSocket.accept();
            }
        }
        catch (InterruptedIOException iioe) {
            // normal part -- should happen regularly so
            // that the endpoint can release if the server
            // is shutdown.
        }
        catch (IOException e) {
            if (running) {
                String msg = "Endpoint " + serverSocket.toString() + " ignored exception: " + e.getMessage();
                log.error(msg, e);
                // Restart endpoint when getting an IOException during accept
                synchronized (threadSync) {
                    try {
                        serverSocket.close();
                    }
                    catch (Exception ex) {
                        msg = "Endpoint " + serverSocket.toString() + " ignored exception: " + ex.getMessage();
                        log.info(msg, ex);
                    }
                    serverSocket = null;
                    try {
                        serverSocket = createServerSocket(socketAddr);
                    }
                    catch (Throwable t) {
                        msg = "Endpoint " + socketAddr + " shutdown due to exception: " + t.getMessage();
                        log.error(msg, t);
                        stop();
                    }
                }
            }
        }

        return accepted;
    }

    /**
     * 处理Socket
     *
     * @param channel
     * @return 是否丢弃指定的Socket
     */
    protected boolean checkSocket(SocketChannel channel)
    {
        if (channel != null) {
            try {
                factory.initChannel(channel);
                return false;
            }
            catch (IOException e) {
                return true;
            }
        }
        return true;
    }


    /**
     * 停止服务
     */
    protected synchronized void doStop()
    {
        if (running) {
            running = false;
            try {
                // Need to create a connection to unlock the accept();
                Socket s;
                if (inet == null) {
                    s = new Socket("127.0.0.1", port);
                }
                else {
                    s = new Socket(inet, port);
                    // setting soLinger to a small value will help shutdown the
                    // connection quicker
                    s.setSoLinger(true, 0);
                }
                s.close();
            }
            catch (Exception e) {
                log.debug("Caught exception trying to unlock accept.", e);
            }

            if (serverSocket != null) {
                try {
                    serverSocket.close();
                }
                catch (Exception e) {
                    log.debug("Caught exception trying to close socket.", e);
                }
            }
            serverSocket = null;
        }
    }

    /**
     * Do Pause
     */
    protected void doPause()
    {
        //todo
    }

    protected synchronized void doDestroy()
    {
        serverSocket = null;
    }

    public int getBufferSize()
    {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize)
    {
        this.bufferSize = bufferSize;
    }

    public SocketChannelHandler getHandler()
    {
        return handler;
    }

    public void setHandler(SocketChannelHandler handler)
    {
        if (handler instanceof SocketEndpointAware) {
            ((SocketEndpointAware)handler).setEndpoint(this);
        }
        this.handler = handler;
    }

    public boolean isPoolSocket()
    {
        return poolSocket;
    }

    public void setPoolSocket(boolean poolSocket)
    {
        this.poolSocket = poolSocket;
    }

    public SocketChannelFactory getFactory()
    {
        return factory;
    }

    public void setFactory(SocketChannelFactory factory)
    {
        this.factory = factory;
    }
}
