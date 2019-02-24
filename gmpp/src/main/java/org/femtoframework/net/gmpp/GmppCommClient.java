package org.femtoframework.net.gmpp;


import org.femtoframework.io.ObjectCodec;
import org.femtoframework.io.ObjectCodecUtil;
import org.femtoframework.net.comm.Connection;
import org.femtoframework.net.comm.ConnectionEvent;
import org.femtoframework.net.comm.ConnectionWrapper;
import org.femtoframework.net.comm.Packet;
import org.femtoframework.net.gmpp.packet.GmppMessagePacket;
import org.femtoframework.net.message.*;
import org.femtoframework.net.message.packet.MessageCommClient;
import org.femtoframework.net.message.packet.MessagePackager;
import org.femtoframework.net.message.packet.MessagePacket;
import org.femtoframework.net.message.packet.RequestWrapper;

/**
 * GMPP Comm 客户端扩展
 * <p/>
 * 1. 增加Codec特性
 * 2. 内部实现TaskPackager接口，说明如何对消息进行打包
 * 3. 从连接中获取远程主机和端口信息
 *
 * @author fengyun
 * @version 1.00 2005-5-7 22:16:06
 */
public class GmppCommClient extends MessageCommClient
    implements MessagePackager
{
    /**
     * 消息注册
     */
    private static MessageRegistry registry = MessageRegistryUtil.getRegistry();

    /**
     * 远程主机地址（唯一标识号，跟连接绑定的服务器地址可能不同）
     */
    private String remoteHost;

    /**
     * 远程主机端口（唯一标识号，跟连接的端口可能不同）
     */
    private int remotePort;

    /**
     * GMPP版本号
     */
    private byte remoteVersion;

    /**
     * 远程服务器类型
     */
    private String remoteType;

    /**
     * 对象编码解码器
     */
    private ObjectCodec objectCodec = ObjectCodecUtil.getDefaultObjectCodec();

    /**
     * 对象编码解码器名称
     */
    private String codecName;

    /**
     * 构造
     */
    public GmppCommClient()
    {
        //设置默认的配置，简化配置
        setPackager(this);
        setPacketListener(this);
        setConnectionClass(GmppConnection.class);
        setProtocolClass(GmppPacketProtocol.class);
    }

    /**
     * 返回消息解码规则
     *
     * @return 编码类型
     */
    public String getCodec()
    {
        return codecName;
    }

    /**
     * 设置消息解码规则
     *
     * @param codec 解码规则
     */
    public void setCodec(String codec)
    {
        this.codecName = codec;
        this.objectCodec = ObjectCodecUtil.getObjectCodec(codec);
    }

    /**
     * 已经连接（从连接中获取远程服务器的信息）
     *
     * @param e 连接事件
     */
    public void connected(ConnectionEvent e)
    {
        Connection c = e.getConnection();
        GmppConnection gconn = null;
        if (c instanceof GmppConnection) {
            gconn = (GmppConnection) c;
        }
        else {
            while (c instanceof ConnectionWrapper) {
                ConnectionWrapper wrapper = (ConnectionWrapper) c;
                c = wrapper.getConnection();
                if (c instanceof GmppConnection) {
                    gconn = (GmppConnection) c;
                    break;
                }
            }
        }
        if (gconn != null) {
            remoteHost = gconn.getRemoteHost();
            remotePort = gconn.getRemotePort();
            remoteVersion = gconn.getRemoteVersion();
            remoteType = gconn.getRemoteType();
        }
        super.connected(e);
    }

    /**
     * 返回远程主机
     *
     * @return 远程主机
     */
    public String getRemoteHost()
    {
        return remoteHost;
    }

    /**
     * 返回远程端口
     *
     * @return 远程端口
     */
    public int getRemotePort()
    {
        return remotePort;
    }

    /**
     * 返回远程版本
     *
     * @return 远程版本号
     */
    public byte getRemoteVersion()
    {
        return remoteVersion;
    }

    /**
     * 根据消息打包成报文
     *
     * @param message 消息
     * @return 打包好的报文
     */
    public MessagePacket pack(Object message)
    {
        GmppMessagePacket packet = new GmppMessagePacket();
        packet.setCodec(objectCodec);
        Object msg = message;
        if (message instanceof RequestResponse) {
            RequestResponse rr = (RequestResponse) message;
            msg = rr.getResponse();
            packet.setMessage(msg, rr.getId());
        }
        else {
            packet.setMessage(msg);
        }

        if (msg instanceof RequestMessage) {
            packet.setTimeout(((RequestMessage) msg).getTimeout());
        }
//        else if (msg instanceof ResponseMessage) {
            //如果是响应消息，那么它应该被释放
//            ((ResponseMessage)msg).destroy();
//        }
        return packet;
    }

    /**
     * 处理信息包
     *
     * @param packet 信息包
     */
    public void onPacket(Packet packet)
    {
        if (logger.isTraceEnabled()) {
            logger.trace("Packet From " + getRemoteHost() + ":" + getRemotePort());
        }
        if (packet instanceof MessagePacket) {
            MessagePacket mp = ((MessagePacket) packet);
            int type = mp.getMessageType();
            if (type < 0) { //响应
                int id = mp.getMessageId();
                RequestWrapper wrapper = (RequestWrapper) window.removeMessage(id);
                if (wrapper == null) {
                    return;
                }
                wrapper.setResponse(mp);
                return;
            }

            if (messageListener == null) {
                throw new IllegalStateException("No message listener set");
            }

            MessageMetadata metadata = registry.getMetadata(type);
            if (metadata.isRequest()) {
                //如果是请求，那么创建GmppReqRepPair
                GmppReqRepPair pair = new GmppReqRepPair(mp, registry);
                pair.setMessageSender(this);
                try {
                    messageListener.onMessage(metadata, pair);
                }
                catch (Throwable t) {
                    pair.getResponse();
                    pair.ack();
                }
            }
            else {
                //否则直接将消息交给侦听者
                messageListener.onMessage(metadata, mp);
            }
        }
        else {
            if (logger != null) {
                logger.warn("Unrecognized packet: " + packet);
            }
        }
    }

    public String getRemoteType()
    {
        return remoteType;
    }

    public void setRemoteHost(String remoteHost)
    {
        this.remoteHost = remoteHost;
    }

    public void setRemotePort(int remotePort)
    {
        this.remotePort = remotePort;
    }

    public void setRemoteVersion(byte remoteVersion)
    {
        this.remoteVersion = remoteVersion;
    }

    public void setRemoteType(String remoteType)
    {
        this.remoteType = remoteType;
    }
}
