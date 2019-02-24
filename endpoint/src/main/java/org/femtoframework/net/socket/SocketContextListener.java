package org.femtoframework.net.socket;

import java.util.EventListener;

/**
 * SocketContextListener
 *
 * @author fengyun
 * @version 1.00 2004-8-5 22:40:03
 */
public interface SocketContextListener extends EventListener
{
    /**
     * On action
     *
     * @param action Action
     * @param context Socket Context
     */
    void handleEvent(int action, SocketContext context);
}
