package org.femtoframework.net.message;

/**
 * 消息发送者，负责发送消息
 *
 * @author fengyun
 * @version 1.00 2005-5-21 19:44:07
 */
public interface MessageSender
{
    /**
     * 返回目标主机地址
     *
     * @return 目标主机地址
     */
    public String getHost();

    /**
     * 返回目标主机端口
     *
     * @return 目标主机端口
     */
    public int getPort();

    /**
     * 发送消息
     *
     * @param message 消息
     * @throws IllegalArgumentException 消息无效的时候抛出
     */
    public MessageFuture send(Object message);

    /**
     * 发送请求
     *
     * @param message 消息
     * @throws IllegalArgumentException   消息无效的时候抛出
     * @throws MessageWindowFullException 消息窗口满的时候抛出
     */
    public RequestFuture submit(RequestMessage message);
}
