package org.femtoframework.net.socket;

import org.femtoframework.bean.NamedBean;

/**
 * SocketValve
 *
 * @author fengyun
 * @version 1.00 Aug 8, 2003 6:47:24 PM
 */
public interface SocketValve extends NamedBean
{
    /**
     * Handle Socket
     *
     * @param context Socket Context
     * @param chain Chain
     */
    void handle(SocketContext context, SocketChain chain);
}

