package org.femtoframework.net.gmpp.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.femtoframework.io.CodecUtil;
import org.femtoframework.io.IOUtil;
import org.femtoframework.lang.OctetBuffer;
import org.femtoframework.net.HostPort;
import org.femtoframework.net.comm.CommException;
import org.femtoframework.net.gmpp.GmppConstants;
import org.femtoframework.lang.Binary;
import org.femtoframework.util.ArrayUtil;
import org.femtoframework.util.DataUtil;
import org.femtoframework.util.StringUtil;
import org.femtoframework.util.crypto.Hex;
import org.femtoframework.util.crypto.MD5;

/**
 * 连接报文
 *
 * @author fengyun
 * @version 1.00 2004-7-1 20:31:34
 */
public class ConnectPacket extends PacketBase
{
    private byte version = GmppConstants.VERSION_3;

    private static String DEFAULT_HOST;
    private static int DEFAULT_PORT;

    private static String DEFAULT_SERVER_TYPE;

    private String host = DEFAULT_HOST;
    private int port = DEFAULT_PORT;

    // Server Type of current server
    private String serverType;
    
    private String codec;
    private String secure;
    private long timestamp = System.currentTimeMillis();

    private transient byte[] checksum;

    public ConnectPacket()
    {
        super(GmppConstants.PACKET_CONNECT);

    }

    public ConnectPacket(int id)
    {
        super(GmppConstants.PACKET_CONNECT, id);
    }

    /**
     * 构造
     *
     * @param type 包类型 [-128, 127]
     * @param id   标识
     */
    protected ConnectPacket(int type, int id)
    {
        super(type, id);
    }

    public String getHost()
    {
        if (host == null) {
            if (DEFAULT_HOST == null) {
                DEFAULT_HOST = HostPort.getLocalHost();
            }
            host = DEFAULT_HOST;
        }
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        if (port == 0) {
            if (DEFAULT_PORT == 0) {
                DEFAULT_PORT = HostPort.getLocalPort();
            }
            port = DEFAULT_PORT;
        }
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * 返回服务器版本信息
     *
     * @return 服务器版本信息
     */
    public byte getVersion()
    {
        return version;
    }

    /**
     * 设置服务器版本信息
     *
     * @param version
     */
    public void setVersion(byte version)
    {
        this.version = version;
    }

    /**
     * IPUtil中有相应的代码，由于考虑非依赖性，所以采用自己的函数
     *
     * @param host
     */
    private static int toInt(String host)
    {
        int[] addr = DataUtil.toInts(host, '.');
        byte[] bytes = new byte[4];
        bytes[0] = (byte) addr[0];
        bytes[1] = (byte) addr[1];
        bytes[2] = (byte) addr[2];
        bytes[3] = (byte) addr[3];
        return Binary.toInt(bytes);
    }

    /**
     * 整数到字符串的转换
     *
     * @param hostInt
     */
    private static String toString(int hostInt)
    {
        byte[] bytes = Binary.toBytes(hostInt);
        StringBuilder sb = new StringBuilder();
        sb.append((int) bytes[0] & 0xFF).append('.');
        sb.append((int) bytes[1] & 0xFF).append('.');
        sb.append((int) bytes[2] & 0xFF).append('.');
        sb.append((int) bytes[3] & 0xFF);
        return sb.toString();
    }

    /**
     * 输出
     *
     * @param pos 包输出流
     */
    public void writePacket(OutputStream pos)
        throws IOException
    {
        CodecUtil.writeByte(pos, version);
        int hostInt = toInt(getHost());
        CodecUtil.writeInt(pos, hostInt);
        CodecUtil.writeInt(pos, getPort());
        CodecUtil.writeSingle(pos, getServerType());
        CodecUtil.writeSingle(pos, codec);
        if (version == GmppConstants.VERSION_3) {
            //Add MD5 validation
            CodecUtil.writeLong(pos, timestamp);
            byte[] bytes = calcChecksum(hostInt, getPort(), getServerType(), codec, timestamp);
            pos.write(bytes);
        }
    }


    protected byte[] calcChecksum(int hostInt, int port, String serverType, String codec, long timestamp) {
        OctetBuffer buffer = new OctetBuffer();
        buffer.append(timestamp);
        buffer.append(codec.getBytes());
        buffer.append(serverType.getBytes());
        buffer.append(port);
        buffer.append(hostInt);
        buffer.append(getSecure().getBytes());
        return MD5.encrypt(buffer.getValue(), 0, buffer.length());
    }

    protected boolean isValidChecksum(int hostInt, byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return false;
        }
        byte[] checksum = calcChecksum(hostInt, getPort(), getServerType(), codec, timestamp);
        return ArrayUtil.matches(checksum, 0, bytes, 0, bytes.length);
    }


    public boolean isValidChecksum() {
        return isValidChecksum(toInt(host), checksum);
    }

    /**
     * 输入
     *
     * @param pis 包输入流
     */
    public void readPacket(InputStream pis)
        throws IOException
    {
        this.version = CodecUtil.readByte(pis);
        int hostInt = CodecUtil.readInt(pis);
        this.host = toString(hostInt);
        this.port = CodecUtil.readInt(pis);
        this.serverType = CodecUtil.readSingle(pis);
        this.codec = CodecUtil.readSingle(pis);
        if (version == GmppConstants.VERSION_3) {
            this.timestamp = CodecUtil.readLong(pis);
            this.checksum = new byte[16];
            IOUtil.readFully(pis, getChecksum());
        }
    }

    /**
     * 返回服务器类型
     *
     * @return 服务器类型
     */
    public String getServerType()
    {
        if (serverType == null) {
            if (DEFAULT_SERVER_TYPE == null) {
                DEFAULT_SERVER_TYPE = System.getProperty("cube.system.type", "cube");
            }
            serverType = DEFAULT_SERVER_TYPE;
        }
        return serverType;
    }

    /**
     * 设置服务器类型
     *
     * @param serverType 服务器类型
     */
    public void setServerType(String serverType)
    {
        this.serverType = serverType;
    }

    public String getCodec()
    {
        return codec;
    }

    public void setCodec(String codec)
    {
        this.codec = codec;
    }

    public String getSecure() {
        if (StringUtil.isInvalid(secure)) {
            String secureEnv = System.getenv("FEMTO_NET_GMPP_SECURE");
            if (secureEnv != null) {
                secure = secureEnv;
            }
            if (secure == null) {
                secure = "";
            }
        }
        return secure;
    }

    public void setSecure(String secure) {
        this.secure = secure;
    }

    public byte[] getChecksum() {
        return checksum;
    }
}
