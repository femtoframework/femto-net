package org.femtoframework.net.gmpp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.femtoframework.io.CodecUtil;
import org.femtoframework.io.IOUtil;
import org.femtoframework.io.ObjectCodec;
import org.femtoframework.io.ObjectCodecUtil;
import org.femtoframework.net.comm.CommException;
import org.femtoframework.net.comm.Packet;
import org.femtoframework.net.comm.PacketProtocol;
import org.femtoframework.net.comm.ParametersAware;
import org.femtoframework.net.comm.tcp.SocketAware;
import org.femtoframework.net.gmpp.packet.ClosePacket;
import org.femtoframework.net.gmpp.packet.CloseRepPacket;
import org.femtoframework.net.gmpp.packet.ConnectPacket;
import org.femtoframework.net.gmpp.packet.ConnectRepPacket;
import org.femtoframework.net.gmpp.packet.GmppMessagePacket;
import org.femtoframework.net.gmpp.packet.PingPacket;
import org.femtoframework.net.gmpp.packet.PingRepPacket;
import org.femtoframework.parameters.Parameters;

/**
 * GMPP Packet 协议
 *
 * @author fengyun
 * @version 1.00 2005-5-6 23:22:18
 */
public class GmppPacketProtocol
    implements PacketProtocol, SocketAware, GmppConstants, ParametersAware
{
    /**
     * 输入流
     */
    private InputStream input;

    /**
     * 输出流
     */
    private OutputStream output;

    /**
     * 消息的编码解码器
     */
    private ObjectCodec codec = ObjectCodecUtil.getDefaultObjectCodec();

    /**
     * 设置Socket
     *
     * @param socket Socket
     */
    public void setSocket(Socket socket) throws IOException
    {
        input = new BufferedInputStream(socket.getInputStream());
        output = new BufferedOutputStream(socket.getOutputStream());
    }

    /**
     * 读取一个信息包
     *
     * @return 返回一个信息包
     * @throws CommException
     *          如果出现异常
     */
    public Packet readPacket() throws IOException
    {
        int type;
        int id;
        GmppPacket packet = null;
        synchronized (input) {
            do {
                type = CodecUtil.readInt(input);
                id = CodecUtil.readInt(input);

                switch (type) {
                    case PACKET_CLOSE:
                        packet = new ClosePacket(id);
                        break;
                    case PACKET_CONNECT:
                        packet = new ConnectPacket(id);
                        break;
                    case PACKET_PING:
                        packet = new PingPacket(id);
                        break;
                    case PACKET_MESSAGE:
                        GmppMessagePacket mp = new GmppMessagePacket(id);
                        mp.setCodec(codec);
                        packet = mp;
                        break;
                    case PACKET_CONNECT_REP:
                        packet = new ConnectRepPacket(id);
                        break;
                    case PACKET_CLOSE_REP:
                        packet = new CloseRepPacket(id);
                        break;
                    case PACKET_PING_REP:
                        packet = new PingRepPacket(id);
                        break;
                    case PACKET_NONE:
                        break;
                }
            }
            while (type == PACKET_NONE);

            if (packet != null) {
                packet.readPacket(input);
                return packet;
            }
        }
        throw new CommException("Invalid Packet: " + type);
    }

    /**
     * 写出一个信息包
     *
     * @param packet 信息包
     * @throws org.femtoframework.net.comm.CommException
     *          如果出现异常
     */
    public void writePacket(Packet packet) throws IOException
    {
        synchronized (output) {
            GmppPacket gp = (GmppPacket) packet;
            CodecUtil.writeInt(output, gp.getType());
            CodecUtil.writeInt(output, gp.getId());
            gp.writePacket(output);
            output.flush();
        }
    }

    /**
     * 关闭协议
     *
     * @throws CommException
     *          关闭异常
     */
    public void close() throws IOException
    {
        IOUtil.close(input);
        IOUtil.close(output);
    }

    /**
     * 设置连接参数集合
     *
     * @param parameters 连接参数集合
     */
    public void setParameters(Parameters parameters)
    {
        String codec = parameters.getString("codec");
        setCodec(codec);
    }

    public void setCodec(String codec) {
        if (codec != null) {
            this.codec = ObjectCodecUtil.getObjectCodec(codec);
        }
    }
}
