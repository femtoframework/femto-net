package org.femtoframework.net.socket;

import org.femtoframework.pattern.EventListeners;

/**
 * SocketContextListeners
 *
 * @author fengyun
 * @version 1.00 2004-8-5 22:41:24
 */
public class SocketContextListeners extends EventListeners<SocketContextListener> implements SocketContextListener
{
    /**
     *
     */
    public SocketContextListeners()
    {
    }

    /**
     * Constructs by one listener
     *
     * @param listener SocketContextListener
     */
    public SocketContextListeners(SocketContextListener listener)
    {
        super(listener);
    }

    /**
     * Handle event
     *
     * @param action Action
     * @param context Socket Context
     */
    public void handleEvent(int action, SocketContext context)
    {
        for (SocketContextListener listener : getListeners()) {
            listener.handleEvent(action, context);
        }
    }

}
