package org.femtoframework.net.message.ext;


import org.femtoframework.bean.annotation.Ignore;
import org.femtoframework.lang.reflect.NoSuchClassException;
import org.femtoframework.lang.reflect.Reflection;
import org.femtoframework.net.message.MessageCreator;
import org.femtoframework.net.message.MessageMetadata;
import org.femtoframework.net.message.RequestMessage;
import org.femtoframework.net.message.ResponseMessage;

/**
 * 消息Metadata实现
 *
 * @author fengyun
 * @version 1.00 2005-5-21 20:12:27
 */
public class SimpleMessageMetadata
    implements MessageMetadata {
    private String listener = DEFAULT_LISTENER;
    private int type;
    private String messageClassName;
    private String creatorClassName;

    @Ignore
    private transient Class<?> messageClass;
    @Ignore
    private transient MessageCreator creator;

    public SimpleMessageMetadata() {
    }

    /**
     * 返回消息的类型
     *
     * @return 消息的类型
     */
    public int getType() {
        return type;
    }

    /**
     * 返回消息对应的类
     *
     * @return 消息对应的类
     */
    public <M> Class<M> getMessageClass() {
        if (messageClass == null) {
            if (messageClassName == null) {
                throw new IllegalArgumentException("No message class name definied");
            }
            try {
                messageClass = Reflection.loadClass(messageClassName);
            }
            catch (ClassNotFoundException e) {
                throw new NoSuchClassException("No such class:" + messageClassName);
            }
        }
        return (Class<M>)messageClass;
    }

    private static DefaultMessageCreator defaultMessageCreator = new DefaultMessageCreator();

    /**
     * 返回消息的构建器
     *
     * @return 消息构建器
     */
    public MessageCreator getCreator() {
        if (creator == null) {
            if (creatorClassName == null) {
                creator = defaultMessageCreator;
            }
            else {
                creator = (MessageCreator)Reflection.newInstance(creatorClassName);
            }
        }
        return creator;
    }

    /**
     * 根据消息类型创建一个新的对象
     *
     * @return 返回对象
     */
    public <M> M createMessage() {
        MessageCreator creator = getCreator();
        Class<M> clazz = getMessageClass();
        return creator.createMessage(type, clazz);
    }

    /**
     * 消息是否是请求消息（实现了RequestMessage的为请求消息）
     *
     * @return 是否是请求消息
     */
    public boolean isRequest() {
        return RequestMessage.class.isAssignableFrom(getMessageClass());
    }

    /**
     * 消息是否是响应消息（实现了ResponseMessage的为请求消息）
     *
     * @return 是否是响应消息
     */
    public boolean isResponse() {
        return ResponseMessage.class.isAssignableFrom(getMessageClass());
    }

    /**
     * 返回处理消息的侦听者名称（默认是'*'，表示所有侦听者都能够处理）
     *
     * @return 返回能够处理该消息的侦听者名称
     */
    public String getListener() {
        return listener;
    }

    /**
     * 设置消息类型
     *
     * @param type 消息类型
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * 设置消息类
     *
     * @param messageClass 消息类名
     */
    public void setMessageClass(String messageClass) {
        this.messageClassName = messageClass;
    }

    /**
     * 设置构建器类
     *
     * @param creator 构建器
     */
    public void setCreator(String creator) {
        this.creatorClassName = creator;
    }

    /**
     * 设置侦听者名称
     *
     * @param listener 侦听者名称
     */
    public void setListener(String listener) {
        this.listener = listener;
    }
}
