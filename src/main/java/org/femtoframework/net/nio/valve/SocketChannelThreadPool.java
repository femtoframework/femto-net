package org.femtoframework.net.nio.valve;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.femtoframework.net.nio.SocketChannelContext;
import org.femtoframework.util.thread.ThreadController;
import org.femtoframework.util.thread.ThreadPool;

/**
 * Thread Pool for NIO
 *
 * @author fengyun
 * @version 1.00 Apr 16, 2004 2:24:39 PM
 */
public class SocketChannelThreadPool extends ThreadPool
{
    /**
     * 尝试打开选取器的次数
     */
    private static final int MAX_SELECTOR_OPEN_TIMES = 10;

    /**
     * Thread列表，记录所有的存在Thread
     */
    private List<SocketChannelThread> threadList;


    /**
     * 参数打开选取器尝试的次数
     */
    private int maxSelectorOpenTimes = MAX_SELECTOR_OPEN_TIMES;

    public synchronized void _doStart()
    {
        threadList = new ArrayList<SocketChannelThread>(maxThreads);
        super._doStart();
    }

    protected ThreadController newThread()
    {
        SocketChannelThread thread = new SocketChannelThread();
        if (selectTimeout > 0) {
            thread.setSelectTimeout(selectTimeout);
        }
        synchronized (threadList) {
            threadList.add(thread);
        }
        return thread;
    }

    public List<SocketChannelThread> getThreadList() {
        return threadList;
    }

    private int selectTimeout = -1;

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
     * 对该控制器进行必要的配置，并且进行初始化和启动
     *
     * @param controller 线程控制器
     */
    public void start(ThreadController controller)
    {
        super.start(controller);
        SocketChannelThread thread = (SocketChannelThread)controller;
        Selector selector = openSelector();
        thread.setSelector(selector);
    }


    protected void destroyIt(ThreadController controller)
    {
        synchronized (threadList) {
            threadList.remove(controller);
        }
        super.destroyIt(controller);
    }

    protected ThreadController allocateNoThread()
    {
        //仍然没有，从ArrayList中任选一个
        return select(threadList);
    }

    /**
     * Executes a given Runnable on a thread in the pool, block if needed.
     */
    public boolean execute(SocketChannelContext context)
    {
        if (context == null) {
            throw new IllegalArgumentException("Null context");
//            throw new NullPointerException();
        }
        ThreadController controller = allocate();
        ((SocketChannelThread)controller).schedule(context);
        return true;
    }

    /**
     * 返回打开Selector的尝试次数
     *
     * @return 打开Selector的尝试次数
     */
    public int getMaxSelectorOpenTimes()
    {
        return maxSelectorOpenTimes;
    }

    /**
     * 设置打开Selector的尝试次数
     *
     * @param maxSelectorOpenTimes
     */
    public void setMaxSelectorOpenTimes(int maxSelectorOpenTimes)
    {
        this.maxSelectorOpenTimes = maxSelectorOpenTimes;
    }

    private static Random random = new Random();

    protected SocketChannelThread select(List<SocketChannelThread> list)
    {
        synchronized (list) {
            return list.get(random.nextInt(list.size()));
        }
    }

    protected Selector openSelector()
    {
        int count = 0;
        Selector selector = null;
        while (count < maxSelectorOpenTimes) {
            try {
                selector = Selector.open();
                if (selector != null) {
                    break;
                }
                count++;
            }
            catch (IOException ioe) {
                logger.warn("Can't open selector", ioe);
                try {
                    Thread.sleep(1000);
                }
                catch(Exception ex) {
                    //
                }
            }
        }
        return selector;
    }
}
