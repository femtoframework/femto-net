package org.femtoframework.net.socket;

/**
 * SocketValve Chain
 *
 * @author fengyun
 * @version 1.00 Oct 25, 2003 11:06:47 AM
 */
public interface SocketChain
{
    /**
     * Handle Next Context
     *
     * @param context Socket Context
     */
    void handleNext(SocketContext context);
}
