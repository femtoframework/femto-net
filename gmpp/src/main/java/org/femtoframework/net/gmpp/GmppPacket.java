package org.femtoframework.net.gmpp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.femtoframework.net.comm.Packet;

/**
 * Gmpp报文
 *
 * @author fengyun
 * @version 1.00 2005-5-7 11:51:50
 */
public interface GmppPacket extends Packet
{

    int getId();

    /**
     * 返回报文类型
     *
     * @since COMM5 将byte型改成int
     */
    int getType();

    /**
     * 输出报文到输出流中
     *
     * @param out 输出流
     */
    void writePacket(OutputStream out)
        throws IOException;

    /**
     * 读取报文
     *
     * @param in 输入流
     */
    void readPacket(InputStream in)
        throws IOException;
}
