package org.femtoframework.net.socket;

import java.util.EventObject;

/**
 * SocketContext Event
 *
 * @author fengyun
 * @version 1.00 2005-1-6 15:51:49
 */
public class SocketValveEvent
    extends EventObject
{
    private SocketContext context;
    private SocketValve valve;
    private Object[] arguments;
    private int action;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @param context SocketContext
     * @param action Action
     * @param arguments Any Arguments attached to this event
     */
    public SocketValveEvent(SocketValve source,
                            SocketContext context,
                            int action,
                            Object[] arguments)
    {
        super(source);
        this.action = action;
        this.valve = source;
        this.context = context;
        this.arguments = arguments;
    }

    public SocketContext getContext()
    {
        return context;
    }

    public Object[] getArguments()
    {
        return arguments;
    }

    public SocketValve getValve()
    {
        return valve;
    }

    public int getAction()
    {
        return action;
    }
}
