package org.bolango.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import org.bolango.jade.Jade;
import org.bolango.net.InetAddressUtil;
import org.bolango.tools.nutlet.Nutlet;
import org.bolango.util.ArrayUtil;
import org.bolango.util.Config;

/**
 * @author fengyun
 * @version 1.00 2004-12-28 21:09:29
 */

public class UdpMulticastTest extends Nutlet
{
    private static final String MULTICAST_ADDRESS = "228.5.6.7";
    private static final int PORT = 9999;

    /**
     * 测试广播是否正常
     *
     * @throws Exception
     */
    public void testUdpMulticast() throws Exception
    {
        System.setProperty("org.bolango.net.udp.sender.daemon", "true");
        System.setProperty("org.bolango.net.udp.receiver.daemon", "true");
        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
        InetSocketAddress socketAddress = new InetSocketAddress(group, PORT);

        final UdpMulticast udp = new UdpMulticast();
        udp.setPort(PORT);
        udp.init();
        udp.joinGroup(group);
        udp.start();


        Thread thread = new Thread()
        {
            public void run()
            {
                final int size = 64 * 1024;
                DatagramPacket packet = new DatagramPacket(new byte[size], size);
                try {
                    udp.receive(packet);

                    receive(packet);
                }
                catch (IOException e) {
                }
            }
        };
        thread.start();


        byte[] bytes = "fengyun".getBytes();
        DatagramPacket packet = new DatagramPacket(bytes, 0, bytes.length, group, PORT);
        try {
            udp.send(socketAddress, bytes);
        }
        catch(IOException ioe) {
            if ("Network is unreachable".equals(ioe.getMessage())) {
                //Ignore
                return;
            }
            else {
                throw ioe;
            }
        }
        udp.send(packet);
        Jade.sleep(1000);

        udp.leaveGroup(group);

        udp.stop();
        udp.destroy();
    }

    /**
     * UDP报文
     *
     * @param packet UDP报文
     */
    public void receive(DatagramPacket packet)
    {
        try {
            byte[] bytes = packet.getData();
            assertNotNull(bytes);
            String str = new String(bytes, packet.getOffset(), packet.getLength());
            assertEquals("fengyun", str);
            System.out.println("Received:" + str);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * 测试添加到组和离开组是否正常
     *
     * @throws Exception
     */
    public void testJoinGroup() throws Exception
    {
        UdpMulticast multicast = new UdpMulticast();
//        multicast.setHost("127.0.0.1");
//        multicast.setReuseAddress(true);
//        multicast.setTimeToLive(0);
        multicast.setPort(PORT);
        multicast.init();
        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
        multicast.joinGroup(group);
        multicast.leaveGroup(group);
//        multicast.joinGroup(new InetSocketAddress(group, PORT));
//        multicast.leaveGroup(new InetSocketAddress(group, PORT));

        multicast.joinGroup(group);
        multicast.leaveGroup(group);
    }


    public void testLeaveGroup() throws Exception
    {
        UdpMulticast multicast = new UdpMulticast();
        try {
            multicast.leaveGroup((InetAddress) null);
            fail("Some exception");
        }
        catch (NullPointerException npe) {
            fail("NullPointerException");
        }
        catch (IllegalArgumentException iae) {

        }
//        try {
//            multicast.leaveGroup((InetSocketAddress) null);
//            fail("Some exception");
//        }
//        catch (NullPointerException npe) {
//            fail("NullPointerException");
//        }
//        catch (IllegalArgumentException iae) {
//
//        }
    }

    public void testSetTimeToLive1() throws Exception
    {
        UdpMulticast multicast = new UdpMulticast();
        multicast.setTimeToLive(0);

        try {
            multicast.setTimeToLive(-1);
            fail("Should have thrown an Exception");
        }
        catch (IllegalArgumentException ex) {
        }
        try {
            multicast.setTimeToLive(256);
            fail("Should have thrown an Exception");
        }
        catch (IllegalArgumentException ex) {
        }
    }

    public void testSetTimeToLive2() throws Exception
    {
        UdpMulticast multicast = new UdpMulticast();
        multicast.start();
        multicast.setTimeToLive(0);

        try {
            multicast.setTimeToLive(-1);
            fail("Should have thrown an Exception");
        }
        catch (IllegalArgumentException ex) {
        }
        try {
            multicast.setTimeToLive(256);
            fail("Should have thrown an Exception");
        }
        catch (IllegalArgumentException ex) {
        }

        multicast.stop();
        multicast.destroy();
    }

    public void testSetInterfaces1() throws Exception
    {
        UdpMulticast multicast = new UdpMulticast();
        multicast.setInterfaces(null);

        assertNotNull(multicast.getInterfaces());
    }

    public void testGetInterfaces1() throws Exception
    {
        UdpMulticast multicast = new UdpMulticast();
        NetworkInterface[] interfaces = multicast.getInterfaces();
        assertNotNull(interfaces);
        assertTrue(interfaces.length > 0);

        NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getByName("127.0.0.1"));
        assertNotNull(ni);
        assertTrue(ArrayUtil.search(interfaces, ni) >= 0);
    }

    public void testGetInterfaces2() throws Exception
    {
        UdpMulticast multicast = new UdpMulticast();
        multicast.setInterfaces("127.0.0.1");
        NetworkInterface[] interfaces = multicast.getInterfaces();
        assertNotNull(interfaces);
        assertTrue(interfaces.length == 1);

        NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getByName("127.0.0.1"));
        assertNotNull(ni);
        assertEquals(interfaces[0], ni);
    }

    public void testGetInterfaces3() throws Exception
    {
        UdpMulticast multicast = new UdpMulticast();
        try {
            multicast.setInterfaces("no_such_host");
            fail("Unkown host");
        }
        catch (Exception ex) {
        }
    }

    public void testCreateSocket() throws Exception
    {
        UdpMulticast multicast = new UdpMulticast();
        multicast.init();
        DatagramSocket socket = multicast.createSocket();
        assertNotNull(socket);
        assertTrue(socket instanceof MulticastSocket);
    }

    public void testInitSocket() throws Exception
    {
        UdpMulticast multicast = new UdpMulticast();
        multicast.init();
        MulticastSocket socket = (MulticastSocket) multicast.createSocket();
        assertEquals(1, socket.getTimeToLive());
        multicast.setTimeToLive(40);
        multicast.initSocket(socket);
        assertEquals(40, socket.getTimeToLive());
    }

    /**
     * 测试组播对组播
     *
     * @throws Exception
     */
    public void testMulticast() throws Exception
    {
        UdpMulticast multicast1 = new UdpMulticast();
        multicast1.setHost("127.0.0.1");
        multicast1.setPort(PORT);

        UdpMulticast multicast2 = new UdpMulticast();
        multicast2.setHost("127.0.0.1");
        multicast2.setPort(PORT);

        final int port = 8888;
        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
//        final InetSocketAddress isa = new InetSocketAddress(group, PORT);

        multicast1.start();
        multicast1.joinGroup(group);
        multicast2.start();
        multicast2.joinGroup(group);

        final InetSocketAddress receiver = new InetSocketAddress(group, PORT);
        InetAddress[] addrs = InetAddressUtil.getNetInterfaces();
        Thread[] threads = new Thread[addrs.length];
        final MulticastSocket[] msockets = new MulticastSocket[addrs.length];
        for (int i = 0; i < addrs.length; i++) {
            final MulticastSocket msocket = new MulticastSocket(new InetSocketAddress(addrs[i], port));
            msockets[i] = msocket;
            threads[i] = new Thread()
            {
                public void run()
                {
                    try {
                        Thread.sleep(500);
                        msocket.send(new DatagramPacket("fengyun".getBytes(), 7, receiver));
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
        }

        byte[] buf1 = new byte[1024];
        DatagramPacket receive1 = new DatagramPacket(buf1, 1024);
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        multicast1.receive(receive1);

        String str1 = new String(buf1, receive1.getOffset(), receive1.getLength());
        System.out.println("String:" + str1);
        assertEquals("fengyun", str1);

        byte[] buf2 = new byte[1024];
        DatagramPacket receive2 = new DatagramPacket(buf2, 1024);
        multicast2.receive(receive2);

        String str2 = new String(buf1, receive1.getOffset(), receive1.getLength());
        System.out.println("String:" + str2);
        assertEquals("fengyun", str2);

        multicast1.stop();
        multicast1.destroy();
        multicast2.stop();
        multicast2.destroy();
        for (int i = 0; i < msockets.length; i++) {
            msockets[i].close();
        }
    }
}