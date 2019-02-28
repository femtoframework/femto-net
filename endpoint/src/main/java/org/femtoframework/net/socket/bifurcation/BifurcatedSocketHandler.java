package org.femtoframework.net.socket.bifurcation;


import org.femtoframework.bean.BeanPhase;
import org.femtoframework.bean.LifecycleMBean;
import org.femtoframework.bean.exception.InitializeException;
import org.femtoframework.net.socket.SocketHandler;

/**
 * BifurcatedSocketHandler
 *
 * @author fengyun
 * @version 1.00 2005-3-10 12:05:55
 */
public abstract class BifurcatedSocketHandler
    implements SocketHandler, Bifurcated, LifecycleMBean
{
    private int bifurcation;
    private BeanPhase beanPhase = BeanPhase.DISABLED;

    /**
     * Implement method of getPhase
     *
     * @return BeanPhase
     */
    public BeanPhase _doGetPhase() {
        return beanPhase;
    }

    /**
     * Phase setter for internal
     *
     * @param phase BeanPhase
     */
    public void _doSetPhase(BeanPhase phase) {
        this.beanPhase = phase;
    }


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
