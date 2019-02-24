package org.femtoframework.net.message;

import org.femtoframework.implement.ImplementUtil;

/**
 * Message Registry Util
 *
 * @author fengyun
 * @version 1.00 2005-9-1 20:30:13
 */
public class MessageRegistryUtil
{
    private MessageRegistryUtil()
    {
    }

    private static MessageRegistry instance = ImplementUtil.getInstance(MessageRegistry.class);

    public static MessageRegistry getRegistry()
    {
        return instance;
    }

    /**
     * 根据消息对象返回类型
     *
     * @param message 消息对象
     * @return 如果不存在该类型，返回<code>NO_SUCH_TYPE</code>
     */
    public static int getType(Object message)
    {
        return instance.getType(message);
    }

    /**
     * 根据消息类型创建一个新的对象，
     *
     * @param type 消息类型
     * @return 返回对象
     */
    public static Object createMessage(int type)
    {
        return instance.createMessage(type);
    }
    /**
     * 根据类型返回消息元数据
     *
     * @param type 消息类型
     * @return 返回对象
     */
    public static MessageMetadata getMetadata(int type)
    {
        return instance.getMetadata(type);
    }

    /**
     * 添加消息元数据
     *
     * @param metadata 消息元数据
     * @throws org.femtoframework.lang.reflect.NoSuchClassException
     *          当类不存在的时候抛出
     * @throws org.femtoframework.lang.reflect.NoSuchMethodException
     *          当方法不存在的时候抛出
     */
    public static void addMetadata(MessageMetadata metadata)
    {
        instance.addMetadata(metadata);
    }


    /**
     * 删除消息元数据
     *
     * @param type 消息类型
     * @return 删除的消息元数据
     */
    public static MessageMetadata removeMetadata(int type)
    {
        return instance.removeMetadata(type);
    }

    /**
     * 删除消息元数据
     *
     * @param clazz 消息类
     * @return 删除的消息元数据
     */
    public static MessageMetadata removeMetadata(Class clazz)
    {
        return instance.removeMetadata(clazz);
    }

}
