package org.femtoframework.net.message;

/**
 * 响应侦听者
 *
 * @author fengyun
 * @version 1.00 2005-5-21 22:53:26
 */
public interface ResponseListener
{
    /**
     * 当有响应到达或者有异常的时候调用
     *
     * @param response 响应对象或者异常
     */
    public void onResponse(Object response);
}
