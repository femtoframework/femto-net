package org.femtoframework.net.comm;

/**
 * 报文发送者
 *
 * @author fengyun
 * @version 1.00 2005-5-21 21:31:50
 */
public interface PacketSender
{
    /**
     * 发送报文给下一层
     *
     * @param packet 报文
     */
    PacketFuture send(Packet packet);
}
