package org.femtoframework.net.message;

/**
 * 消息层接口
 *
 * @author fengyun
 * @version 1.00 2005-5-21 22:10:33
 */
public interface MessageLayer extends MessageSender
{
    /**
     * 设置消息侦听者
     *
     * @param listener 消息侦听者
     */
    public void setMessageListener(MessageListener listener);

    /**
     * 返回消息侦听者
     *
     * @return 消息侦听者
     */
    public MessageListener getMessageListener();
}
