package org.femtoframework.net.gmpp;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.femtoframework.net.comm.CommException;
import org.femtoframework.net.comm.Packet;
import org.femtoframework.net.comm.tcp.SocketConnection;
import org.femtoframework.net.gmpp.packet.ClosePacket;
import org.femtoframework.net.gmpp.packet.ConnectPacket;
import org.femtoframework.net.gmpp.packet.ConnectRepPacket;
import org.femtoframework.net.gmpp.packet.PingPacket;
import org.femtoframework.net.gmpp.packet.PingRepPacket;
import org.femtoframework.parameters.Parameters;
import org.femtoframework.util.StringUtil;

import static org.femtoframework.net.gmpp.GmppConstants.SC_INVALID_SECURE;

/**
 * Gmpp连接实现
 *
 * @author fengyun
 * @version 1.00 2005-5-6 23:07:04
 */
public class GmppConnection extends SocketConnection
{
    private String remoteHost;
    private int remotePort;
    private byte remoteVersion;
    private String remoteType;
    private String codec = "apsis";
    private String secure = "";
    private byte localVersion = GmppConstants.VERSION_3;

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
     * @return 远程版本
     */
    public byte getRemoteVersion()
    {
        return remoteVersion;
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
        Packet packet;
        while (true) {
            packet = super.readPacket();
            //Ping相应报文，送回Ping响应报文
            if (packet instanceof PingPacket) {
                PingPacket ping = (PingPacket)packet;
                PingRepPacket pingRep = new PingRepPacket(ping.getId());
                pingRep.setReceivedTime(System.currentTimeMillis());
                writePacket(pingRep); //读取下一个
                continue;
            }
            else if (packet instanceof PingRepPacket) {
                continue;
            }
            else if (packet instanceof ClosePacket) {
                super.doClose();
                throw new EOFException("Socket closed");
            }
            break;
        }
        return packet;
    }

    /**
     * 连接请求
     *
     * @throws CommException
     *          如果连接异常
     */
    protected void doConnect() throws IOException
    {
        ConnectPacket connect = new ConnectPacket();
        //发送客户端编码解码信息
        connect.setVersion(localVersion);
        connect.setCodec(codec);
        connect.setSecure(secure);
        writePacket(connect);
        Packet packet = readPacket();
        if (packet instanceof ConnectRepPacket) {
            ConnectRepPacket response = (ConnectRepPacket)packet;
            if (response.isStatusOK()) {
                remoteHost = response.getHost();
                remotePort = response.getPort();
                remoteVersion = response.getVersion();
                remoteType = response.getServerType();
            }
            else {
                throw new CommException("Can't connect to server:" + getHost()
                                        + ':' + getPort() + " status:" + response.getStatus());
            }
        }
        else {
            throw new CommException("Invalid response packet from server:" + getHost() + ':' + getPort());
        }
    }

    /**
     * 等待连接请求
     *
     * @param supportedVersions 支持的版本
     * @param supportedCodecs   支持的编码解码器
     * @throws IOException
     */
    public void accept(byte[] supportedVersions, Set supportedCodecs) throws IOException
    {
        Packet request = readPacket();
        if (request instanceof ConnectPacket) {
            ConnectPacket connect = (ConnectPacket)request;
            byte version = connect.getVersion();
            ConnectRepPacket response = new ConnectRepPacket();
            if (Arrays.binarySearch(supportedVersions, version) < 0) {
                //不支持的版本号
                response.setStatus(GmppConstants.SC_UNSUPPORTED_VERSION);
            }
            else {
                String codec = connect.getCodec();
                if (codec != null && !supportedCodecs.contains(codec)) {
                    //不支持的Codec
                    response.setStatus(GmppConstants.SC_UNSUPPORTED_CODEC);
                }
                else {
                    response.setCodec(codec);

                    if (version == GmppConstants.VERSION_3) {
                        if (!connect.isValidChecksum()) {
                            response.setStatus(SC_INVALID_SECURE);
                        }
                    }
                }
            }
            writePacket(response);

            //结束连接信息
            remoteHost = connect.getHost();
            remotePort = connect.getPort();
            remoteVersion = version;
            remoteType = connect.getServerType();
            codec = connect.getCodec();
            setAlive(true);//设置成连接状态
        }
        else {
            throw new CommException("Invalid packet:" + request);
        }
    }

    /**
     * 连接超时，告诉另外一方，此连接已经超时
     *
     * @throws CommException
     *          如果发送异常的时候抛出
     */
    public void timeout() throws IOException
    {
    }

    /**
     * 发送Ping命令，检查连接是否正常
     *
     * @throws CommException
     *          如果发送异常的时候抛出
     */
    public void ping() throws IOException
    {
        try {
            writePacket(new PingPacket());
        }
        catch (IOException ex) {
            //直接调用关闭
            super.doClose();
            throw ex;
        }
    }

    /**
     * 关闭连接
     *
     * @throws CommException
     *          如果连接异常
     */
    protected void doClose() throws IOException
    {
        try {
            if (isAlive()) {
                writePacket(new ClosePacket());
            }
        }
        finally {
            super.doClose();
        }
    }

    /**
     * 返回远程服务器类型
     *
     * @return 远程服务器类型
     */
    public String getRemoteType()
    {
        return remoteType;
    }

    /**
     * 设置连接参数集合
     *
     * @param parameters 连接参数集合
     */
    public void setParameters(Parameters parameters)
    {
        super.setParameters(parameters);
        codec = parameters.getString("codec", codec);
        secure = parameters.getString("secure", secure);
        localVersion = (byte)parameters.getInt("local_version", localVersion);
    }

    /**
     * 返回对象编码加码
     *
     * @return 对象编码加码
     */
    public String getCodec()
    {
        return codec;
    }

    public String toString()
    {
        return "GmppConnection{" +
               "remoteHost='" + remoteHost + '\'' +
               ", remotePort=" + remotePort +
               ", remoteType='" + remoteType + '\'' +
               '}';
    }

    public String getSecure() {
        return secure;
    }

    public void setSecure(String secure) {
        this.secure = secure;
    }

    public byte getLocalVersion() {
        return localVersion;
    }

    public void setLocalVersion(byte localVersion) {
        this.localVersion = localVersion;
    }
}
