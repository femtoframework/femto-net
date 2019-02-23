package org.femtoframework.net.nio.valve;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

import org.femtoframework.bean.Destroyable;
import org.femtoframework.io.IOUtil;
import org.femtoframework.net.nio.SimpleChannelContext;
import org.femtoframework.net.nio.SocketChannelCallback;
import org.femtoframework.net.nio.SocketChannelContext;
import org.femtoframework.net.nio.SocketChannelEvent;
import org.femtoframework.net.nio.SocketChannelEventSupport;
import org.femtoframework.net.nio.SocketChannelRegistry;
import org.femtoframework.net.nio.SocketChannelWrapper;
import org.femtoframework.net.socket.SocketContext;
import org.femtoframework.net.socket.SocketContextListener;
import org.femtoframework.util.SystemUtil;
import org.femtoframework.util.queue.LinkedQueue;
import org.femtoframework.util.queue.Queue;
import org.femtoframework.util.thread.ThreadContainer;
import org.femtoframework.util.thread.ThreadController;

/**
 * 线程处理模块
 *
 * @author fengyun
 * @version 1.00 2004-8-6 21:45:26
 */
public class SocketChannelThread
    extends ThreadController
    implements SocketChannelRegistry, Destroyable, SocketChannelEventSupport {
    public static final int DEFAULT_SELECT_TIMEOUT = 100;

    /**
     * 选取超时时间
     */
    private int selectTimeout = DEFAULT_SELECT_TIMEOUT;

    /**
     * 处理请求
     */
    private Queue<SocketChannelContext> queue;

    /**
     * 选取器
     */
    private Selector selector = null;

    /**
     * 请求处理阀门
     */
    private SocketChannelValve valve;

    /**
     * 是否在服务
     */
    private boolean serving = false;

    /**
     * 上一次检查超时时间
     */
    private long lastCheckTime;

    /**
     * 是不是Windows操作系统
     */
    private static boolean isWindows = SystemUtil.isWindows();

    /**
     * 线程池
     */
    private SocketChannelThreadPool pool;

    /**
     * 当前处理的上下文
     */
    private SocketChannelContext currentContext;

    /**
     * 构造
     */
    public SocketChannelThread() {
        this.queue = new LinkedQueue<>();
        this.lastCheckTime = System.currentTimeMillis();
    }

    /**
     * 设置所在的线程容器
     *
     * @param container 线程容器
     */
    public void setContainer(ThreadContainer container) {
        super.setContainer(container);
        this.pool = (SocketChannelThreadPool)container;
    }

    protected void doRun() throws InterruptedException {
        synchronized (this) {
            if (!shouldRun && isRunning()) {
                this.wait();
            }
        }
        if (!isRunning()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Controller stopped");
            }
            return;
        }
        /* Check if should execute a runnable.  */
        try {
            if (shouldRun) {
                doRun0();
            }
        }
        catch (Throwable t) {
            if (logger.isDebugEnabled()) {
                logger.debug("Caught exception executing "
                          + task + ", terminating thread", t);
            }
            setRunning(false);
            shouldRun = false;
            pool.terminate(this);
        }
        finally {
            if (shouldRun) {
                shouldRun = false;
                pool.recycle(this);
            }
        }
    }

    private void doRun0() {
        while (serving) {
            if (!isEmpty()) {
                SocketChannelContext context = queue.poll(100);
                this.currentContext = context;
                try {

                    if (logger.isTraceEnabled()) {
                        logger.trace("Socket Channel Context:" + context + " Action:" + context.getAction());
                    }
                    if (context != null) {
                        int action = context.getAction();
                        SocketContextListener listener = getListener(context);
                        switch (action) {
                            case SocketContext.ACTION_CLOSE:
                                close(context);
                                break;
                            case SocketContext.ACTION_EVENT:
                                SocketChannelEvent event = context.getEvent();
                                if (event != null) {
                                    SocketChannelCallback callback = event.getCallback();
                                    if (callback != null) {
                                        callback.callback(event);
                                    }
                                }
                                context.setAction(SocketContext.ACTION_HANDLING);
                                break;
                            case SocketContext.ACTION_WAITING_EVENT:
                                System.out.println("Current action:" + action + " " + context.getAction());
                                break;
                            case SocketContext.ACTION_HANDLING:
                                //处理中
                            case SocketContext.ACTION_CONNECTED:
                                //第一次连接
                            case SocketContext.ACTION_TIMEOUT:
                                //超时
                            default:
                                if (listener != null) {
                                    SocketChannel channel = context.getChannel();
                                    if (channel instanceof SocketChannelWrapper) {
                                        //对于SSLSocketChannel无法读取所有Buffer数据的问题修复
                                        SocketChannelWrapper wrapper = (SocketChannelWrapper)channel;
                                        SelectionKey key = context.getSelectionKey();
                                        boolean isRead = false;
                                        boolean first = true;
                                        while (true) {
                                            listener.handleEvent(action, context);
                                            action = context.getAction();
                                            if (first) {
                                                isRead = key != null && key.isValid() && key.isReadable();
                                                first = false;
                                            }
                                            //修正 POPDS中RETR重复输出的问题
                                            if (context.getOperationSet() != SocketChannelContext.OP_READ) {
                                                break;
                                            }
                                            if (action != SocketContext.ACTION_HANDLING || !isRead ||
                                                !wrapper.hasReadable()) {
                                                break;
                                            }
                                        }
                                    }
                                    else {
                                        listener.handleEvent(action, context);
                                    }
                                }
                        }
                        action = context.getAction();
                        if (action == SocketContext.ACTION_SASL_COMPLETED ||
                            action == SocketContext.ACTION_SASL_FAILED) {
                            //重新进入到侦听者中
                            if (listener != null) {
                                listener.handleEvent(action, context);
                            }
                        }
                        if (action == SocketContext.ACTION_CLOSED) {
                            //已经关闭
                            doUnregister(context);
                        }
                        else if (action == SocketContext.ACTION_HANDLING || action == SocketContext.ACTION_WAITING_EVENT
                                 || action == SocketContext.ACTION_EVENT) {
                            //更新时间戳
                            context.updateLastAccess();
                        }

                        this.currentContext = null;
                    }
                }
                catch (CancelledKeyException cke) {
                    logger.debug("CancelledKeyException", cke);
//                    close(context);
                    serving = false;
                    break;
                }
            }
            else {
                Set keys = selector.keys();
                if (keys.isEmpty()) {
                    //尝试真正关闭所有的SocketChannel
                    try {
                        selector.selectNow();
                    }
                    catch (IOException e) {
                    }
                    serving = false;
                    break;
                }
                else {
                    try {
                        select();
                    }
                    catch (ClosedSelectorException cse) {
                        logger.debug("ClosedSelectorException", cse);
                        serving = false;
                        break;
                    }
                    catch (CancelledKeyException cke) {
                        logger.debug("CancelledKeyException", cke);
                        serving = false;
                        break;
                    }
                    catch (RuntimeException re) {
                        logger.debug("RuntimeException", re);
                        serving = false;
                        break;
                    }
                }
            }
        }
    }

    /**
     * 返回SocketContext侦听者
     *
     * @return SocketContext侦听者
     */
    private SocketContextListener getListener(SocketContext context) {
        return context.getListener();
    }

    private int selectWithWait() throws IOException {
        if (isWindows) {
            return selectWithWaitWindows();
        }
        else {
            return selectWithWaitUnix();
        }
    }

    private int selectWithWaitUnix() throws IOException {
        int selected;
        if (selector.keys().isEmpty()) {
            selected = selector.selectNow();
        }
        else {
            selected = selector.select(selectTimeout);
        }
        return selected;
    }

    private int selectWithWaitWindows() throws IOException {
        long start = System.currentTimeMillis();
        int selected;
        if (selector.keys().isEmpty()) {
            selected = selector.selectNow();
        }
        else {
            selected = selector.select(selectTimeout);
            if (selected <= 0) {
                long timeout = System.currentTimeMillis() - start;
                if (timeout < selectTimeout) {
                    synchronized (this) {
                        try {
                            this.wait(selectTimeout - timeout);
                        }
                        catch (InterruptedException e) {
                        }
                    }
                }
            }
        }

        return selected;
    }

    private void select() {
        int selectedCount;
        try {
            selectedCount = selectWithWait();
        }
        catch (IOException ioe) {
            //IOException
            logger.warn("Selector io error", ioe);
            openNewSelector();
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (selectedCount > 0) {
            //获取已经有数据的上下文，调度该任务
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            for (SelectionKey readyKey : readyKeys) {
                queue.offer((SocketChannelContext)readyKey.attachment());
            }
            readyKeys.clear();
        }
        //检查超时的SocketChannel
        if (currentTime - lastCheckTime > (selectTimeout << 1)) {
            Set<SelectionKey> keys = selector.keys();
            if (!keys.isEmpty()) {
                for (SelectionKey key : keys) {
                    SocketChannelContext context = (SocketChannelContext)key.attachment();
                    if (context.isTimeout()) {
                        context.setAction(SocketContext.ACTION_TIMEOUT);
                        queue.offer(context);
                    }
                }
            }

            lastCheckTime = currentTime;
        }
    }

    private void close(SocketChannelContext context) {
        unregister(context);
        context.close();
    }

    /**
     * 打开一个新的选取器
     */
    private void openNewSelector() {
        Selector oldSelector = selector;
        Selector newSelector = pool.openSelector();
        if (newSelector == null) {
            throw new ClosedSelectorException();
        }

        selector = newSelector;

        Set<SelectionKey> keys = oldSelector.keys();
        SocketChannelContext context;
        for (SelectionKey key : keys) {
            context = (SocketChannelContext)key.attachment();
            key.cancel();
            register(context);
        }

        IOUtil.close(oldSelector);
    }

    /**
     * 是否已经没有新的任务
     *
     * @return 是否已经没有新的任务
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * 调度上下文
     *
     * @param context SocketChannel上下文
     */
    public void schedule(SocketChannelContext context) {
        if (context instanceof SimpleChannelContext) {
            ((SimpleChannelContext)context).setRegistry(this);
        }

        queue.offer(context);
        this.serving = true;
        this.shouldRun = true;
        selector.wakeup();
        synchronized (this) {
            this.notify();
        }
    }

    /**
     * 注册SocketChannelContext
     *
     * @param context 上下文
     */
    public void register(SocketChannelContext context) {
        if (selector != null) {
            try {
                SocketChannel channel = context.getChannel();
                if (channel instanceof SocketChannelWrapper) {
                    channel = ((SocketChannelWrapper) channel).getChannel();
                }
                SelectionKey key = channel.register(selector, context.getOperationSet(), context);
                context.setSelectionKey(key);
            } catch (ClosedChannelException cce) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Socket channel closed", cce);
                }
                //增加状态码和状态消息的传递
                context.close(SocketContext.SC_INTERNAL_SERVER_ERROR, cce.getMessage());
            }
        }
    }

    /**
     * 取消注册SocketChannelContext
     *
     * @param context 上下文
     */
    public void unregister(SocketChannelContext context) {
        doUnregister(context);

        context.onClosed();
    }

    protected void doUnregister(SocketChannelContext context) {
        SocketChannel channel = context.getChannel();
        if (channel != null) {
            SocketChannel wrapper = null;
            if (channel instanceof SocketChannelWrapper) {
                wrapper = channel;
                channel = ((SocketChannelWrapper)channel).getChannel();
            }
            if (selector != null) {
                SelectionKey key = channel.keyFor(selector);
                if (key != null) {
                    key.cancel();
                }
            }
            IOUtil.close(wrapper);
        }
        else {
            SelectionKey key = context.getSelectionKey();
            if (key != null) {
                key.cancel();
            }
        }
    }

    /**
     * 返回选取器
     *
     * @return 选取器
     */
    public Selector getSelector() {
        return selector;
    }

    /**
     * 返回等待的任务总数
     *
     * @return 等待的任务总数
     */
    public synchronized int getWaitingCount() {
        return selector.keys().size();
    }

    public void _doDestroy() {
        super._doDestroy();
        if (selector != null) {
            IOUtil.close(selector);
            selector = null;
        }
        if (queue != null) {
            queue = null;
        }
    }

    /**
     * 返回选取超时时间
     *
     * @return 选取超时时间
     */
    public int getSelectTimeout() {
        return selectTimeout;
    }

    /**
     * 设置选取超时时间
     *
     * @param selectTimeout
     */
    public void setSelectTimeout(int selectTimeout) {
        if (selectTimeout < 100) {
            throw new IllegalArgumentException("Invalid select timeout, too small:" + selectTimeout);
        }
        this.selectTimeout = selectTimeout;
    }

    /**
     * 设置选取器
     *
     * @param selector 选取器
     */
    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    /**
     * 返回SocketChannelValve
     *
     * @return SocketChannelValve
     */
    public SocketChannelValve getValve() {
        return valve;
    }

    /**
     * 设置SocketChannelValve
     *
     * @param valve SocketChannelValve
     */
    public void setValve(SocketChannelValve valve) {
        this.valve = valve;
    }

    /**
     * 添加事件
     *
     * @param event 事件
     */
    public void fireEvent(SocketChannelEvent event) {
        SocketChannelContext context = event.getContext();
        if (context == null) {
            context = currentContext;
        }

        if (context instanceof SimpleChannelContext) {
            context.setAction(SocketChannelContext.ACTION_EVENT);
            ((SimpleChannelContext)context).setEvent(event);
        }

        queue.offer(context);
    }

    /**
     * 返回当前处理的SocketChannelContext
     *
     * @return 当前处理的SocketChannelContext
     */
    public SocketChannelContext getCurrentContext() {
        return currentContext;
    }
}
