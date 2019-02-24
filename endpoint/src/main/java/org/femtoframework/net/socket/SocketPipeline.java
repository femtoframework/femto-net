package org.femtoframework.net.socket;


import org.femtoframework.bean.NamedBean;

import java.util.List;

/**
 * Socket Pipeline
 *
 * @author fengyun
 * @version 1.00 Aug 8, 2003 6:47:36 PM
 */
public interface SocketPipeline extends SocketHandler, NamedBean
{
    /**
     * Add SocketValve
     *
     * @param valve SocketValve
     */
    default void addValve(SocketValve valve) {
        getValves().add(valve);
    }

    /**
     * Remove SocketValve
     *
     * @param valve SocketValve
     */
    default void removeValve(SocketValve valve) {
        getValves().remove(valve);
    }

    /**
     * Returns all socket valves
     *
     * @return Socket Valves
     */
    List<SocketValve> getValves();

    /**
     * Set all socket valves
     *
     * @param valves Socket Valves
     */
    void setValves(List<SocketValve> valves);

    /**
     * Set finally valve, it will be invoked for what ever reason
     *
     * @param valve Socket Valve
     */
    void setFinallyValve(SocketValve valve);

    /**
     * Returns finally valve
     *
     * @return FinallyValve
     */
    SocketValve getFinallyValve();
}
