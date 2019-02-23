package org.bolango.net.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * @author fengyun
 * @version 1.00 2005-12-7 13:17:53
 */
public class UdpClient
{
    public static void main(String[] args) throws Exception
    {
        String addr = null;
        int port = 9777;

        if (args.length >= 1) {
            addr = args[0];
        }
        if (args.length >= 2) {
            port = Integer.parseInt(args[1]);
        }

        DatagramSocket ds = new DatagramSocket(9888);
        byte[] bytes = "fengyun".getBytes();
        DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
        dp.setSocketAddress(new InetSocketAddress(addr, port));
        ds.send(dp);

        DatagramPacket receivePacket = new DatagramPacket(new byte[64], 64);
        ds.receive(receivePacket);
        byte[] data = receivePacket.getData();
        System.out.println("Received data:" + new String(data, 0, receivePacket.getLength()));
    }
}
