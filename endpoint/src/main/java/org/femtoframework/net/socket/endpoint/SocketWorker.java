package org.femtoframework.net.socket.endpoint;

import org.femtoframework.io.IOUtil;
import org.slf4j.Logger;

import java.net.Socket;

/**
 * SocketWorker
 *
 * @author fengyun
 * @version 1.00 2005-2-28 0:34:39
 */
class SocketWorker implements Runnable
{
    /* This is not a normal Runnable - it gets attached to an existing
       thread, runs and when run() ends - the thread keeps running.

       It's better to keep the name ThreadPoolRunnable - avoid confusion.
       We also want to use per/thread data and avoid sync wherever possible.
    */
    PoolEndpoint endpoint;

    private Logger log;

    public SocketWorker(PoolEndpoint endpoint)
    {
        this.endpoint = endpoint;
        this.log = endpoint.getLogger();
    }

    public void run()
    {
        // Create per-thread cache
        Socket s = null;
        while (endpoint.isRunning()) {
            // Loop if endpoint is paused

            while (endpoint.isPaused()) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    // Ignore
                }
            }

            try {
                s = endpoint.acceptSocket();
            }
            catch (Throwable t) {
                log.debug("Exception in acceptSocket", t);
            }
            if (endpoint.checkSocket(s)) {
                IOUtil.close(s);
                s = null;
                continue;
            }
            // Continue accepting on another thread...
            try {
                endpoint.execute(this);
            }
            catch (IllegalStateException ise) {
                IOUtil.close(s);
                s = null;
                continue;
            }

            try {
                // 1: Set socket options: timeout, linger, etc
                endpoint.setSocketOptions(s);

                // 2: SSL handshake
                if (endpoint.getServerSocketFactory() != null) {
                    //TODO
                    //endpoint.getServerSocketFactory().handshake(newSocket);
                }

                endpoint.handle(s);
            }
            catch (Throwable t) {
                log.error("Unexpected error", t);
                // Try to close the socket
                IOUtil.close(s);
            }
            break;
        }
    }
}

