package org.femtoframework.net;

import org.femtoframework.lang.Binary;
import org.femtoframework.util.DataUtil;
import org.femtoframework.util.StringUtil;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.InetAddress;

/**
 * 主机端口信息
 *
 * @author fengyun
 * @version Feb 23, 2003 2:07:20 PM
 */
public class HostPort
    implements Externalizable, Cloneable, HostPortAddress {

    private String host;
    private int port;

    /**
     * JVM的唯一标识
     */
    private transient long id;

    /**
     * 字符串信息
     */
    private transient String hostInfo;


    public HostPort() {
        //Externalizable
    }

    /**
     * 构造
     *
     * @param host
     * @param port
     */
    public HostPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 返回主机地址
     *
     * @return 主机地址
     */
    public String getHost() {
        return host;
    }

    /**
     * 返回端口
     *
     * @return 端口
     */
    public int getPort() {
        return port;
    }


    //Object
    public String toString() {
        if (hostInfo == null) {
            hostInfo = host + ':' + port;
        }
        return hostInfo;
    }

    /**
     * Clone the InetSocketAddr.
     *
     * @return A new instance.
     */
    public Object clone() {
        HostPort hostPort = null;
        try {
            hostPort = (HostPort)super.clone();
        }
        catch (CloneNotSupportedException cnse) {
        }
        hostPort.host = host;
        hostPort.port = port;
        hostPort.id = id;
        hostPort.hostInfo = hostInfo;
        return hostPort;
    }

    /**
     * Hash Code.
     *
     * @return hash Code.
     */
    public int hashCode() {
        return port + ((host == null) ? 0 : host.hashCode());
    }

    /**
     * Equals.
     *
     * @param o
     * @return True if is the same address and port.
     */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o instanceof HostPort) {
            HostPort iap = (HostPort)o;
            return iap.port == port && StringUtil.equals(iap.host, host);
        }
        return false;
    }

    /**
     * 返回JVM的唯一标识
     *
     * @return JVM的唯一标识
     */
    public long getId() {
        if (id == 0) {
            id = toId(host, port);
        }
        return id;
    }

    /**
     * 将IP地址和端口变成长整数
     *
     * @param host IP地址
     * @param port 端口
     * @return 将IP地址和端口变成长整数
     */
    public static long toId(String host, int port) {
        int[] array = DataUtil.toInts(host, '.');
        int h;
        if (array != null && array.length == 4) {
            byte[] bytes = new byte[4];
            bytes[0] = (byte)array[0];
            bytes[1] = (byte)array[1];
            bytes[2] = (byte)array[2];
            bytes[3] = (byte)array[3];
            h = Binary.toInt(bytes);
        }
        else {
            throw new IllegalArgumentException("Invalid host:" + host);
        }
        return (((long)port) << 32) | (((long)h) & 0xFFFFFFFFL);
    }

    /**
     * 从标识中获取主机名
     *
     * @param id 标识
     */
    public static HostPort toHostPort(long id) {
        int h = (int)(id & 0xFFFFFFFFL);
        int port = (int)((id >> 32) & 0xFFFFFFFFL);
        StringBuilder sb = new StringBuilder();
        sb.append(((h >> 24) & 0xFF)).append('.');
        sb.append(((h >> 16) & 0xFF)).append('.');
        sb.append(((h >> 8) & 0xFF)).append('.');
        sb.append(((h) & 0xFF));
        return new HostPort(sb.toString(), port);
    }

    private static final String LOCAL_PORT = "cube.system.port";
    public static final int DEFAULT_PORT = 9168;
    public static final int MAX_TCP_PORT = 19168;

    private static HostPort local;

    static {
        InetAddress host = InetAddressUtil.getLocalAddress();
        int port = DataUtil.getInt(System.getProperty(LOCAL_PORT), -1);
        if (port == -1) { //没有定义，那么随机选择
            port = InetAddressUtil.getAvailablePort(host, DEFAULT_PORT, MAX_TCP_PORT);
        }
        //Try to listen
        local = new HostPort(host.getHostAddress(), port);
    }

    public static HostPort getLocal() {
        return local;
    }

    public static String getLocalHost() {
        return local.getHost();
    }

    public static int getLocalPort() {
        return local.getPort();
    }

    public static boolean isLocal(HostPort port) {
        return local.equals(port);
    }

    public static boolean isLocal(String host, int port) {
        return StringUtil.equals(local.host, host) && local.port == port;
    }

    @Override
    public void writeExternal(ObjectOutput oos) throws IOException {
        oos.writeUTF(host);
        oos.writeInt(port);
    }

    @Override
    public void readExternal(ObjectInput ois) throws IOException, ClassNotFoundException {
        this.host = ois.readUTF();
        this.port = ois.readInt();
    }
}
