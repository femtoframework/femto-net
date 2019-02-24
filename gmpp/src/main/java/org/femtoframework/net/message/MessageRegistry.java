package org.femtoframework.net.message;

/**
 * 消息注册登记
 *
 * @author fengyun
 * @version 1.00 2005-5-7 0:11:46
 */
public interface MessageRegistry
{
    /**
     * 没有该类型
     */
    public static final int NO_SUCH_TYPE = -1;

    /**
     * 根据消息对象返回类型
     *
     * @param message 消息对象
     * @return 如果不存在该类型，返回<code>NO_SUCH_TYPE</code>
     */
    public int getType(Object message);

    /**
     * 根据消息类型创建一个新的对象
     *
     * @param type  消息类型
     * @return  返回对象
     */
    public Object createMessage(int type);

    /**
     * 根据类型返回消息元数据
     *
     * @param type 消息类型
     * @return 返回对象
     */
    public MessageMetadata getMetadata(int type);

    /**
     * 根据消息返回消息元数据
     *
     * @param message 消息
     * @return 返回对象
     */
    public MessageMetadata getMetadata(Object message);

    /**
     * 添加消息元数据
     *
     * @param metadata 消息元数据
     */
    public void addMetadata(MessageMetadata metadata);


    /**
     * 删除消息元数据
     *
     * @param type 消息类型
     * @return 删除的消息元数据
     */
    public MessageMetadata removeMetadata(int type);

    /**
     * 删除消息元数据
     *
     * @param clazz 消息类
     * @return 删除的消息元数据
     */
    public MessageMetadata removeMetadata(Class clazz);
}
