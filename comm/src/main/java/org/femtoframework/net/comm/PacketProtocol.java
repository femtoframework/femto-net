package org.femtoframework.net.comm;

import java.io.Closeable;
import java.io.IOException;

/**
 * 报文传输协议
 *
 * @author fengyun
 * @version 1.1 2005-3-30 17:06:50 修改了配置和异常处理
 *          1.00 Mar 12, 2002 5:01:17 PM
 */
public interface PacketProtocol extends Closeable
{
    /**
     * 读取一个信息包
     *
     * @return 返回一个信息包
     * @throws CommException 如果出现异常
     */
    Packet readPacket() throws IOException;

    /**
     * 写出一个信息包
     *
     * @param packet 信息包
     * @throws CommException 如果出现异常
     */
    void writePacket(Packet packet) throws IOException;
}
