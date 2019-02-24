package org.femtoframework.net.message;

/**
 * Message Metadata用来定义系统中特定消息的相关信息
 *
 * @author fengyun
 * @version 1.00 2005-5-21 19:28:39
 */
public interface MessageMetadata {

    String DEFAULT_LISTENER = "*";

    /**
     * 返回消息的类型
     *
     * @return 消息的类型
     */
    public int getType();

    /**
     * 返回消息对应的类
     *
     * @return 消息对应的类
     */
    public <M> Class<M> getMessageClass();

    /**
     * 根据消息类型创建一个新的对象
     *
     * @return 返回对象
     */
    public <M> M createMessage();


    /**
     * 消息是否是请求消息（实现了RequestMessage的为请求消息）
     *
     * @return 是否是请求消息
     */
    public boolean isRequest();

    /**
     * 消息是否是响应消息（实现了ResponseMessage的为请求消息）
     *
     * @return 是否是响应消息
     */
    public boolean isResponse();

    /**
     * 返回处理消息的侦听者名称（默认是'*'，表示所有侦听者都能够处理）
     *
     * @return 返回能够处理该消息的侦听者名称
     */
    public String getListener();
}
