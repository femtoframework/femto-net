package org.femtoframework.net.gmpp.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.femtoframework.net.gmpp.GmppConstants;
import org.femtoframework.io.CodecUtil;

/**
 * Ping
 *
 * @author fengyun
 * @version 1.00 Mar 14, 2002 3:13:20 PM
 */
public class PingPacket extends PacketBase
{
    /**
     * 创建时间
     */
    private long creationTime;

    /**
     * 构造Ping包
     */
    public PingPacket()
    {
        super(GmppConstants.PACKET_PING);
        this.creationTime = System.currentTimeMillis();
    }

    /**
     * 构造
     *
     * @param id 标识
     */
    public PingPacket(int id)
    {
        super(GmppConstants.PACKET_PING, id);
        this.creationTime = System.currentTimeMillis();
    }

    /**
     * 判断两个包是不是等效的
     *
     * @param obj 对象
     */
    public boolean equals(Object obj)
    {
        return super.equals(obj) &&
               creationTime == ((PingPacket) obj).creationTime;
    }

    /**
     * 输出
     *
     * @param pos 包输出流
     */
    public void writePacket(OutputStream pos)
        throws IOException
    {
        super.writePacket(pos);
        CodecUtil.writeLong(pos, creationTime);
    }

    /**
     * 输入
     *
     * @param pis 包输入流
     */
    public void readPacket(InputStream pis)
        throws IOException
    {
        super.readPacket(pis);
        creationTime = CodecUtil.readLong(pis);
    }
}
