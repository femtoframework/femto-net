package org.femtoframework.net.gmpp.packet;

import org.femtoframework.net.gmpp.GmppConstants;

/**
 * 关闭连接响应报文
 *
 * @author fengyun
 * @version 1.00 2004-7-1 20:56:18
 */
public class CloseRepPacket extends PacketBase
{
    public CloseRepPacket()
    {
        super(GmppConstants.PACKET_CLOSE_REP);
    }

    public CloseRepPacket(int id)
    {
        super(GmppConstants.PACKET_CLOSE_REP, id);
    }
}