package org.femtoframework.net.message;

/**
 * 请求消息，用来标识那些需要响应的消息
 *
 * @author fengyun
 * @version 1.00 2005-5-21 20:25:44
 */
public interface RequestMessage extends Message
{
    /**
     * 返回请求超时时间，如果请求超时时间是0，表示没有超时
     *
     * @return 请求超时时间
     */
    public int getTimeout();
}
