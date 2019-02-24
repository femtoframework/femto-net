package org.femtoframework.net.comm;

/**
 * COMM工具类
 *
 * @author fengyun
 * @version 1.00 2005-5-5 16:10:27
 */
public class CommUtil
{
    /**
     * 安全关闭连接
     *
     * @param conn 连接
     */
    public static void close(Connection conn)
    {
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (Exception e) {
        }
    }

    /**
     * 关闭通讯客户端
     *
     * @param client 通讯客户端
     */
    public static void close(CommClient client)
    {
        try {
            if (client != null) {
                client.close();
            }
        }
        catch (Exception e) {
        }
    }

    /**
     * 关闭通讯协议
     *
     * @param protocol 通讯协议
     */
    public static void close(PacketProtocol protocol)
    {
        try {
            if (protocol != null) {
                protocol.close();
            }
        }
        catch (Exception e) {
        }
    }
}
