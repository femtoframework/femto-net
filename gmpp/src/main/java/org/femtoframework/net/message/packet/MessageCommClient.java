package org.femtoframework.net.message.packet;


import org.femtoframework.bean.Lifecycle;
import org.femtoframework.net.comm.PacketFuture;
import org.femtoframework.net.comm.packet.PacketCommClient;
import org.femtoframework.net.message.*;

import java.io.IOException;

/**
 * 基于消息的通讯客户端（偶合MessageLayer和PacketLayer）
 * 为什么要继承？如果通过非紧密的偶合，那么需要相对复杂的配置
 *
 * @author fengyun
 * @version 1.00 2005-5-21 22:12:44
 */
public class MessageCommClient extends PacketCommClient
    implements MessageLayer
{
    /**
     * 消息侦听者
     */
    protected MessageListener messageListener;

    /**
     * 消息打包程序
     */
    private MessagePackager messagePackager;

    /**
     * 消息窗口
     */
    protected MessageWindow<Message> window = new SimpleMessageWindow<>();

    /**
     * 设置消息侦听者
     *
     * @param listener 消息侦听者
     */
    public void setMessageListener(MessageListener listener)
    {
        this.messageListener = listener;
    }

    /**
     * 返回消息侦听者
     *
     * @return 消息侦听者
     */
    public MessageListener getMessageListener()
    {
        return messageListener;
    }

    /**
     * 发送消息
     *
     * @param message 消息
     * @throws IllegalArgumentException 消息无效的时候抛出
     */
    public MessageFuture send(Object message)
    {
        if (message == null) {
            throw new IllegalArgumentException("Null message");
        }
        MessagePacket packet = messagePackager.pack(message);
        if (packet != null) {
            PacketFuture pf = send(packet);
            MessageWrapper wrapper = new MessageWrapper(message);
            wrapper.setPacketFuture(pf);
            return wrapper;
        }
        else {
            throw new IllegalArgumentException("Can't package the message:" + message);
        }
    }

    /**
     * 发送请求
     *
     * @param message 消息
     * @throws IllegalArgumentException 消息无效的时候抛出
     */
    public RequestFuture submit(RequestMessage message)
    {
        if (message == null) {
            throw new IllegalArgumentException("Null message");
        }

        MessagePacket packet = messagePackager.pack(message);
        if (packet != null) {
            int msgId = packet.getMessageId();
            RequestWrapper wrapper = new RequestWrapper(msgId, message);
            boolean added = window.addMessage(msgId, wrapper);
            if (added) {
                PacketFuture pf = send(packet);
                wrapper.setPacketFuture(pf);
            }
            else {
                //窗口已经满了
                throw new MessageWindowFullException("Message window full");
            }
            return wrapper;
        }
        else {
            throw new IllegalArgumentException("Can't package the message:" + message);
        }
    }

    /**
     * 返回消息打包程序
     *
     * @return 消息打包程序
     */
    public MessagePackager getPackager()
    {
        return messagePackager;
    }

    /**
     * 设置消息打包程序
     *
     * @param packager 消息打包程序
     */
    public void setPackager(MessagePackager packager)
    {
        this.messagePackager = packager;
    }

    /**
     * 返回消息窗口
     *
     * @return 消息窗口
     */
    public MessageWindow<Message> getWindow()
    {
        return window;
    }

    /**
     * 设置消息窗口
     *
     * @param window 消息窗口
     */
    public void setWindow(MessageWindow<Message> window)
    {
        this.window = window;
    }

    /**
     * 强制建立连接，确保通道畅通
     *
     * @return 有效的连接个数
     * @throws IOException 异常
     */
    public int connect() throws IOException
    {
        if (window != null) {
            if (window instanceof Lifecycle) {
                ((Lifecycle) window).init();
                ((Lifecycle) window).start();
            }
        }
        return super.connect();
    }

    /**
     * 关闭其它部分，方便扩展
     */
    protected void doClose()
    {
        if (window != null) {
            if (window instanceof Lifecycle) {
                ((Lifecycle) window).stop();
                ((Lifecycle) window).destroy();
            }
            window = null;
        }
    }
}
