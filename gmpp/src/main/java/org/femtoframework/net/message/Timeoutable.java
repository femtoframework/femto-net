package org.femtoframework.net.message;



/**
 * 请求超时处理接口
 *
 * @author fengyun
 * @version 1.00 2005-5-21 20:51:27
 */
public interface Timeoutable extends RequestMessage
{
    /**
     * 判断请求是否超时
     *
     * @return 是否超时
     */
    public boolean isTimeout();

    /**
     * 处理请求超时
     */
    public void timeout();
}
