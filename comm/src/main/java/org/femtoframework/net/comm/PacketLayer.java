package org.femtoframework.net.comm;

/**
 * 报文层接口
 *
 * @author fengyun
 * @version 1.00 2005-5-21 21:32:36
 */
public interface PacketLayer extends PacketSender
{
    /**
     * 设置报文侦听者
     *
     * @param listener 报文侦听者
     */
    void setPacketListener(PacketListener listener);

    /**
     * 返回报文侦听者
     *
     * @return 报文侦听者
     */
    PacketListener getPacketListener();
}
