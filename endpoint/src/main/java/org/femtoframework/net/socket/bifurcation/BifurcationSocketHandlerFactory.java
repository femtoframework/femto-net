package org.femtoframework.net.socket.bifurcation;

/**
 * SocketHandler工厂
 *
 * @author fengyun
 * @version 1.00 2005-3-14 20:26:44
 */
public interface BifurcationSocketHandlerFactory
{
    /**
     * 根据bifurcation返回相应的处理器
     *
     * @param bifurcation Bifurcation
     */
    BifurcatedSocketHandler getHandler(int bifurcation);

    /**
     * 添加处理器
     *
     * @param handler Socket处理器
     */
    void addHandler(BifurcatedSocketHandler handler);
}
