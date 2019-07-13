package org.femtoframework.net.socket.endpoint;

import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executor;

import org.femtoframework.bean.AbstractLifecycle;
import org.femtoframework.bean.exception.InitializeException;
import org.femtoframework.net.socket.SocketEndpoint;

/**
 * Abstract Endpoint
 *
 * @author fengyun
 * @version 1.00 2005-2-28 0:26:21
 */
public abstract class AbstractEndpoint
    extends AbstractLifecycle
    implements SocketEndpoint, Executor
{
    private static final int BACKLOG = 500;
    private static final int TIMEOUT = 60000;

    protected int backlog = BACKLOG;
    protected int serverTimeout = TIMEOUT;

    private String host = null;
    protected InetAddress inet;
    protected int port;

    protected boolean tcpNoDelay = false;
    protected int linger = -1;
    protected int socketTimeout = -1;

    private Executor executor;

    protected volatile boolean running = false;
    private volatile boolean paused = false;

    protected final Object threadSync = new Object();

    /**
     * Check whether Launched
     */
    private boolean checkLaunched = false;

    /**
     * Is it secure
     */
    protected boolean secure = false;

    /**
     * SSL
     */
    public boolean isSecure()
    {
        return secure;
    }

    /**
     * Set secure
     *
     * @param secure SSL
     */
    public void setSecure(boolean secure)
    {
        this.secure = secure;
    }

    public boolean isCheckLaunched()
    {
        return checkLaunched;
    }

    public void setCheckLaunched(boolean checkLaunched)
    {
        this.checkLaunched = checkLaunched;
    }

    /**
     * Initialize
     */
    public void _doInit()
    {
        if (executor == null) {
            throw new InitializeException("No thread pool configurated");
        }
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public InetAddress getAddress()
    {
        return inet;
    }

    public void setAddress(InetAddress inet)
    {
        this.inet = inet;
    }

    public void setAddress(String addr)
    {
        if (addr != null && !"*".equals(addr) && addr.indexOf(',') < 0) {
            try {
                this.inet = InetAddress.getByName(addr);
            }
            catch (Exception e) {
                throw new InitializeException("Invalid host:" + addr);
            }
        }
        this.host = addr;
    }

    public void setHost(String host)
    {
        setAddress(host);
    }

    public boolean isRunning()
    {
        return running;
    }

    public boolean isPaused()
    {
        return paused;
    }

    /**
     * Allows the server developer to specify the backlog that
     * should be used for server sockets. By default, this value
     * is 100.
     */
    public void setBacklog(int backlog)
    {
        if (backlog > 0) {
            this.backlog = backlog;
        }
    }

    public int getBacklog()
    {
        return backlog;
    }

    /**
     * Sets the timeout in ms of the server sockets created by this
     * server. This method allows the developer to make servers
     * more or less responsive to having their server sockets
     * shut down.
     * <p/>
     * <p>By default this value is 1000ms.
     */
    public void setServerTimeout(int timeout)
    {
        this.serverTimeout = timeout;
    }

    public void setTcpNoDelay(boolean tcpNoDelay)
    {
        this.tcpNoDelay = tcpNoDelay;
    }

    public void setSoLinger(int soLinger)
    {
        this.linger = soLinger;
    }

    public void setSoTimeout(int soTimeout)
    {
        this.socketTimeout = soTimeout;
    }

    public void setServerSoTimeout(int serverTimeout)
    {
        this.serverTimeout = serverTimeout;
    }

    /**
     * Pause
     */
    public void pause()
    {
        if (running && !paused) {
            paused = true;
            doPause();
        }
    }

    protected abstract void doPause();

    public void resume()
    {
        if (running) {
            paused = false;
        }
    }

    public void _doStart()
    {
        running = true;
        paused = false;
    }

    public void setSocketOptions(Socket socket)
    {
        try {
            if (linger >= 0) {
                socket.setSoLinger(true, linger);
            }
            if (tcpNoDelay) {
                socket.setTcpNoDelay(tcpNoDelay);
            }
            if (socketTimeout > 0) {
                socket.setSoTimeout(socketTimeout);
            }
        }
        catch (SocketException se) {
        }
    }

    public Executor getExecutor()
    {
        return executor;
    }

    public String getHost()
    {
        return host;
    }

    public void execute(Runnable runnable)
    {
        executor.execute(runnable);
    }

    public void setExecutor(Executor executor)
    {
        this.executor = executor;
    }
}
