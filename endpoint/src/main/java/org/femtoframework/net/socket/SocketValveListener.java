package org.femtoframework.net.socket;

import java.util.EventListener;

/**
 * Socket Valve Listener
 *
 * @author fengyun
 * @version 1.00 Oct 24, 2003 5:16:03 PM
 */
public interface SocketValveListener extends EventListener
{
    void handleEvent(SocketValveEvent event);
}
