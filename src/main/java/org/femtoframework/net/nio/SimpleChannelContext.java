package org.femtoframework.net.nio;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.femtoframework.io.IOUtil;
import org.femtoframework.net.socket.SimpleSocketContext;

/**
 * 简单的通道上下文
 *
 * @author fengyun
 */
public class SimpleChannelContext extends SimpleSocketContext
    implements SocketChannelContext
{
    private SocketChannel channel;
    private SelectionKey key;
    private int ops;

    private int timeout = DEFAULT_TIMEOUT;

    private long lastAccess;

    /**
     * 注册管理器
     */
    private SocketChannelRegistry registry;

    /**
     * 事件
     */
    private SocketChannelEvent event;

    /**
     * 事件回调
     */
    private SocketChannelCallback callback;

    /**
     * 构造
     *
     * @param channel
     */
    protected SimpleChannelContext(SocketChannel channel)
    {
        init(channel);
    }

    /**
     * 构造
     */
    protected SimpleChannelContext()
    {
    }

    /**
     * 初始化SocketChannel
     *
     * @param channel
     */
    public void init(SocketChannel channel)
    {
        if (channel == null) {
            throw new IllegalArgumentException("Null socket channel");
        }
        init(channel.socket());
        put(SOCKET_CHANNEL, channel);
        this.channel = channel;
        lastAccess = System.currentTimeMillis();
        this.event = null;
        this.callback = null;
    }

    /**
     * /**
     * 返回Socket通道
     *
     * @return Socket通道
     */
    public SocketChannel getChannel()
    {
        return channel;
    }

    /**
     * 替换SocketChannel
     *
     * @param channel SocketChannel
     */
    public void setChannel(SocketChannel channel)
    {
        this.channel = channel;
    }

    /**
     * 返回SelectionKeykey
     *
     * @return SelectionKeykey
     */
    public SelectionKey getSelectionKey()
    {
        return key;
    }

    /**
     * 设置SelectionKeykey
     *
     * @param key key
     */
    public void setSelectionKey(SelectionKey key)
    {
        this.key = key;
    }

    /**
     * 设置有效的操作集合
     *
     * @param ops
     */
    public void setOperationSet(int ops)
    {
        this.ops = ops;
    }

    /**
     * 返回有效的操作集合
     *
     * @return 有效的操作集合
     */
    public int getOperationSet()
    {
        return ops;
    }


    /**
     * 设置读写超时时间
     *
     * @param timeout 超时时间
     */
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    /**
     * 返回读写超时时间
     *
     * @return 读写超时时间
     */
    public int getTimeout()
    {
        return timeout;
    }

    /**
     * 判断是否超时
     *
     * @return 是否超时
     */
    public boolean isTimeout()
    {
        return System.currentTimeMillis() - lastAccess > timeout;
    }

    /**
     * 更新最好访问时间，用当前的系统时间更新时间戳
     */
    public void updateLastAccess()
    {
        updateLastAccess(System.currentTimeMillis());
    }

    /**
     * 更新最好访问时间，用给定的时间更新时间戳
     *
     * @param timestamp 时间戳
     */
    public void updateLastAccess(long timestamp)
    {
        this.lastAccess = timestamp;
    }

    /**
     * 返回最后更新时间
     *
     * @return 最后更新时间
     */
    public long getLastAccess()
    {
        return lastAccess;
    }

    /**
     * 返回SocketChannelRegistry
     *
     * @return SocketChannelRegistry
     */
    public SocketChannelRegistry getRegistry()
    {
        return registry;
    }

    /**
     * 获取事件
     *
     * @return 事件
     */
    public SocketChannelEvent getEvent()
    {
        return event;
    }

    /**
     * 获取事件回调
     *
     * @return 事件回调
     */
    public SocketChannelCallback getCallback()
    {
        return callback;
    }

    /**
     * 关闭之后
     * <p/>
     * 这个方法在容器最终关闭上下文的时候调用
     */
    public void onClosed()
    {
        super.onClosed();

        if (channel != null) {
            IOUtil.close(channel);
            channel = null;
        }
    }

    protected void closeSocket()
    {
    }


    /**
     * 设置注册管理器
     *
     * @param registry
     */
    public void setRegistry(SocketChannelRegistry registry)
    {
        this.registry = registry;
    }

    /**
     * 设置事件
     *
     * @param event 事件
     */
    public void setEvent(SocketChannelEvent event)
    {
        this.event = event;
    }

    public void setCallback(SocketChannelCallback callback)
    {
        this.callback = callback;
    }
}
