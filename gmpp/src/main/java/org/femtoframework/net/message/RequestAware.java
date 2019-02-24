package org.femtoframework.net.message;

/**
 * 需要请求的响应（接口注入）
 *
 * @author fengyun
 * @version 1.00 2005-5-21 23:55:16
 */
public interface RequestAware
{
    /**
     * 设置请求
     *
     * @param request 请求
     */
    public void setRequest(Object request);
}
