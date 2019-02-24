package org.femtoframework.net;


/**
 * Host Port Address
 *
 * @author Sheldon Shao on 11/25/16.
 * @version 1.0
 */
public interface HostPortAddress {

    /**
     * 返回主机地址
     *
     * @return 主机地址
     */
    String getHost();

    /**
     * 返回端口
     *
     * @return 端口
     */
    int getPort();


    /**
     * ID
     *
     * @return
     */
    long getId();
}
