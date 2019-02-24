package org.femtoframework.net.socket.bifurcation;


import org.femtoframework.net.socket.SocketHandler;
import org.femtoframework.net.socket.endpoint.PoolEndpoint;

/**
 * 能够根据第一个byte自动分发的Endpoint
 *
 * @author fengyun
 * @version 1.00 2005-3-10 11:12:36
 */
public class BifurcatedEndpoint extends PoolEndpoint
{
    private BifurcationDispatchHandler dispatcher = new BifurcationDispatchHandler();

    /**
     * 设置SocketHandler工厂
     *
     * @param factory Factory工厂
     */
    public void setSocketHandlerFactory(BifurcationSocketHandlerFactory factory)
    {
        this.dispatcher.setSocketHandlerFactory(factory);
    }

    /**
     * 返回SocketHandlerFactory工厂
     *
     * @return
     */
    public BifurcationSocketHandlerFactory getSocketHandlerFactory()
    {
        return dispatcher.getSocketHandlerFactory();
    }


    /**
     * 根据bifurcation返回相应的处理器
     *
     * @param bifurcation Bifurcation
     */
    public BifurcatedSocketHandler getHandler(int bifurcation) {
        return dispatcher.getSocketHandlerFactory().getHandler(bifurcation);
    }

    /**
     * 添加处理器
     *
     * @param handler Socket处理器
     */
    public void addHandler(BifurcatedSocketHandler handler) {
        dispatcher.getSocketHandlerFactory().addHandler(handler);
    }

    /**
     * 初始化实现
     */
    public void _doInitialize()
    {
        super._doInitialize();

        this.handler = dispatcher;
    }

    /**
     * 设置处理器
     *
     * @param handler 无法设置处理器（只能添加）
     */
    public void setHandler(SocketHandler handler)
    {
        throw new IllegalStateException("Can't setHandler");
    }
}
