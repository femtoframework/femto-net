package org.femtoframework.net.socket.bifurcation;


import org.femtoframework.bean.AbstractLifecycle;
import org.femtoframework.bean.exception.InitializeException;
import org.femtoframework.net.socket.SocketHandler;

/**
 * BifurcatedSocketHandler
 *
 * @author fengyun
 * @version 1.00 2005-3-10 12:05:55
 */
public abstract class BifurcatedSocketHandler
    extends AbstractLifecycle
    implements SocketHandler, Bifurcated
{
    private int bifurcation;

    public void _doInit()
    {
        if (bifurcation == 0) {
            throw new InitializeException("No scheme set");
        }
    }

    @Override
    public int getBifurcation() {
        return bifurcation;
    }

    public void setBifurcation(int bifurcation) {
        this.bifurcation = bifurcation;
    }
}
