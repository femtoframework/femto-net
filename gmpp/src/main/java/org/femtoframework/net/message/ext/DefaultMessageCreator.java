package org.femtoframework.net.message.ext;


import org.femtoframework.net.message.MessageCreator;

/**
 * 默认消息构建器
 *
 * @author fengyun
 * @version 1.00 2005-5-21 20:16:45
 */
public class DefaultMessageCreator implements MessageCreator
{
    /**
     * 构造
     */
    DefaultMessageCreator()
    {
    }

    /**
     * 根据消息类型构建一个新的对象，
     *
     * @param type  消息类型
     * @param clazz 消息对应的类
     * @return 返回对象
     * @throws org.femtoframework.lang.reflect.ObjectCreationException
     *          当创建对象异常的时候抛出
     */
    public <M> M createMessage(int type, Class<M> clazz)
    {
        try {
            return clazz.newInstance();
        }
        catch (Exception e) {
            throw new IllegalStateException("", e);
        }
    }

}
