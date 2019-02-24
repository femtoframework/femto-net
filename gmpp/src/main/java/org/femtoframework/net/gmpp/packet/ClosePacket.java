package org.femtoframework.net.gmpp.packet;

import org.femtoframework.net.gmpp.GmppConstants;

/**
 * 关闭连接包
 *
 * @author fengyun
 * @version 1.00 Mar 15, 2002 11:51:05 AM
 */
public class ClosePacket extends PacketBase
{
    /**
     * 构造关闭连接包
     */
    public ClosePacket()
    {
        super(GmppConstants.PACKET_CLOSE);
    }

    /**
     * 构造
     *
     * @param id 标识
     */
    public ClosePacket(int id)
    {
        super(GmppConstants.PACKET_CLOSE, id);
    }
}
