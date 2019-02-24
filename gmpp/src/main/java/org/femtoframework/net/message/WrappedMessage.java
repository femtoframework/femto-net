package org.femtoframework.net.message;

/**
 * 封装了的消息
 *
 * @author fengyun
 * @version 1.00 2005-5-22 0:21:31
 */
public interface WrappedMessage
{
    /**
     * 返回消息
     *
     * //SerializationException 串行化异常的时候抛出
     */
    public Object getMessage();
}
