package org.bolango.net.udp.scheme;

import java.io.IOException;
import java.net.DatagramPacket;

import org.bolango.jade.thread.ThreadPool;
import org.bolango.net.udp.scheme.ext.SimpleSchemeEndpoint;
import org.bolango.tools.nutlet.Nutlet;

/**
 * @author fengyun
 * @version 1.00 2005-1-7 23:43:10
 */

public class SchemeClientSocketTest extends Nutlet {

    static class SimpleSocket extends SchemeClientSocket {
        /**
         * 构造
         *
         * @param scheme Scheme
         */
        public SimpleSocket(int scheme) {
            super(scheme);
        }

        /**
         * 收到UDP报文之后调用
         *
         * @param packet UDP报文
         */
        public void receive(int scheme, DatagramPacket packet) throws IOException {
        }
    }

    private SimpleSocket socket = null;

    private SimpleSchemeEndpoint service = null;

    protected void setUp() throws Exception {
        super.setUp();

        service = new SimpleSchemeEndpoint();
        ThreadPool executor = new ThreadPool();
        executor.start();
        service.setExecutor(executor);
        service.setPort(9999);
        service.start();

        socket = new SimpleSocket(9999);
        socket.bind(service);
    }

    protected void tearDown() throws Exception {
        super.tearDown();

        service.stop();
        service.destroy();

        if (socket != null) {
            socket.close();
        }
    }

//    /**
//     * NullPointer
//     *
//     * @throws Exception
//     */
//    public void testSend0() throws Exception
//    {
//        try {
//            socket.send((DatagramPacket) null);
//            fail("IllegalArgument");
//        }
//        catch (IllegalArgumentException iae) {
//
//        }
//    }
//
//    /**
//     * 无效的状态
//     *
//     * @throws Exception
//     */
//    public void testSend1() throws Exception
//    {
//        try {
//            socket.send(new DatagramPacket(new byte[1], 1));
//            fail("IllegalArgumentException");
//        }
//        catch (IllegalArgumentException iae) {
//
//        }
//        try {
//            DatagramPacket[] packets = new DatagramPacket[]{new DatagramPacket(new byte[1], 1)};
//            socket.send(packets);
//            fail("IllegalArgumentException");
//        }
//        catch (IllegalArgumentException iae) {
//
//        }
//    }
//
//    /**
//     * 无效的状态
//     *
//     * @throws Exception
//     */
//    public void testSend2() throws Exception
//    {
//        try {
//            socket.send(new DatagramPacket(new byte[2], 2));
//            fail("IllegalArgumentException");
//        }
//        catch (IllegalArgumentException iae) {
//
//        }
//        DatagramPacket[] packets = new DatagramPacket[]{new DatagramPacket(new byte[2], 2)};
//        try {
//            socket.send(packets);
//            fail("IllegalArgumentException");
//        }
//        catch (IllegalArgumentException iae) {
//
//        }
//    }
//
//
//    /**
//     * 正常发送
//     *
//     * @throws Exception
//     */
//    public void testSend3() throws Exception
//    {
//        socket.send(new DatagramPacket(new byte[2], 2, InetAddress.getLocalHost(), 999));
//        DatagramPacket[] packets = new DatagramPacket[]{new DatagramPacket(new byte[2], 2, InetAddress.getLocalHost(), 999)};
//        socket.send(packets);
//    }

    /**
     * 测试构造之后的各种状态值是否按照预想的值
     *
     * @throws Exception
     */
    public void testSchemeDatagramSocket() throws Exception {
        SchemeClientSocket dispatcher = new SchemeClientSocket() {
            /**
             * 收到UDP报文之后调用
             *
             * @param packet UDP报文
             */
            public void receive(int scheme, DatagramPacket packet) {
            }
        };
        assertEquals(0, dispatcher.getScheme());

        SchemeClientSocket dispatcher2 = new SchemeClientSocket(9999) {
            /**
             * 收到UDP报文之后调用
             *
             * @param packet UDP报文
             */
            public void receive(int scheme, DatagramPacket packet) {
            }
        };
        assertEquals(9999, dispatcher2.getScheme());
//        assertEquals(false, dispatcher2.isMulti());
//        assertEquals(false, dispatcher2.isMatch(9999));
    }
}