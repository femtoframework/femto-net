package org.femtoframework.net.socket;


import org.femtoframework.parameters.Parameters;
import org.femtoframework.util.status.Status;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Socket Context
 *
 * @author fengyun
 * @version 1.00 Aug 8, 2003 6:42:04 PM
 */
public interface SocketContext extends Status, Parameters, SocketActions
{

    /**
     * Normal status code
     */
    int SC_OK = 200;

    /**
     * Internal server error
     */
    int SC_INTERNAL_SERVER_ERROR = 500;

    /**
     * Is a secure socket?
     * 
     * @return secure socket
     */
    boolean isSecure();

    /**
     * Set secure
     *
     * @param secure Secure
     */
    void setSecure(boolean secure);

    /**
     * Returns remote socket address
     *
     * @return client socket address
     */
    InetSocketAddress getRemoteAddress();

    /**
     * Returns local socket address
     *
     * @return Local socket address
     */
    InetSocketAddress getLocalAddress();

    /**
     * Socket
     *
     * @return Socket
     */
    Socket getSocket();

    /**
     * Input Stream
     *
     * @return InputStream from socket
     */
    InputStream getInputStream() throws IOException;

    /**
     * Output Stream
     *
     * @return Output Stream
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * Returns current action
     *
     * @return Action
     */
    int getAction();

    /**
     * Set current action
     *
     * @param action Current Action
     */
    void setAction(int action);


    /**
     * Set code
     *
     * @param code Code
     */
    void setCode(int code);

    /**
     * Set message
     *
     * @param message Message
     */
    void setMessage(String message);

    /**
     * Set code + message
     *
     * @param code Error Code
     * @param message Error Message
     */
    void sendError(int code, String message);

    /**
     * Is it finished
     */
    boolean isFinished();

    /**
     * Finish current socket
     */
    void finish();

    /**
     * Close socket
     */
    void close();

    /**
     * Close socket with given code and message
     *
     * @param code Code
     * @param message Message
     */
    void close(int code, String message);

    /**
     * Hooked on closed
     */
    void onClosed();

    /**
     * Add SocketContextListener
     *
     * @param listener SocketContextListener
     */
    void addListener(SocketContextListener listener);

    /**
     * Delete SocketContextListener
     *
     * @param listener SocketContextListener
     */
    void removeListener(SocketContextListener listener);

    /**
     * Return current socket context listener
     *
     * @return SocketContextListener
     */
    SocketContextListener getListener();

    /**
     * Returns current auth mechanism name
     *
     * @return auth mechanism name
     */
    String getAuthMechanismName();

    /**
     * Sets auth mechanism name
     *
     * @param mechamismName auth mechanism name
     */
    void setAuthMechanismName(String mechamismName);

    /**
     * Delay to close, anti DDOS
     *
     * @param error Error message
     */
    void delayToClose(byte[] error);
}

