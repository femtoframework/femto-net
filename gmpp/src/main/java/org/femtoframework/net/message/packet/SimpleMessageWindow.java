package org.femtoframework.net.message.packet;

import org.femtoframework.bean.AbstractLifecycle;
import org.femtoframework.bean.BeanPhase;
import org.femtoframework.bean.Destroyable;
import org.femtoframework.net.message.MessageWindow;
import org.femtoframework.net.message.Timeoutable;
import org.femtoframework.util.thread.ExecutorUtil;
import org.femtoframework.util.thread.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 消息窗口实现
 *
 * @author fengyun
 * @version 1.00 2005-5-21 21:08:04
 */
public class SimpleMessageWindow<M> extends AbstractLifecycle
    implements MessageWindow<M>, Runnable
{
    public static final int DEFAULT_MAX_SIZE = 512;

    private static ScheduleService scheduler = ExecutorUtil.newSingleThreadScheduler();

    private int maxCount = DEFAULT_MAX_SIZE;

    private Map<Integer, M> list = new HashMap<Integer, M>(32);

    private Lock lock = new ReentrantLock();

    private transient ScheduledFuture future;

    public SimpleMessageWindow()
    {
    }

    /**
     * 实际启动实现
     */
    public void _doStart()
    {
        this.future = scheduler.scheduleAtFixedRate(this, 1000L, 1000L);
    }

    /**
     * 实际停止实现
     */
    public void _doStop()
    {
        if (future != null) {
            future.cancel(false);
        }
    }

    /**
     * 实际销毁实现
     */
    public void _doDestroy()
    {
        if (list != null) {
            lock.lock();
            try {
                for (M message : list.values()) {
                    if (message instanceof Destroyable) {
                        ((Destroyable)message).destroy();
                    }
                }
                list.clear();
                list = null;
            }
            finally {
                lock.unlock();
            }
        }
    }

    /**
     * 添加消息
     *
     * @param message 消息
     * @return 是否添加成功，如果消息已满，那么返回<code>false</code>
     */
    public boolean addMessage(int msgId, M message)
    {
        if (list.size() < maxCount) {
            lock.lock();
            try {
                list.put(msgId, message);
            }
            finally {
                lock.unlock();
            }
            return true;
        }

        return false;
    }

    /**
     * 根据消息标识删除消息
     *
     * @param id 消息标识
     */
    public M removeMessage(int id)
    {
        lock.lock();
        try {
            return list.remove(id);
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 返回当前窗口中的消息总数
     *
     * @return 消息总数
     */
    public int getMessageCount()
    {
        return list.size();
    }

    /**
     * 返回最大消息数目
     *
     * @return 最大消息数目
     */
    public int getMaxCount()
    {
        return maxCount;
    }

    /**
     * 设置最大消息数目
     *
     * @param maxCount 消息数目
     */
    public void setMaxCount(int maxCount)
    {
        this.maxCount = maxCount;
    }

    /**
     * 根据消息标识返回消息
     *
     * @param id 消息标识
     */
    public M getMessage(int id)
    {
        return list.get(id);
    }


    private static Logger logger = LoggerFactory.getLogger(SimpleMessageWindow.class);

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run()
    {
        if (getBeanPhase().isAfterOrCurrent(BeanPhase.STARTED)) {
            Map<Integer, M> list = this.list;
            try {
                int m = list.size() / 2;
                List<Integer> toTrim = new ArrayList<Integer>(m);
                lock.lock();
                try {
                    Iterator<Integer> it = list.keySet().iterator();
                    int i = 0;
                    while (it.hasNext() && i < m) {
                        toTrim.add(it.next());
                        i++;
                    }
                }
                finally {
                    lock.unlock();
                }
//            int id;
                int recycled = 0;
                for (int i = 0, len = toTrim.size(); i < len; i++) {
                    int id = toTrim.get(i);
                    Object request = list.get(id);
                    if (request instanceof Timeoutable) {
                        Timeoutable timeout = (Timeoutable)request;
                        if (timeout.isTimeout()) {
                            removeMessage(id);
                            timeout.timeout();
                            recycled++;
                        }
                    }
                }
                if (logger.isDebugEnabled() && recycled > 0) {
                    logger.debug("recycle message count=" + recycled);
                }
            }
            catch (Throwable e) {
                logger.warn("recycle message error", e);
            }
        }
    }
}
