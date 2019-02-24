package org.femtoframework.net.message;

/**
 * 消息构建器
 *
 * @author fengyun
 * @version 1.00 2005-5-7 12:59:30
 */
public interface MessageCreator {
    /**
     * 根据消息类型构建一个新的对象，
     *
     * @param type  消息类型
     * @param clazz 消息对应的类
     * @return 返回对象
     * @throws ObjectCreationException
     *          当创建对象异常的时候抛出
     */
    public <M> M createMessage(int type, Class<M> clazz);
}
