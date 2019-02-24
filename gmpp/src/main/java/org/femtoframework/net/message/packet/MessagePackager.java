package org.femtoframework.net.message.packet;



/**
 * 消息打包者，将消息打包成Packet
 * 将原先在SocketClient中的报文转换和处理的代码变成一个可以被替换的接口。<br>
 * 这个接口是MessageLayer和PacketLayer的一个交互界面
 *
 * @author fengyun
 * @version 1.00 2005-5-6 13:17:06
 */
public interface MessagePackager
{
    /**
     * 根据消息打包成报文
     *
     * @param message 消息
     * @return
     */
    public MessagePacket pack(Object message);
}
