package org.femtoframework.net.gmpp;

/**
 * 常量集
 *
 * @author fengyun
 * @version 1.00 2004-7-1 20:42:29
 */
public interface GmppConstants
{
    byte VERSION = 0x20;

    byte VERSION_3 = 0x30;

    /**
     * 成功的状态码
     */
    int SC_OK = 200;

    /**
     * 不支持指定的版本号
     */
    int SC_UNSUPPORTED_VERSION = 301;

    /**
     * 不支持指定的编码方式
     */
    int SC_UNSUPPORTED_CODEC = 302;

    /**
     * Invalid secure code
     */
    int SC_INVALID_SECURE = 303;

    /**
     * 没有数据，忽略
     */
    int PACKET_NONE = 0;

    int PACKET_CONNECT = 0x05;

    int PACKET_CONNECT_REP = -0x05;

    int PACKET_CLOSE = 0x02;

    int PACKET_CLOSE_REP = -0x02;

    int PACKET_PING = 0x04;

    int PACKET_PING_REP = -0x04;

    int PACKET_MESSAGE = 0x22;
}
