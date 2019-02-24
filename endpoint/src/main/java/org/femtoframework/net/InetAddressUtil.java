package org.femtoframework.net;

import org.femtoframework.io.CodecUtil;
import org.femtoframework.io.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.*;

/**
 * InetAddress Util
 *
 * @author fengyun
 * @version 1.00 2004-9-30 15:17:46
 */
public class InetAddressUtil
{

    private static InetAddress localAddress;
    private static InetAddress localHost;
    private static InetAddress[] netInterfaces;
    public static InetAddress ZERO;
    private static Map<String, InetAddress> ip2Address = null;

    private static final String KEY_LOCAL_HOST = "cube.system.address";

    static {
        try {
            ZERO = InetAddress.getByName("0.0.0.0");

            initInterfaces();

            localHost = InetAddress.getByName("127.0.0.1");

            String host = System.getProperty(KEY_LOCAL_HOST);
            if (host == null) {
                try {
                    localAddress = InetAddress.getLocalHost();
                }
                catch (Exception e) {
                    localAddress = localHost;
                }
            }
            else {
                localAddress = InetAddress.getByName(host);
                if (!isLocalAddress(localAddress)) {
                    System.err.println("The ip address:" + host
                                       + " is not a valid network interface.");
                    System.exit(-1);
                }
            }
        }
        catch (Exception e) {
            System.err.println("Init org.femtoframework.net.InetAddressUtil exception:" + e.getMessage());
        }
    }

    private static void initInterfaces()
        throws Exception
    {
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        List<InetAddress> list = new ArrayList<InetAddress>();
        while (en.hasMoreElements()) {
            NetworkInterface ni = en.nextElement();
            Enumeration<InetAddress> addressEnumeration = ni.getInetAddresses();
            while(addressEnumeration.hasMoreElements()) {
                list.add(addressEnumeration.nextElement());
            }
        }
        netInterfaces = new InetAddress[list.size()];
        list.toArray(netInterfaces);

        ip2Address = new HashMap<String, InetAddress>();

        for (int i = 0; i < netInterfaces.length; i++) {
            ip2Address.put(netInterfaces[i].getHostAddress(), netInterfaces[i]);
        }
    }

    /**
     * 根据IP地址返回地址
     *
     * @param ip IP地址
     */
    public static InetAddress getAddress(String ip)
    {
        return ip2Address.get(ip);
    }

    /**
     * 返回所有网卡上的所有绑定IP，这个方法只在JDK1.4下有效
     */
    public static InetAddress[] getNetInterfaces()
    {
        return netInterfaces;
    }

    /**
     * 本地地址，跟HostPort中返回的地址一样
     */
    public static InetAddress getLocalAddress()
    {
        if (localAddress != null) {
            return localAddress;
        }

        if (netInterfaces == null) {
            return localHost;
        }
        for (int i = 0; i < netInterfaces.length; i++) {
            String address = netInterfaces[i].getHostAddress();
            if (!"127.0.0.1".equals(address)) {
                return localAddress = netInterfaces[i];
            }
        }
        return localHost;
    }

    /**
     * 判断是否属于本地的IP地址
     *
     * @param address 地址
     */
    public static boolean isLocalAddress(InetAddress address)
    {
        if (address == null) {
            return false;
        }
        else if (localHost.equals(address) || ZERO.equals(address)) {
            return true;
        }
        if (netInterfaces == null) {
            return true;
        }
        for (int i = 0; i < netInterfaces.length; i++) {
            if (netInterfaces[i].equals(address)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 输出InetSocketAddress地址
     *
     * @param out     输出流
     * @param address 地址
     * @throws IOException
     */
    public static void writeSocketAddress(OutputStream out, InetSocketAddress address)
        throws IOException
    {
        if (address == null) {
            CodecUtil.writeBoolean(out, true);
            return;
        }
        else {
            CodecUtil.writeBoolean(out, false);
        }

        InetAddress addr = address.getAddress();
        byte[] bytes = addr.getAddress();
        int port = address.getPort();
        CodecUtil.writeUnsignedByte(out, bytes.length);
        out.write(bytes);
        CodecUtil.writeUnsignedShort(out, port);
    }

    /**
     * 读入一个InetSocketAddress
     *
     * @param in 输入流
     * @return
     * @throws IOException
     */
    public static InetSocketAddress readSocketAddress(InputStream in)
        throws IOException
    {
        boolean isNull = CodecUtil.readBoolean(in);
        if (isNull) {
            return null;
        }
        int s = CodecUtil.readUnsignedByte(in);
        byte[] bytes = new byte[s];
        IOUtil.readFully(in, bytes);
        int port = CodecUtil.readUnsignedShort(in);
        InetAddress addr = InetAddress.getByAddress(bytes);
        return new InetSocketAddress(addr, port);
    }

    /**
     * 选定一个可以侦听的端口
     *
     * @param address 侦听的端口地址
     * @param minPort 最小可侦听端口
     * @param maxPort 最大可侦听端口
     */
    public static int getAvailablePort(InetAddress address, int minPort, int maxPort) {
        //Try to listen
        int port = minPort;
        try {
            ServerSocket socket = new ServerSocket(port, 1, address);
            socket.close();
            return port;
        }
        catch (Exception e) {
            //Ignore
        }
        int hop = 0;
        Random random = new Random();
        do {
            hop += random.nextInt(maxPort - minPort);
            port = minPort + hop;
            try {
                ServerSocket socket = new ServerSocket(port, 1, address);
                socket.close();
                return port;
            }
            catch (Exception e) {
                //Ignore
            }
        }
        while (port < maxPort);
        throw new IllegalStateException("No valid port from:" + minPort + "-" + maxPort);
    }
}
