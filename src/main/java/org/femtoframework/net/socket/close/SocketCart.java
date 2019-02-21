package org.femtoframework.net.socket.close;

import org.femtoframework.io.IOUtil;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Socket Cart
 *
 * @author fengyun
 * @version 1.00 Oct 23, 2003 6:23:33 PM
 */
public class SocketCart implements Closeable
{
    public static final byte[] CRLF = "\r\n".getBytes();

    private Socket socket;

    protected long timestamp;

    private byte[] error = null;

    public SocketCart(Socket socket, int delayTime)
    {
        this(socket, delayTime, null);
    }

    public SocketCart(Socket socket, int delayTime, byte[] error)
    {
        this.socket = socket;
        this.timestamp = System.currentTimeMillis() + delayTime;
        this.error = error;
    }

    public boolean isTimeout()
    {
        return timestamp > System.currentTimeMillis();
    }

    public Socket getSocket()
    {
        return socket;
    }

    public void close()
    {
        if (error != null) {
            writeError();
        }
        IOUtil.close(socket);
    }

    private void writeError()
    {
        setBlocking(socket);
        try {
            OutputStream out = socket.getOutputStream();
            out.write(error);
            out.write(CRLF);
            out.flush();
        }
        catch (IOException ioe) {
            //Ignore
        }
    }

    private void setBlocking(Socket socket)
    {
        try {
            socket.getChannel().configureBlocking(true);
        }
        catch (Exception e) {
        }
    }

    public long getTimestamp()
    {
        return timestamp;
    }


    public byte[] getError()
    {
        return error;
    }

    public void setError(byte[] error)
    {
        this.error = error;
    }

    public void setError(String error)
    {
        setError(error.getBytes());
    }

}
