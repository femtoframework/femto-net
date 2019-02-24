package org.femtoframework.net.gmpp.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.femtoframework.net.gmpp.GmppConstants;
import org.femtoframework.io.CodecUtil;

/**
 * Ping响应
 *
 * @author fengyun
 * @version 1.00 2004-7-1 20:55:35
 */
public class PingRepPacket extends PacketBase
{
    /**
     * 接收时间
     */
    private long receivedTime;

    public PingRepPacket()
    {
        super(GmppConstants.PACKET_PING_REP);
    }

    public PingRepPacket(int id)
    {
        super(GmppConstants.PACKET_PING_REP, id);
    }

    public void writePacket(OutputStream pos)
        throws IOException
    {
        super.writePacket(pos);
        CodecUtil.writeLong(pos, receivedTime);
    }

    public void readPacket(InputStream pis)
        throws IOException
    {
        super.readPacket(pis);
        receivedTime = CodecUtil.readLong(pis);
    }

    /**
     * 设置收到的时间
     *
     * @param receivedTime 收到的时间
     */
    public void setReceivedTime(long receivedTime)
    {
        this.receivedTime = receivedTime;
    }

    /**
     * 返回收到的时间
     *
     * @return 收到的时间
     */
    public long getReceivedTime()
    {
        return receivedTime;
    }
}
