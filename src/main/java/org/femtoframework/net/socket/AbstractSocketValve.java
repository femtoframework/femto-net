package org.femtoframework.net.socket;

import java.util.List;

/**
 * SocketValve
 *
 * @author fengyun
 * @version 1.00 Aug 8, 2003 6:54:33 PM
 */
public abstract class AbstractSocketValve
    implements SocketValve
{
    private String name;

    protected SocketValveListener valveListener;

    protected SocketContextListener contextListener;

    public void addValveListener(SocketValveListener listener)
    {
        if (listener == null) {
            return;
        }

        SocketValveListener old = this.valveListener;
        if (old == null) {
            this.valveListener = listener;
        }
        else if (old instanceof SocketValveListeners) {
            ((SocketValveListeners)old).addListener(listener);
        }
        else {
            SocketValveListeners listeners = new SocketValveListeners(old);
            listeners.addListener(listener);
            this.valveListener = listeners;
        }
    }

    public void removeValveListener(SocketValveListener listener)
    {
        if (listener == null) {
            return;
        }

        SocketValveListener old = this.valveListener;
        if (old == listener) {
            this.valveListener = null;
        }
        else if (old instanceof SocketValveListeners) {
            ((SocketValveListeners)old).removeListener(listener);
        }
    }

    public SocketValveListener getValveListener()
    {
        return valveListener;
    }

    public void fireValveEvent(int action, SocketContext context)
    {
        fireValveEvent(action, context, null);
    }

    public void fireValveEvent(int action, SocketContext context, Object[] arguments)
    {
        if (valveListener != null) {
            valveListener.handleEvent(new SocketValveEvent(this, context, action, arguments));
        }
    }

    public void addContextListener(SocketContextListener listener)
    {
        if (listener == null) {
            return;
        }

        SocketContextListener old = this.contextListener;
        if (old == null) {
            this.contextListener = listener;
        }
        else if (old instanceof SocketContextListeners) {
            if (listener instanceof SocketContextListeners) {
                SocketContextListeners listeners = (SocketContextListeners)listener;
                List<SocketContextListener> array = listeners.getListeners();
                SocketContextListeners oldListeners = ((SocketContextListeners)old);
                for (SocketContextListener l : array) {
                    oldListeners.addListener(l);
                }
            }
            else {
                ((SocketContextListeners)old).addListener(listener);
            }
        }
        else {
            SocketContextListeners listeners = new SocketContextListeners(old);
            listeners.addListener(listener);
            this.contextListener = listeners;
        }
    }

    public void removeContextListener(SocketContextListener listener)
    {
        if (listener == null) {
            return;
        }

        SocketContextListener old = this.contextListener;
        if (old == listener) {
            this.contextListener = null;
        }
        else if (old instanceof SocketContextListeners) {
            ((SocketContextListeners)old).removeListener(listener);
        }
    }

    public SocketContextListener getContextListener()
    {
        return contextListener;
    }

    public void setContextListener(SocketContextListener contextListener)
    {
        this.contextListener = contextListener;
    }

    public void fireContextEvent(int action, SocketContext context)
    {
        if (contextListener != null) {
            contextListener.handleEvent(action, context);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
