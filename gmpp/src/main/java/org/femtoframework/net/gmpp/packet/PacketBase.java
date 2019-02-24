package org.femtoframework.net.gmpp.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.femtoframework.net.comm.AbstractPacket;
import org.femtoframework.net.gmpp.GmppPacket;

/**
 * 报文基类
 *
 * @author fengyun
 * @version 1.00 2005-5-7 12:10:44
 */
public class PacketBase extends AbstractPacket
    implements GmppPacket
{
    /**
     * 构造
     *
     * @param type 包类型 [-0x8000000, 0x7FFFFFFF]
     */
    protected PacketBase(int type)
    {
        super(type);
    }

    /**
     * 构造
     *
     * @param type 包类型 [-128, 127]
     * @param id   标识
     */
    protected PacketBase(int type, int id)
    {
        super(type, id);
    }

    /**
     * 输出报文到输出流中
     *
     * @param out 输出流
     */
    public void writePacket(OutputStream out) throws IOException
    {

    }

    /**
     * 读取报文
     *
     * @param in 输入流
     */
    public void readPacket(InputStream in) throws IOException
    {

    }
}
