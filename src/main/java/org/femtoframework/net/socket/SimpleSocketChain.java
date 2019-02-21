package org.femtoframework.net.socket;

import java.util.List;

/**
 * SimpleSocketChain
 *
 * @author fengyun
 * @version 1.00 Oct 25, 2003 11:08:24 AM
 */
public class SimpleSocketChain implements SocketChain
{
    private List<SocketValve> valves = null;

    private int index = 0;

    private int size;

    protected SimpleSocketChain(List<SocketValve> valves)
    {
        this.valves = valves;
        this.size = valves.size();
    }

    public void handleNext(SocketContext context)
    {
        if (index < size) {
            SocketValve valve = valves.get(index++);
            valve.handle(context, this);
        }
    }
}
