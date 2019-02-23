package org.femtoframework.net.nio;

import org.femtoframework.io.IOUtil;
import org.slf4j.Logger;

import java.nio.channels.SocketChannel;

/**
 * SocketChannelWorker
 *
 * @author fengyun
 * @version 1.00 2004-8-6 16:26:41
 */
class SocketChannelWorker implements Runnable
{
    /* This is not a normal Runnable - it gets attached to an existing
       thread, runs and when run() ends - the thread keeps running.

       It's better to keep the name ThreadPoolRunnable - avoid confusion.
       We also want to use per/thread data and avoid sync wherever possible.
    */
    SocketChannelEndpoint endpoint;

    private Logger log;

    /**
     * SocketChannelWorker
     *
     * @param endpoint
     */
    public SocketChannelWorker(SocketChannelEndpoint endpoint)
    {
        this.endpoint = endpoint;
        this.log = endpoint.getLogger();
    }

    public void run()
    {
        // Create per-thread cache
        SocketChannel channel = null;
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
                channel = endpoint.acceptSocket();
            }
            catch (Throwable t) {
                log.debug("Exception in acceptSocket", t);
            }
            if (endpoint.checkSocket(channel)) {
                IOUtil.close(channel);
                channel = null;
                continue;
            }
            // Continue accepting on another thread...
            endpoint.execute(this);

            try {
                channel.configureBlocking(false);
                endpoint.getHandler().handle(channel);
            }
            catch (Throwable t) {
                log.error("Unexpected error", t);
                // Try to close the channel
                IOUtil.close(channel);
            }
            break;
        }
    }
}