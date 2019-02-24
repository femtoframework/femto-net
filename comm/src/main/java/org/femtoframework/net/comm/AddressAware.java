package org.femtoframework.net.comm;

/**
 * @author fengyun
 * @version 1.00 11-8-21 下午7:43
 */
public interface AddressAware {
    /**
     * 设置主机地址
     *
     * @param host 主机地址
     */
    public void setHost(String host);

    /**
     * 设置端口
     *
     * @param port 端口
     */
    public void setPort(int port);
}
