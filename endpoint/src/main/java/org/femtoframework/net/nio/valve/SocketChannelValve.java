package org.femtoframework.net.nio.valve;

import java.util.concurrent.Executor;

import org.femtoframework.net.nio.SocketChannelContext;
import org.femtoframework.net.socket.AbstractSocketValve;
import org.femtoframework.net.socket.SocketChain;
import org.femtoframework.net.socket.SocketContext;

/**
 * 抽象的非阻塞Socket阀门，用于实现有限线程处理众多Socket
 *
 * @author fengyun
 * @version 1.00 2004-8-6 21:33:35
 */
public class SocketChannelValve extends AbstractSocketValve
{

    /**
     * 线程池
     */
    private SocketChannelThreadPool executor;

    /**
     * 处理连接
     *
     * @param context 连接上下文
     * @param chain   阀门控制链
     */
    public void handle(SocketContext context, SocketChain chain)
    {
        SocketChannelContext ssc = (SocketChannelContext)context;
        doHandle(ssc, chain);
    }

    /**
     * 处理连接
     *
     * @param context 连接上下文
     * @param chain   阀门控制链
     */
    protected void doHandle(SocketChannelContext context, SocketChain chain)
    {
        schedule(context);
        chain.handleNext(context);
    }

    /**
     * 调度Context
     *
     * @param context 上下文
     */
    protected void schedule(SocketChannelContext context)
    {
        //将阀门中的事件侦听者添加给Context, 添加侦听者
        context.addListener(getContextListener());
        executor.execute(context);
    }


    /**
     * 设置执行器
     *
     * @param executor 执行器
     */
    public void setExecutor(Executor executor)
    {
        if (!(executor instanceof SocketChannelThreadPool)) {
            throw new IllegalArgumentException(
                "Please add a 'org.femtoframework.net.nio.valve.SocketChannelThreadPool' thread pool.");
        }
        this.executor = (SocketChannelThreadPool)executor;
    }

    /**
     * 返回线程执行器
     *
     * @return 线程执行器
     */
    public Executor getExecutor()
    {
        return executor;
    }
}
