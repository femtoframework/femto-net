package org.femtoframework.net.nio;

import org.femtoframework.net.socket.SocketContext;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Socket Channel 上下文
 *
 * @author fengyun
 */
public interface SocketChannelContext extends SocketContext
{
    String SOCKET_CHANNEL = "socket_channel";
    int DEFAULT_TIMEOUT = 60 * 1000;

    int OP_READ = SelectionKey.OP_READ;
    int OP_WRITE = SelectionKey.OP_WRITE;
    int OP_CONNECT = SelectionKey.OP_CONNECT;
    int OP_ACCEPT = SelectionKey.OP_ACCEPT;

    /**
     * 返回Socket通道
     *
     * @return Socket通道
     */
    SocketChannel getChannel();

    /**
     * 替换SocketChannel
     *
     * @param channel SocketChannel
     */
    void setChannel(SocketChannel channel);

    /**
     * 返回SelectionKeykey
     *
     * @return SelectionKeykey
     */
    SelectionKey getSelectionKey();

    /**
     * 设置SelectionKeykey
     *
     * @param key key
     */
    void setSelectionKey(SelectionKey key);

    /**
     * 设置有效的操作集合
     *
     * @param ops
     */
    void setOperationSet(int ops);

    /**
     * 返回有效的操作集合
     *
     * @return 有效的操作集合
     */
    int getOperationSet();


    /**
     * 设置读写超时时间
     *
     * @param timeout 超时时间
     */
    void setTimeout(int timeout);

    /**
     * 返回读写超时时间
     *
     * @return 读写超时时间
     */
    int getTimeout();

    /**
     * 判断是否超时
     *
     * @return 是否超时
     */
    boolean isTimeout();

    /**
     * 更新最好访问时间，用当前的系统时间更新时间戳
     */
    void updateLastAccess();

    /**
     * 更新最好访问时间，用给定的时间更新时间戳
     *
     * @param timestamp 时间戳
     */
    void updateLastAccess(long timestamp);

    /**
     * 返回最好更新时间
     *
     * @return 最好更新时间
     */
    long getLastAccess();

    /**
     * 返回SocketChannelRegistry
     *
     * @return SocketChannelRegistry
     */
    SocketChannelRegistry getRegistry();

    /**
     * 获取事件
     *
     * @return 事件
     */
    SocketChannelEvent getEvent();

    /**
     * 获取事件回调
     *
     * @return 事件回调
     */
    SocketChannelCallback getCallback();

    /**
     * 设置事件回调
     *
     * @param callback 事件回调
     */
    void setCallback(SocketChannelCallback callback);
}
