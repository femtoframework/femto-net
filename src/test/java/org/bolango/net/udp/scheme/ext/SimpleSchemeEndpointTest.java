package org.bolango.net.udp.scheme.ext;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;

import org.bolango.lang.Binary;
import org.bolango.net.udp.scheme.SchemeClientSocket;
import org.bolango.net.udp.scheme.SchemePacket;
import org.bolango.tools.nutlet.Nutlet;

/**
 * @author fengyun
 * @version 1.00 2004-12-28 23:06:20
 */

public class SimpleSchemeEndpointTest extends Nutlet {
    public void testGetSchemes() throws Exception {
        SimpleSchemeEndpoint service = new SimpleSchemeEndpoint();
        service.start();
        SocketAddress address = service.getSocketAddress();
        new SimpleSocket(8888).bind(service);
        new SimpleSocket(7777).bind(service);
        new SimpleSocket(9999) {
            /**
             * 报文处理程序（方便服务器端程序扩展）
             *
             * @param packet 接收到的报文
             */
            public void onReceived(SchemePacket packet) {
            }
        }.bind(service);

        assertNotNull(service.getSocket(8888));
        int[] schemes = service.getSchemes();
        assertNotNull(schemes);
        assertEquals(3, schemes.length);
        assertNotNull(service.getSocket(schemes[0]));
        assertNotNull(service.getSocket(schemes[1]));
        assertNotNull(service.getSocket(schemes[2]));

        service.destroy();
    }

    /**
     * 测试正确的Scheme（固定分发器）
     *
     * @throws Exception
     */
    public void testReceiveNext4() throws Exception {
        SimpleSchemeEndpoint service = new SimpleSchemeEndpoint();
        service.setPort(9999);

        service.start();

        SocketAddress address = service.getSocketAddress();
        final SchemePacket[] receivedPacket = new SchemePacket[1];
        new SimpleSocket(7777) {
            /**
             * 报文处理程序（方便服务器端程序扩展）
             *
             * @param packet 接收到的报文
             */
            public void onReceived(SchemePacket packet) {
                assertNotNull(packet);
                receivedPacket[0] = packet;
            }
        }.bind(service);

        DatagramSocket sender = new DatagramSocket(8888);
        byte[] data = new byte[888];
        Binary.appendUnsignedShort(data, 0, 7777);
        DatagramPacket packet = new DatagramPacket(data, 0, 888,
            InetAddress.getLocalHost(), 9999);
        sender.send(packet);
        sender.close();

        Thread.sleep(1000);
        service.start();
        Thread.sleep(1000);
        assertNotNull(receivedPacket[0]);
        service.stop();
        service.destroy();
    }

    /**
     * 测试发送无效的Scheme
     *
     * @throws Exception
     */
    public void testReceiveNext5() throws Exception {
        SimpleSchemeEndpoint service = new SimpleSchemeEndpoint();
        service.setPort(9999);

        service.start();
        SocketAddress address = service.getSocketAddress();
        final SchemePacket[] receivedPacket = new SchemePacket[1];
        new SimpleSocket(7777) {
            /**
             * 报文处理程序（方便服务器端程序扩展）
             *
             * @param packet 接收到的报文
             */
            public void onReceived(SchemePacket packet) {
                assertNotNull(packet);
                receivedPacket[0] = packet;
            }
        }.bind(service);

        DatagramSocket sender = new DatagramSocket(8888);
        byte[] data = new byte[888];
        Binary.appendUnsignedShort(data, 0, 8888);
        DatagramPacket packet = new DatagramPacket(data, 0, 888,
            InetAddress.getLocalHost(), 9999);
        sender.send(packet);
        sender.close();

        Thread.sleep(1000);
        assertNull(receivedPacket[0]);
        service.stop();
        service.destroy();
    }

//
//    /**
//     * 测试发送正确的数据（多个接收者）
//     *
//     * @throws Exception
//     */
//    public void testReceiveNext6() throws Exception
//    {
//        SchemeUdpMulticast multicast = new SchemeUdpMulticast();
//        multicast.setPort(9999);
//
//        final DatagramPacket[] receivedPacket = new DatagramPacket[2];
//        multicast.start();
//        SocketAddress address = multicast.getSocketAddress();
//        new SimpleSocket(7777)
//        {
//            /**
//             * UDP报文
//             *
//             * @param packet UDP报文
//             */
//            public void receive(int scheme, DatagramPacket packet)
//            {
//                assertNotNull(packet);
//                receivedPacket[0] = packet;
//            }
//        }.bind(address);
//        new AllReceiver()
//        {
//            /**
//             * UDP报文
//             *
//             * @param packet UDP报文
//             */
//            public void receive(int scheme, DatagramPacket packet)
//            {
//                assertNotNull(packet);
//                receivedPacket[1] = packet;
//            }
//        }.bind(address);
//
//        DatagramSocket sender = new DatagramSocket(8888);
//        byte[] data = new byte[888];
//        Binary.appendUnsignedShort(data, 0, 7777);
//        DatagramPacket packet = new DatagramPacket(data, 0, 888,
//            InetAddress.getLocalHost(), 9999);
//        sender.send(packet);
//
//        Thread.sleep(1000);
//        multicast.receive(new DatagramPacket(new byte[64 * 1024], 64 * 1024));
//        assertNotNull(receivedPacket[0]);
//        assertNotNull(receivedPacket[1]);
//
//        receivedPacket[0] = null;
//        receivedPacket[1] = null;
//
//        Binary.appendUnsignedShort(data, 0, 9999);
//        sender.send(packet);
//
//        Thread.sleep(1000);
//        multicast.receive(new DatagramPacket(new byte[64 * 1024], 64 * 1024));
//        assertNull(receivedPacket[0]);
//        assertNotNull(receivedPacket[1]);
//
//        sender.close();
//        multicast.stop();
//        multicast.destroy();
//    }
//
//    /**
//     * 测试一个接收者异常的时候，是否能够继续
//     *
//     * @throws Exception
//     */
//    public void testReceiveNext7() throws Exception
//    {
//        SchemeUdpMulticast multicast = new SchemeUdpMulticast();
//        multicast.setPort(9999);
//
//        final DatagramPacket[] receivedPacket = new DatagramPacket[2];
//        multicast.start();
//        SocketAddress address = multicast.getSocketAddress();
//        new SimpleSocket(7777)
//        {
//            /**
//             * UDP报文
//             *
//             * @param packet UDP报文
//             */
//            public void receive(int scheme, DatagramPacket packet)
//            {
//                assertNotNull(packet);
//                receivedPacket[0] = packet;
//            }
//        }.bind(address);
//        new AllReceiver()
//        {
//            /**
//             * UDP报文
//             *
//             * @param packet UDP报文
//             */
//            public void receive(int scheme, DatagramPacket packet)
//            {
//                throw new IllegalArgumentException("Invalid packet");
//            }
//        }.bind(address);
//
//        DatagramSocket sender = new DatagramSocket(8888);
//        byte[] data = new byte[888];
//        Binary.appendUnsignedShort(data, 0, 7777);
//        DatagramPacket packet = new DatagramPacket(data, 0, 888,
//            InetAddress.getLocalHost(), 9999);
//        sender.send(packet);
//
//        Thread.sleep(1000);
//        multicast.receive(new DatagramPacket(new byte[64 * 1024], 64 * 1024));
//        assertNotNull(receivedPacket[0]);
//        assertNull(receivedPacket[1]);
//
//        sender.close();
//        multicast.stop();
//        multicast.destroy();
//    }

    static class SimpleSocket
        extends SchemeClientSocket {
        public SimpleSocket(int scheme) {
            super(scheme);
        }

        /**
         * 报文处理程序（方便服务器端程序扩展）
         *
         * @param packet 接收到的报文
         */
        public void onReceived(SchemePacket packet)
            throws IOException {
            System.out.println("Packet Received: " + packet);
        }
    }

    /**
     * 添加Dispatcher
     *
     * @throws Exception
     */
    public void testAddSocket1() throws Exception {
        SimpleSchemeEndpoint service = new SimpleSchemeEndpoint();
        service.addSocket(new SimpleSocket(1110));

        assertNotNull(service.getSocket(1110));
    }

    /**
     * 添加空的Dispatcher
     *
     * @throws Exception
     */
    public void testAddSocket2() throws Exception {
        SimpleSchemeEndpoint service = new SimpleSchemeEndpoint();
        try {
            service.addSocket(null);
            fail("Illegal Argument");
        }
        catch (IllegalArgumentException iae) {
        }
    }

    /**
     * 添加无效Scheme的Dispatcher
     *
     * @throws Exception
     */
    public void testAddSocket3() throws Exception {
        SimpleSchemeEndpoint service = new SimpleSchemeEndpoint();

        try {
            service.addSocket(new SimpleSocket(1110));
            service.addSocket(new SimpleSocket(1110));
            fail("IllegalStateException");
        }
        catch (IllegalStateException iae) {
        }
    }

    /**
     * 添加重复Scheme的Dispatcher
     *
     * @throws Exception
     */
    public void testAddSocket4() throws Exception {
        SimpleSchemeEndpoint service = new SimpleSchemeEndpoint();

        try {
            service.addSocket(new SimpleSocket(99999999));
            fail("Illegal Argument");
        }
        catch (IllegalArgumentException iae) {
        }
    }

    public void testGetSocket() throws Exception {
        SimpleSchemeEndpoint service = new SimpleSchemeEndpoint();
        service.addSocket(new SimpleSocket(8888));

        assertNotNull(service.getSocket(8888));
        assertNull(service.getSocket(0));

        service.destroy();
    }

    public void testRemoveSocket() throws Exception {
        SimpleSchemeEndpoint service = new SimpleSchemeEndpoint();
        service.addSocket(new SimpleSocket(8888));

        assertNotNull(service.getSocket(8888));
        assertNotNull(service.removeSocket(8888));
        assertNull(service.getSocket(8888));
        assertNull(service.removeSocket(0));
        service.destroy();
    }
}