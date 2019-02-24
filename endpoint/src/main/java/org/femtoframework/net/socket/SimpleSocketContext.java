package org.femtoframework.net.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.femtoframework.io.IOUtil;
import org.femtoframework.net.socket.close.CloseHandler;
import org.femtoframework.parameters.ParametersMap;


/**
 * Simple Socket Context
 *
 * @author fengyun
 * @version 1.00 Aug 8, 2003 6:56:02 PM
 */
public class SimpleSocketContext extends ParametersMap implements SocketContext
{

    protected Socket socket;

    private InetSocketAddress remote;
    private InetSocketAddress local;


    private int code;

    private String message;

    protected InputStream input;

    protected OutputStream output;

    private boolean finished = false;

    protected SocketContextListener listener;

    private int action = ACTION_CONNECTED;

    private boolean secure = false;

    private String authMechanismName;

    private boolean delayToClose = false;

    /**
     * Delay to close
     */
    private int delayToCloseTime = 60 * 1000;


    private byte[] delayToCloseError = null;


    protected transient CloseHandler closeHandler;

    public SimpleSocketContext(Socket socket)
    {
        init(socket);
    }

    public SimpleSocketContext()
    {
    }

    /**
     * Initialize Socket
     *
     * @param socket Socket
     */
    public void init(Socket socket)
    {
        this.action = ACTION_CONNECTED;
        this.finished = false;
        output = null;
        input = null;
        this.socket = socket;
        this.remote = null;
        this.secure = false;
        this.authMechanismName = null;
        delayToCloseError = null;

        this.delayToClose = false;
        this.listener = null;
    }

    /**
     * Is a secure socket?
     *
     * @return secure socket
     */
    public boolean isSecure()
    {
        return secure;
    }

    /**
     * Returns remote socket address
     *
     * @return client socket address
     */
    public InetSocketAddress getRemoteAddress()
    {
        if (remote == null) {
            remote = socket != null ? (InetSocketAddress)socket.getRemoteSocketAddress() : null;
        }
        return remote;
    }

    /**
     * Returns local socket address
     *
     * @return Local socket address
     */
    public InetSocketAddress getLocalAddress()
    {
        if (local == null) {
            local = socket != null ? (InetSocketAddress)socket.getLocalSocketAddress() : null;
        }
        return local;
    }

    /**
     * Socket
     *
     * @return Socket
     */
    public Socket getSocket()
    {
        return socket;
    }

    /**
     * Input Stream
     *
     * @return InputStream from socket
     */
    public InputStream getInputStream() throws IOException
    {
        if (input == null) {
            input = socket.getInputStream();
        }
        return input;
    }

    /**
     * Output Stream
     *
     * @return Output Stream
     */
    public OutputStream getOutputStream() throws IOException
    {
        if (output == null) {
            output = socket.getOutputStream();
        }
        return output;
    }

    /**
     * Returns current action
     *
     * @return Action
     */
    public int getAction()
    {
        return action;
    }

    /**
     * Set current action
     *
     * @param action Current Action
     */
    public void setAction(int action)
    {
        this.action = action;
    }

    /**
     * Finish
     */
    public void finish()
    {
        if (finished) {
            return;
        }
        finished = true;
        doFinish();
        //Event
        fireEvent(ACTION_FINISHED);
    }

    /**
     * Do finish
     */
    protected void doFinish()
    {
    }

    /**
     * Close socket with given code and message
     *
     * @param code Code
     * @param message Message
     */
    public void close(int code, String message)
    {
        if (code > 0) {
            setCode(code);
        }
        if (message != null) {
            setMessage(message);
        }
        close();
    }

    /**
     * Close socket
     */
    public void close()
    {
        if (!isFinished()) {
            finish();
        }


        try {
            fireEvent(ACTION_CLOSING);
        }
        catch (Exception ex) {
        }

        code = 0;
        message = null;
        remote = null;
        local = null;
        secure = false;

        closeSocket();

        //Clear all
        clear();

        fireClosedEvent();
    }

    /**
     * Hooked on closed
     */
    public void onClosed()
    {
        socket = null;
        output = null;
        input = null;
        delayToCloseError = null;
        this.listener = null;
    }

    /**
     * Delay to close, anti DDOS
     *
     * @param error Error message
     */
    public void delayToClose(byte[] error)
    {
        this.delayToCloseError = error;
        this.delayToClose = true;
    }

    protected void closeSocket()
    {
        if (delayToClose) {
            closeHandler.handle(socket, delayToCloseTime, delayToCloseError);
        }
        else {
            IOUtil.close(input);
            IOUtil.close(output);
            IOUtil.close(socket);
        }
    }

    protected void fireClosedEvent()
    {
        //Event
        try {
            fireEvent(ACTION_CLOSED);
        }
        catch (Exception e) {
            //Ignore
        }
    }

    /**
     * Set code
     *
     * @param code Code
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Set message
     *
     * @param message Message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Set code + message
     *
     * @param code Error Code
     * @param message Error Message
     */
    public void sendError(int code, String message) {
        setCode(code);
        setMessage(message);

        finish();
    }

    /**
     * Is it finished
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Status code
     *
     * @return Code
     */
    public int getCode() {
        return code;
    }

    /**
     * Status message
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Add SocketContextListener
     *
     * @param listener SocketContextListener
     */
    public void addListener(SocketContextListener listener)
    {
        if (listener == null) {
            return;
        }

        SocketContextListener old = this.listener;
        if (old == null) {
            this.listener = listener;
        }
        else if (old instanceof SocketContextListeners) {
            ((SocketContextListeners)old).addListener(listener);
        }
        else {
            SocketContextListeners listeners = new SocketContextListeners(old);
            listeners.addListener(listener);
            this.listener = listeners;
        }
    }

    /**
     * Remove SocketContextListener
     *
     * @param listener SocketContextListener
     */
    public void removeListener(SocketContextListener listener)
    {
        if (listener == null) {
            return;
        }

        SocketContextListener old = this.listener;
        if (old == listener) {
            this.listener = null;
        }
        else if (old instanceof SocketContextListeners) {
            ((SocketContextListeners)listener).removeListener(listener);
        }
    }

    /**
     * Return current SocketContextListener
     *
     * @return SocketContextListener
     */
    public SocketContextListener getListener()
    {
        return listener;
    }

    /**
     * Returns current auth mechanism name
     *
     * @return auth mechanism name
     */
    public String getAuthMechanismName()
    {
        if (authMechanismName == null) {
            authMechanismName = getString("auth_mechanism_name");
        }
        return authMechanismName;
    }

    /**
     * Sets auth mechanism name
     *
     * @param mechamismName auth mechanism name
     */
    public void setAuthMechanismName(String mechamismName)
    {
        this.authMechanismName = mechamismName;
    }

    /**
     * Fire action
     *
     * @param action Action
     */
    public void fireEvent(int action)
    {
        setAction(action);
        if (listener != null) {
            listener.handleEvent(action, this);
        }
    }

    /**
     * Set secure
     *
     * @param secure Secure
     */
    public void setSecure(boolean secure)
    {
        this.secure = secure;
    }

    public int getDelayToCloseTime()
    {
        return delayToCloseTime;
    }

    public void setDelayToCloseTime(int delayToCloseTime)
    {
        this.delayToCloseTime = delayToCloseTime;
    }

    public void setCloseHandler(CloseHandler closeHandler) {
        this.closeHandler = closeHandler;
    }
}
