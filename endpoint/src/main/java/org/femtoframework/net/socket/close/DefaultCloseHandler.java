package org.femtoframework.net.socket.close;

import org.femtoframework.bean.AbstractLifecycle;
import org.femtoframework.util.SortedList;

import java.net.Socket;
import java.util.Comparator;

/**
 * Socket关闭队列
 *
 * @author fengyun
 * @version 1.00 Oct 23, 2003 6:21:08 PM
 */
public class DefaultCloseHandler
    extends AbstractLifecycle
    implements CloseHandler, Comparator<SocketCart>, Runnable
{
    public static final int DEFAULT_SLEEP_TIME = 1000;

    private final SortedList<SocketCart> queue;
    private boolean isRunning = true;

    private Thread thread;

    public DefaultCloseHandler()
    {
        queue = new SortedList<>(this);
        thread = new Thread(this);
    }

    public void run()
    {
        int sleepTime = DEFAULT_SLEEP_TIME;
        SocketCart cart = null;
        while (isRunning) {
            try {
                synchronized (queue) {
                    if (queue.isEmpty()) {
                        queue.wait(DEFAULT_SLEEP_TIME);
                    }
                    else {
                        cart = queue.get(0);
                    }
                    if (cart != null) {
                        sleepTime = (int) (cart.getTimestamp() - System.currentTimeMillis());
                        if (sleepTime <= 0) {
                            queue.remove(0);
                            cart.close();
                            cart = null;
                        }
                        else {
                            queue.wait(sleepTime);
                        }
                    }
                }
            }
            catch (Throwable t) {
                t.printStackTrace();
                //Ignore
            }
        }
    }

    public void _doStart() {
        isRunning = true;
        thread.start();
    }

    public void _doStop()
    {
        isRunning = false;
        synchronized (queue) {
            queue.notifyAll();
        }
    }

    public void handle(Socket socket)
    {
        handle(new SocketCart(socket, DEFAULT_SLEEP_TIME, null));
    }

    public void handle(SocketCart cart)
    {
        synchronized (queue) {
            queue.add(cart);
            queue.notifyAll();
        }
    }

    public void handle(Socket socket, int keepTime, byte[] error)
    {
        handle(new SocketCart(socket, keepTime, error));
    }

    @Override
    public int compare(SocketCart o1, SocketCart o2) {
        return (int)(o1.timestamp - o2.timestamp);
    }

}
