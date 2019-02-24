package org.femtoframework.net.socket;

import org.femtoframework.pattern.EventListeners;

/**
 * Socket Valve Listeners
 *
 * @author fengyun
 * @version 1.00 Oct 24, 2003 5:29:18 PM
 */
public class SocketValveListeners extends EventListeners<SocketValveListener> implements SocketValveListener
{

    public SocketValveListeners()
    {
    }

    public SocketValveListeners(SocketValveListener listener)
    {
        super(listener);
    }

    public void handleEvent(SocketValveEvent event)
    {
        for (SocketValveListener listener : getListeners()) {
            listener.handleEvent(event);
        }
    }
}
