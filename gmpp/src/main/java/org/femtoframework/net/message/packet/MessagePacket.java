package org.femtoframework.net.message.packet;

import org.femtoframework.bean.Destroyable;
import org.femtoframework.net.comm.Packet;
import org.femtoframework.net.message.WrappedMessage;

/**
 * 消息报文，特指那些可以携带消息的报文
 *
 * @author fengyun
 * @version 1.00 2005-5-7 16:56:19
 */
public interface MessagePacket
    extends WrappedMessage, Packet, Destroyable
{
    /**
     * 返回消息的类型，用于判断消息属于什么类型
     */
    public int getMessageType();

    /**
     * 返回消息的序列号
     *
     * @return 序列号
     */
    public int getMessageId();

    /**
     * 返回消息
     *
     * //SerializationException
     *          串行化异常的时候抛出
     */
    public Object getMessage();


    /**
     * 设置消息
     *
     * @param message 消息
     * SerializationException
     *          串行化异常的时候抛出
     */
    public void setMessage(Object message, int msgId);

    /**
     * 设置消息
     *
     * @param message 消息
     * SerializationException 串行化异常的时候抛出
     */
    public void setMessage(Object message);

    /**
     * 返回类装载器
     *
     * @return 类装载器
     */
    public ClassLoader getClassLoader();

    /**
     * 设置类装载器
     *
     * @param classLoader 类装载器
     */
    public void setClassLoader(ClassLoader classLoader);

    /**
     * 返回请求超时时间，如果请求超时时间是0，表示没有超时
     *
     * @return 请求超时时间
     */
    public int getTimeout();

    /**
     * 设置超时时间
     *
     * @param timeout 超时时间
     */
    public void setTimeout(int timeout);
}
