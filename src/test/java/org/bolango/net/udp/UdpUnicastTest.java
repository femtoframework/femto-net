package org.bolango.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.bolango.jade.Jade;
import org.bolango.jade.LifecycleMBean;
import org.bolango.tools.nutlet.Nutlet;
import org.junit.Ignore;
import org.junit.Test;

import static junit.Assert.assertEquals;
import static junit.Assert.assertNotNull;
import static junit.Assert.fail;

/**
 * Datagram Unicast 测试
 *
 * @author fengyun
 * @version 1.00 2004-12-28 19:45:50
 */
public class UdpUnicastTest {
    @Test
    @Ignore
    public void testUdpUnicast() throws Exception {
        System.setProperty("org.bolango.net.udp.sender.daemon", "true");
        System.setProperty("org.bolango.net.udp.receiver.daemon", "true");

        final UdpUnicast udp = new UdpUnicast();
        udp.setPort(9999);
        udp.init();
        udp.start();

        final int[] len = new int[1];
        Thread thread = new Thread() {
            public void run() {
                final int size = 64 * 1024;
                DatagramPacket packet = new DatagramPacket(new byte[size], size);
                try {
                    udp.receive(packet);

                    byte[] data = packet.getData();
                    len[0] = packet.getLength();
                    assertEquals("fengyun", new String(data, 0, 7));
                }
                catch (IOException e) {
                }
            }
        };
        thread.start();

        InetAddress address = InetAddress.getLocalHost();
        InetSocketAddress socketAddress = new InetSocketAddress(address, 9999);
        udp.send(socketAddress, "fengyun".getBytes());
        Jade.sleep(1000);

        assertEquals(7, len[0]);
        udp.stop();
        udp.destroy();
    }

    /**
     * UDP报文
     *
     * @param packet UDP报文
     */
    public void receive(DatagramPacket packet) {
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

//    /**
//     * 测试在没有启动状态下调用发送
//     *
//     * @throws Exception
//     */
//    public void testSend1() throws Exception
//    {
//        try {
//            new UdpUnicast().send(null, 999, new byte[99]);
//            fail("Should have thrown an Exception");
//        }
//        catch (Exception ex) {
//        }
//    }

    /**
     * 测试给定Null地址
     *
     * @throws Exception
     */
    @Ignore
    @Test
    public void testSend2() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        unicast.setPort(9999);
        unicast.start();
        //Null 表示本地地址
        unicast.send(null, 9999, new byte[99]);

        unicast.stop();
        unicast.destroy();
    }

    /**
     * 测试给定错误的数据
     *
     * @throws Exception
     */
    public void testSend3() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        unicast.start();
        try {
            unicast.send(null, 999, null);
            fail("Should have thrown an Exception");
        }
        catch (NullPointerException ex) {
            fail("Should thrown a not NullPointerException");
        }
        catch (Exception e) {

        }
        unicast.stop();
        unicast.destroy();
    }

    /**
     * 测试给定错误的数据(DatagramSocket null)
     *
     * @throws Exception
     */
    public void testSend4() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        try {
            unicast.send((DatagramPacket)null);
            fail("Should have thrown an Exception");
        }
        catch (NullPointerException ex) {
            fail("Should thrown a not NullPointerException");
        }
        catch (IllegalArgumentException e) {
        }
        catch (Exception e) {
            fail("Unknown exception:" + e);
        }
    }

    /**
     * 测试给定错误的数据(DatagramSocket null)
     *
     * @throws Exception
     */
    public void testSend5() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        try {
            unicast.send((DatagramPacket[])null);
            fail("Should have thrown an Exception");
        }
        catch (NullPointerException ex) {
            fail("Should thrown a not NullPointerException");
        }
        catch (IllegalArgumentException e) {
        }
        catch (Exception e) {
            fail("Unknown exception:" + e);
        }
    }

    /**
     * 设置主机(null)
     *
     * @throws Exception
     */
    public void testSetHost() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        unicast.setHost(null);

        assertEquals(InetAddress.getLocalHost(), unicast.getAddress());
        assertEquals(InetAddress.getLocalHost().getHostAddress(), unicast.getHost());
    }

//    /**
//     * 设置不存在的主机地址
//     *
//     * @throws Exception
//     */
//    public void testSetHost2() throws Exception {
//        if (Jade.isWindows()) {
//            UdpUnicast unicast = new UdpUnicast();
//            try {
//                unicast.setHost("no_such_host");
//                fail("No such host");
//            }
//            catch (Exception ex) {
//
//            }
//        }
//    }

    /**
     * 判断返回主机地址是否符合预想的
     *
     * @throws Exception
     */
    public void testGetHost1() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        unicast.setHost("*");

        assertEquals(InetAddress.getByName("0.0.0.0"), unicast.getAddress());
        assertEquals("0.0.0.0", unicast.getHost());
    }

    /**
     * 返回127.0.0.1对应的主机地址
     *
     * @throws Exception
     */
    public void testGetHost2() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        unicast.setHost("127.0.0.1");

        assertEquals(InetAddress.getByName("127.0.0.1"), unicast.getAddress());
        assertEquals("127.0.0.1", unicast.getHost());
    }

    /**
     * 测试GetSocketAddress
     *
     * @throws Exception
     */
    public void testGetSocketAddress() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        unicast.setHost("127.0.0.1");
        unicast.setPort(8888);

        assertEquals(InetAddress.getByName("127.0.0.1"), unicast.getAddress());
        assertEquals("127.0.0.1", unicast.getHost());
        assertEquals(new InetSocketAddress("127.0.0.1", 8888), unicast.getSocketAddress());

        unicast.start();
        assertEquals(new InetSocketAddress("127.0.0.1", 8888), unicast.getSocketAddress());

        unicast.stop();
        unicast.destroy();
    }

    /**
     * 测试返回InetAddress
     *
     * @throws Exception
     */
    public void testGetAddress1() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        unicast.setHost("127.0.0.1");

        assertEquals(InetAddress.getByName("127.0.0.1"), unicast.getAddress());
        assertEquals("127.0.0.1", unicast.getHost());
    }

    /**
     * 测试返回InetAddress(0.0.0.0)
     *
     * @throws Exception
     */
    public void testGetAddress2() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        unicast.setHost("0.0.0.0");

        assertEquals(InetAddress.getByName("0.0.0.0"), unicast.getAddress());
        assertEquals("0.0.0.0", unicast.getHost());
    }

    /**
     * 测试返回InetAddress(null)
     *
     * @throws Exception
     */
    public void testGetAddress3() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        unicast.setHost(null);

        assertEquals(InetAddress.getLocalHost(), unicast.getAddress());
    }

    /**
     * * 测试返回InetAddress(*)
     *
     * @throws Exception
     */
    public void testGetAddress4() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        unicast.setHost("*");

        assertEquals(InetAddress.getByName("0.0.0.0"), unicast.getAddress());
    }

    /**
     * 测试Receive方法
     *
     * @throws Exception
     */
    @Ignore
    @Test
    public void testReceive1() throws Exception {
        final UdpUnicast udp = new UdpUnicast();
        udp.setPort(9999);
        udp.start();

        Thread thread = new Thread() {
            public void run() {
                final int size = 64 * 1024;
                DatagramPacket packet = new DatagramPacket(new byte[size], size);
                try {
                    udp.receive(packet);

                    assertEquals(0, packet.getLength());
                }
                catch (IOException e) {
                }
            }
        };
        thread.start();

        Thread thread2 = new Thread() {
            public void run() {
                try {
                    udp.receive(new DatagramPacket(new byte[0], 0));
                }
                catch (Exception ex) {
                }
            }
        };
        thread2.start();

        try {
            udp.receive(null);
            fail("Null");
        }
        catch (NullPointerException ex) {
            fail("NullPointerException");
        }
        catch (Exception ex) {
        }
        udp.destroy();
    }

//    /**
//     * 在没有初始化的情况下调用GetQueueSize，应该返回0
//     *
//     * @throws Exception
//     */
//    public void testGetQueueSize1() throws Exception
//    {
//        UdpUnicast unicast = new UdpUnicast();
//        assertEquals(unicast.getQueueSize(), 0);
//    }
//
//    /**
//     * 调用初始化但是没有调用启动应该队列中有报文
//     *
//     * @throws Exception
//     */
//    public void testGetQueueSize2() throws Exception
//    {
//        UdpUnicast unicast = new UdpUnicast();
//        unicast.init();
//        unicast.send(new DatagramPacket(new byte[0], 0));
//        assertEquals(1, unicast.getQueueSize());
//    }
//
//    /**
//     * 调用启动之后应该队列中没有报文
//     *
//     * @throws Exception
//     */
//    public void testGetQueueSize3() throws Exception
//    {
//        UdpUnicast unicast = new UdpUnicast();
//        unicast.start();
//        unicast.send(new DatagramPacket(new byte[0], 0));
//        Jade.sleep(1000);
//        assertEquals(0, unicast.getQueueSize());
//
//        unicast.stop();
//        unicast.destroy();
//    }

    public void testSetPort() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        try {
            unicast.setPort(-4);
            fail("Should have thrown an Exception");
        }
        catch (IllegalArgumentException ex) {
        }
    }

    /**
     * 设置发送Buffer大小
     *
     * @throws Exception
     */
    public void testSetSendBufferSize1() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        try {
            unicast.setSendBufferSize(-4);
            fail("Should have thrown an Exception");
        }
        catch (IllegalArgumentException ex) {
        }
    }

    /**
     * 设置发送Buffer大小
     *
     * @throws Exception
     */
    public void testSetSendBufferSize2() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        try {
            unicast.setSendBufferSize(0);
            fail("Should have thrown an Exception");
        }
        catch (IllegalArgumentException ex) {
        }
    }

    /**
     * 设置发送Buffer大小
     *
     * @throws Exception
     */
    public void testSetSendBufferSize3() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        try {
            unicast.setSendBufferSize(1024);
            fail("Should have thrown an Exception");
        }
        catch (IllegalArgumentException ex) {
        }
    }

    /**
     * 设置发送Buffer大小
     *
     * @throws Exception
     */
    public void testSetSendBufferSize4() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        unicast.setSendBufferSize(16 * 1024);
    }

    /**
     * 设置发送Buffer大小
     *
     * @throws Exception
     */
    public void testSetSendBufferSize5() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        unicast.start();
        unicast.setSendBufferSize(16 * 1024);
        unicast.stop();
        unicast.destroy();
    }

    /**
     * 设置ReceiveBuffer大小
     *
     * @throws Exception
     */
    public void testSetReceiveBufferSize1() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        try {
            unicast.setReceiveBufferSize(-4);
            fail("Should have thrown an Exception");
        }
        catch (IllegalArgumentException ex) {
        }
    }

    /**
     * 设置ReceiveBuffer大小
     *
     * @throws Exception
     */
    public void testSetReceiveBufferSize2() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        try {
            unicast.setReceiveBufferSize(0);
            fail("Should have thrown an Exception");
        }
        catch (IllegalArgumentException ex) {
        }
    }

    /**
     * 设置ReceiveBuffer大小
     *
     * @throws Exception
     */
    public void testSetReceiveBufferSize3() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        try {
            unicast.setReceiveBufferSize(1024);
            fail("Should have thrown an Exception");
        }
        catch (IllegalArgumentException ex) {
        }
    }

    /**
     * 设置ReceiveBuffer大小
     *
     * @throws Exception
     */
    public void testSetReceiveBufferSize4() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        unicast.setReceiveBufferSize(16 * 1024);
        unicast.start();
        unicast.setReceiveBufferSize(16 * 1024);
        unicast.stop();
        unicast.destroy();
    }

    public void testSetReuseAddress() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        unicast.setReuseAddress(true);
        unicast.start();
        unicast.setReuseAddress(false);
        unicast.stop();
        unicast.destroy();
    }

//    public void testSetSoTimeout() throws Exception
//    {
//        UdpUnicast unicast = new UdpUnicast();
//        unicast.setSoTimeout(500);
//        unicast.start();
//        long start = System.currentTimeMillis();
//        unicast.setSoTimeout(1000);
//        //应该在一秒之内释放锁
//        int time = (int) (System.currentTimeMillis() - start);
//        assertTrue(time < 1000);
//
//        unicast = new UdpUnicast();
//        try {
//            unicast.setSoTimeout(-1);
//            fail("Should have thrown an Exception");
//        }
//        catch (IllegalArgumentException ex) {
//        }
//        unicast.stop();
//        unicast.destroy();
//    }
//
//    public void testGetSoTimeout() throws Exception
//    {
//        UdpUnicast unicast = new UdpUnicast();
//        unicast.setSoTimeout(500);
//        unicast.start();
//        unicast.setSoTimeout(1000);
//        //应该在一秒之内释放锁
//        assertEquals(1000, unicast.getSoTimeout());
//        unicast.stop();
//        unicast.destroy();
//    }

    /**
     * 测试生命周期
     *
     * @throws Exception
     */
    public void testLifeCycle() throws Exception {
        UdpUnicast unicast = new UdpUnicast();
        unicast.start();
        assertNotNull(unicast.getHost());
        assertNotNull(unicast.getAddress());
        assertNotNull(unicast.getSocketAddress());

        assertEquals(unicast.getStatus(), LifecycleMBean.STATUS_STARTED);

        unicast.stop();
        unicast.destroy();
        assertNotNull(unicast.getHost());
        assertNotNull(unicast.getAddress());
        assertNotNull(unicast.getSocketAddress());
        assertEquals(unicast.getStatus(), LifecycleMBean.STATUS_DESTROYED);
    }
}