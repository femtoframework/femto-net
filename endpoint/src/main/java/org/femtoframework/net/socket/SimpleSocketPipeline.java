package org.femtoframework.net.socket;

import org.femtoframework.bean.*;
import org.femtoframework.net.socket.close.CloseHandler;
import org.femtoframework.net.socket.close.DefaultCloseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple Socket Pipeline
 *
 * @author fengyun
 * @version 1.00 Aug 8, 2003 6:55:34 PM
 */
public class SimpleSocketPipeline
    implements SocketPipeline, SocketEndpointAware, LifecycleMBean
{
    private String name;

    /**
     * Socket Valve
     */
    private List<SocketValve> list = new ArrayList<>(4);

    /**
     * Finally Valve
     */
    private SocketValve finallyValve = null;


    /**
     * SocketEndpoint
     */
    private SocketEndpoint endpoint;

    private CloseHandler closeHandler = new DefaultCloseHandler();

    /**
     * Returns all socket valves
     *
     * @return Socket Valves
     */
    public List<SocketValve> getValves() {
        return list;
    }

    /**
     * Set all socket valves
     *
     * @param valves Socket Valves
     */
    public void setValves(List<SocketValve> valves) {
        this.list = valves;
    }

    /**
     * 处理Socket
     *
     * @param socket Socket
     */
    public void handle(Socket socket)
    {
        SocketContext context = getSocketContext(socket);
        if (context != null) {
            handle(context);
            if (!context.isFinished()) {
                context.finish();
            }
        }
    }

    /**
     * 处理SocketContext
     *
     * @param context Socket上下文
     */
    public void handle(SocketContext context)
    {
        SimpleSocketChain chain = new SimpleSocketChain(list);

        try {
            chain.handleNext(context);
        }
        catch (RuntimeException re) {
            handleError(re);
        }

        if (finallyValve != null) {
            try {
                finallyValve.handle(context, chain);
            }
            catch (RuntimeException re) {
                handleError(re);
            }
        }
    }

    protected Logger log = LoggerFactory.getLogger(getClass());

    public Logger getLogger()
    {
        return log;
    }

    protected void handleError(Throwable t)
    {
        log.warn("Error", t);
    }

    protected SocketContext getSocketContext(Socket socket)
    {
        SimpleSocketContext socketContext = new SimpleSocketContext(socket);
        socketContext.setCloseHandler(closeHandler);
        return socketContext;
    }

    /**
     * Set finally valve, it will be invoked for what ever reason
     *
     * @param valve Socket Valve
     */
    public void setFinallyValve(SocketValve valve) {
        this.finallyValve = valve;
    }

    /**
     * Returns finally valve
     *
     * @return FinallyValve
     */
    public SocketValve getFinallyValve() {
        return finallyValve;
    }

    /**
     * 设置SocketEndpoint
     *
     * @param endpoint SocketEndpoint
     */
    public void setEndpoint(SocketEndpoint endpoint)
    {
        this.endpoint = endpoint;
    }

    /**
     * 返回所在的Endpoint
     */
    public SocketEndpoint getEndpoint()
    {
        return endpoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CloseHandler getCloseHandler() {
        return closeHandler;
    }

    public void setCloseHandler(CloseHandler closeHandler) {
        this.closeHandler = closeHandler;
    }

    private BeanPhase phase = BeanPhase.DISABLED;

    /**
     * Implement method of getPhase
     *
     * @return BeanPhase
     */
    @Override
    public BeanPhase _doGetPhase() {
        return phase;
    }

    /**
     * Phase setter for internal
     *
     * @param phase BeanPhase
     */
    @Override
    public void _doSetPhase(BeanPhase phase) {
        this.phase = phase;
    }


    /**
     * Initialize internally
     */
    public void _doInitialize() {
        if (closeHandler instanceof Initializable) {
            ((Initializable)closeHandler).initialize();
        }
    }

    /**
     * Start internally
     */
    public void _doStart() {
        if (closeHandler instanceof Startable) {
            ((Startable)closeHandler).start();
        }
    }

    /**
     * Stop internally
     */
    public void _doStop() {
        if (closeHandler instanceof Stoppable) {
            ((Stoppable)closeHandler).stop();
        }
    }

    /**
     * Destroy internally
     */
    public void _doDestroy() {
        if (closeHandler instanceof Destroyable) {
            ((Destroyable)closeHandler).destroy();
        }
    }
}
