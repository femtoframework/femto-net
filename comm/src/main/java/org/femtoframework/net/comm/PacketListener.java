package org.femtoframework.net.comm;

/**
 * 报文侦听者，当有报文到达的时候该接口中的onPacket方法将被调用
 *
 * @author fengyun
 * @version 1.00 Mar 13, 2002 7:42:29 PM
 * @see Packet
 */
public interface PacketListener
{
    /**
     * 报文到达的时候调用
     *
     * @param packet 报文
     */
    void onPacket(Packet packet);
}
