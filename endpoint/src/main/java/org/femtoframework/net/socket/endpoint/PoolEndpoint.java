package org.femtoframework.net.socket.endpoint;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

import org.femtoframework.bean.exception.InitializeException;
import org.femtoframework.implement.ImplementUtil;
import org.femtoframework.io.IOUtil;
import org.femtoframework.net.socket.SocketEndpointAware;
import org.femtoframework.net.socket.SocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread pool endpoint
 *
 * @author fengyun
 * @version 1.00 2005-2-28 0:31:06
 */
public class PoolEndpoint extends AbstractEndpoint implements SocketHandler {

    protected SocketHandler handler;

    private ServerSocketFactory factory;
    private ServerSocket serverSocket;

    protected Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Logger
     *
     * @return Logger
     */
    public Logger getLogger() {
        return log;
    }

    protected ServerSocket createServerSocket() {
        ServerSocket serverSocket = null;
        try {
            if (factory == null) {
                if (secure) {
                    factory = SSLServerSocketFactory.getDefault();
                }
                else {
                    factory = ServerSocketFactory.getDefault();
                }
            }
            if (serverSocket == null) {
                try {
                    if (inet == null) {
                        serverSocket = factory.createServerSocket(port, backlog);
                    }
                    else {
                        serverSocket = factory.createServerSocket(port, backlog, inet);
                    }
                }
                catch (BindException be) {
                    throw new BindException(be.getMessage() + ":" + port);
                }
            }
            if (serverTimeout >= 0) {
                serverSocket.setSoTimeout(serverTimeout);
            }
        }
        catch (IOException ex) {
            //        log("couldn't start endpoint", ex, Logger.DEBUG);
            throw new InitializeException("IOException", ex);
        }
        return serverSocket;
    }

    public void _doInit() {
        super._doInit();
        serverSocket = createServerSocket();
    }

    public void _doStart() {
        super._doStart();

        if (isCheckLaunched()) {
            CheckLaunched handler = ImplementUtil.getInstance(CheckLaunched.class);
            handler.setHandler(this.handler);
            this.handler = handler;
        }

        SocketWorker worker = new SocketWorker(this);
        execute(worker);
    }

    public void _doStop() {
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
            try {
                serverSocket.close();
            }
            catch (Exception e) {
                log.debug("Caught exception trying to close socket.", e);
            }
            serverSocket = null;
        }
    }

    public void _doDestroy() {
    }

    public void setServerSocketFactory(ServerSocketFactory factory) {
        this.factory = factory;
    }

    public ServerSocketFactory getServerSocketFactory() {
        return factory;
    }

    public void setHandler(SocketHandler handler) {
        if (handler instanceof SocketEndpointAware) {
            ((SocketEndpointAware)handler).setEndpoint(this);
        }
        this.handler = handler;
    }

    public SocketHandler getHandler() {
        return handler;
    }

    protected void doPause() {
        unlockAccept();
    }

    protected void unlockAccept() {
        Socket s = null;
        try {
            // Need to create a connection to unlock the accept();
            if (inet == null) {
                s = new Socket("127.0.0.1", port);
            }
            else {
                s = new Socket(inet, port);
                // setting soLinger to a small value will help shutdown the
                // connection quicker
                s.setSoLinger(true, 0);
            }
        }
        catch (Exception e) {
            log.debug("Caught exception trying to unlock accept on " + port
                      + " " + e.toString());
        }
        finally {
            IOUtil.close(s);
        }
    }

    protected Socket acceptSocket() {
        Socket accepted = null;

        try {
            if (running) {
                if (null != serverSocket) {
                    accepted = serverSocket.accept();
                    if (!running) {
                        if (null != accepted) {
                            accepted.close();  // rude, but unlikely!
                            accepted = null;
                        }
                    }
                }
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
                if (accepted != null) {
                    try {
                        accepted.close();
                        accepted = null;
                    }
                    catch (Exception ex) {
                        msg = "Endpoint " + accepted.toString() + " ignored exception: " + ex.getMessage();
                        log.info(msg, ex);
                    }
                }
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
                        serverSocket = createServerSocket();
                    }
                    catch (Throwable t) {
                        msg = "Endpoint " + serverSocket.toString() + " shutdown due to exception: " + t.getMessage();
                        log.error(msg, t);
                        stop();
                    }
                }
            }
        }

        return accepted;
    }

    protected boolean checkSocket(Socket s) {
        return s == null;
    }

    public void handle(Socket socket) {
        handler.handle(socket);
    }
}
