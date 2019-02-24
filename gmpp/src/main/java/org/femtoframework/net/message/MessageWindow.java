package org.femtoframework.net.message;

/**
 * 消息窗口，用来维持请求消息，并对发送的请求大小进行控制
 *
 * @author fengyun
 * @version 1.00 2005-5-21 20:45:58
 */
public interface MessageWindow<M>
{
    /**
     * 添加消息
     *
     * @param msgId   消息标识
     * @param message 消息
     * @return 如果添加成成功，返回<code>true</code>
     */
    public boolean addMessage(int msgId, M message);

    /**
     * 根据消息标识删除消息
     *
     * @param id 消息标识
     */
    public M removeMessage(int id);

    /**
     * 返回当前窗口中的消息总数
     *
     * @return 消息总数
     */
    public int getMessageCount();

    /**
     * 返回最大消息数目
     *
     * @return 最大消息数目
     */
    public int getMaxCount();

    /**
     * 设置最大消息数目
     *
     * @param maxCount 消息数目
     */
    public void setMaxCount(int maxCount);

    /**
     * 根据消息标识返回消息
     *
     * @param id 消息标识
     */
    public M getMessage(int id);
}
