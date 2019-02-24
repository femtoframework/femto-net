package org.femtoframework.net.comm;

import java.io.IOException;

/**
 * 一个抽象的连接
 *
 * @author fengyun
 * @version 1.1 2005-3-30 17:06:50 把类改成接口，修改配置方式
 *          1.00 Mar 13, 2002 10:38:56 PM
 */
public interface Connection extends PacketProtocol
{
    /**
     * 判断连接是否还处于激活状态
     *
     * @return 如果还在活动状态返回<code>true</code>，否则返回<code>false</code>
     */
    boolean isAlive();

    /**
     * 连接请求
     *
     * @throws CommException 如果连接异常
     */
    void connect() throws IOException;

    /**
     * 连接超时，告诉另外一方，此连接已经超时
     *
     * @throws CommException 如果发送异常的时候抛出
     */
    void timeout() throws IOException;

    /**
     * 发送Ping命令，检查连接是否正常
     *
     * @throws CommException 如果发送异常的时候抛出
     */
    void ping() throws IOException;
}
