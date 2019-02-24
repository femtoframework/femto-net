package org.femtoframework.net.nio.cmd;

import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import org.femtoframework.nio.ByteBufferPool;
import org.femtoframework.util.ArrayUtil;
import org.femtoframework.util.nutlet.NutletUtil;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 测试CommandContext
 * <p/>
 * todo 没有测试真实的连接情况
 *
 * @author fengyun
 * @version 1.00 2005-1-2 23:14:53
 */

public class CommandContextTest
{

    @Test
    public void testInit0() throws Exception
    {
        CommandContext context = new CommandContext();
        try {
            context.init((SocketChannel) null);
            fail("IllegalArgument");
        }
        catch (IllegalArgumentException iae) {
        }
    }

    @Test
    public void testInit1() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        context.close();
        serverSocket.close();
    }

    /**
     * 测试读取数据
     *
     * @throws Exception
     */
    @Test
    public void testRead0() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        try {
            context.read();
            fail("None connected");
        }
        catch (Exception ioe) {

        }

        context.close();
        serverSocket.close();
    }

    /**
     * 测试输出Buffer
     *
     * @throws Exception
     */
    @Test
    public void testWrite0() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        try {
            context.write();
            fail("None connected");
        }
        catch (Exception ioe) {

        }

        context.close();
        serverSocket.close();
    }

    /**
     * 测试Buffer倒转
     *
     * @throws Exception
     */
    @Test
    public void testFlip() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        byte[] bytes = NutletUtil.getBytes(256);
        context.print(bytes);
        int size = context.writeBuff.position();
        assertEquals(bytes.length, size);
        context.flip();
        assertEquals(0, context.writeBuff.position());

        context.close();
        serverSocket.close();
    }

    @Test
    public void testReadFully() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        byte[] bytes = NutletUtil.getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        context.readFully(bais);

        int size = context.writeBuff.position();
        assertEquals(bytes.length > context.getWriteBuffCapacity() ? context.getWriteBuffCapacity() : bytes.length,
            size);
        byte[] array = context.writeBuff.array();
        assertMatches(bytes, 0, array, 0, size);

        context.close();
        serverSocket.close();
    }

    @Test
    public void testFlush0() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        try {
            context.flush();
            fail("None connected");
        }
        catch (Exception ioe) {

        }

        context.close();
        serverSocket.close();
    }

    @Test
    public void testOnBufferWritten() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        context.onBufferWritten();

        context.close();
        serverSocket.close();
    }

    /**
     * 测试获取下一个命令
     *
     * @throws Exception
     */
    public void testNextCommand0() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        try {
            context.nextCommand();
            fail("None connected");
        }
        catch (Exception ioe) {

        }

        context.close();
        serverSocket.close();
    }

    /**
     * 测试返回命令数据
     *
     * @throws Exception
     */
    @Test
    public void testGetCommandBytes() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        byte[] array = context.getCommandBytes();
        assertEquals(context.getReadBuffCapacity(), array.length);

        context.close();
        serverSocket.close();
    }

    /**
     * 测试返回命令长度
     *
     * @throws Exception
     */
    @Test
    public void testGetCommandLength() throws Exception
    {
        CommandContext context = new CommandContext();
        assertEquals(0, context.getCommandLength());
    }

    @Test
    public void testPrintSpace() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        context.printSpace();
        int size = context.writeBuff.position();
        assertEquals(1, size);
        byte[] array = context.writeBuff.array();
        assertEquals(' ', array[0]);

        context.close();
        serverSocket.close();
    }

    /**
     * 测试输出一个字节
     *
     * @throws Exception
     */
    @Test
    public void testPrint0() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        byte b = NutletUtil.getByte();
        context.print(b);
        int size = context.writeBuff.position();
        assertEquals(1, size);
        byte[] array = context.writeBuff.array();
        assertEquals(b, array[0]);

        context.close();
        serverSocket.close();
    }

    /**
     * 测试输出一个整数
     *
     * @throws Exception
     */
    @Test
    public void testPrint1() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        int i = NutletUtil.getInt();
        context.print(i);
        String str = String.valueOf(i);
        int size = context.writeBuff.position();
        assertEquals(str.length(), size);
        byte[] array = context.writeBuff.array();
        assertEquals(str, new String(array, 0, size));

        context.close();
        serverSocket.close();
    }

    /**
     * 输出一个长整型数字
     *
     * @throws Exception
     */
    @Test
    public void testPrint2() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        long l = NutletUtil.getLong();
        context.print(l);
        String str = String.valueOf(l);
        int size = context.writeBuff.position();
        assertEquals(str.length(), size);
        byte[] array = context.writeBuff.array();
        assertEquals(str, new String(array, 0, size));

        context.close();
        serverSocket.close();
    }

    /**
     * 测试输出byte数组
     *
     * @throws Exception
     */
    @Test
    public void testPrint3() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        byte[] bytes = NutletUtil.getBytes(256);
        context.print(bytes);
        int size = context.writeBuff.position();
        assertEquals(bytes.length, size);
        byte[] array = context.writeBuff.array();
        assertMatches(bytes, 0, array, 0, size);

        context.close();
        serverSocket.close();
    }

    public static void assertMatches(byte[] bytes, int off1, byte[] array, int off2, int len) {
        assertTrue(ArrayUtil.matches(bytes, off1, array, off2, len));
    }

    /**
     * 测试输出byte数组
     *
     * @throws Exception
     */
    @Test
    public void testPrint4() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        byte[] bytes = NutletUtil.getBytes(256);
        context.print(bytes, 24, 40);
        int size = context.writeBuff.position();
        assertEquals(40, size);
        byte[] array = context.writeBuff.array();
        assertMatches(bytes, 24, array, 0, size);

        context.close();
        serverSocket.close();
    }

    /**
     * 测试输出字符串
     *
     * @throws Exception
     */
    @Test
    public void testPrint5() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        String str = NutletUtil.getAscii(256);
        context.print(str);
        int size = context.writeBuff.position();
        assertEquals(str.length(), size);
        byte[] array = context.writeBuff.array();
        assertMatches(str.getBytes(), 0, array, 0, size);

        context.close();
        serverSocket.close();
    }

    /**
     * 测试输出字符串（边界测试）
     *
     * @throws Exception
     */
    @Test
    public void testPrint6() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        try {
            context.print((String) null);
            fail("IllegalArgument");
        }
        catch (IllegalArgumentException iae) {

        }

        try {
            context.print((byte[]) null);
            fail("IllegalArgument");
        }
        catch (IllegalArgumentException iae) {

        }

        try {
            context.print((byte[]) null, 0, 0);
            fail("IllegalArgument");
        }
        catch (IllegalArgumentException iae) {

        }

        context.close();
        serverSocket.close();
    }

    /**
     * 测试输出字符串
     *
     * @throws Exception
     */
    @Test
    public void testPrintln0() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        try {
            context.println((String) null);
            fail("IllegalArgument");
        }
        catch (IllegalArgumentException iae) {

        }

        try {
            context.println((byte[]) null);
            fail("IllegalArgument");
        }
        catch (IllegalArgumentException iae) {

        }

        try {
            context.println((byte[]) null, 0, 0);
            fail("IllegalArgument");
        }
        catch (IllegalArgumentException iae) {

        }

        context.close();
        serverSocket.close();
    }


    /**
     * 测试输出byte[]
     *
     * @throws Exception
     */
    @Test
    public void testPrintln1() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        byte[] bytes = NutletUtil.getBytes(256);
        context.println(bytes);
        int size = context.writeBuff.position();
        assertEquals(bytes.length + 2, size);
        byte[] array = context.writeBuff.array();
        assertMatches(bytes, 0, array, 0, size - 2);

        context.close();
        serverSocket.close();
    }

    /**
     * 测试输出byte[]
     *
     * @throws Exception
     */
    @Test
    public void testPrintln2() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        byte[] bytes = NutletUtil.getBytes(256);
        context.println(bytes, 24, 40);
        int size = context.writeBuff.position();
        assertEquals(42, size);
        byte[] array = context.writeBuff.array();
        assertMatches(bytes, 24, array, 0, size - 2);

        context.close();
        serverSocket.close();
    }

    /**
     * 测试输出字符串
     *
     * @throws Exception
     */
    @Test
    public void testPrintln3() throws Exception
    {
        SocketAddress address = new InetSocketAddress(9999);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        CommandContext context = new CommandContext();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        context.init(channel);

        String str = NutletUtil.getAscii(256);
        context.println(str);
        int size = context.writeBuff.position();
        assertEquals(str.length() + 2, size);
        byte[] array = context.writeBuff.array();
        assertMatches(str.getBytes(), 0, array, 0, size - 2);

        context.close();
        serverSocket.close();
    }

    /**
     * 测试返回写Buff容积
     *
     * @throws Exception
     */
    @Test
    public void testGetWriteBuffCapacity() throws Exception
    {
        CommandContext context = new CommandContext();
        assertEquals(1024, context.getWriteBuffCapacity());
    }

    /**
     * 测试设置WriteBuffCapacity
     *
     * @throws Exception
     */
    @Test
    public void testSetWriteBuffCapacity() throws Exception
    {
        CommandContext context = new CommandContext();
        context.setWriteBuffCapacity(ByteBufferPool.SIZE_8192);
        assertEquals(ByteBufferPool.SIZE_8192, context.getWriteBuffCapacity());
    }

    @Test
    public void testGetReadBuffCapacity() throws Exception
    {
        CommandContext context = new CommandContext();
        assertEquals(1024, context.getReadBuffCapacity());
    }

    @Test
    public void testSetReadBuffCapacity() throws Exception
    {
        CommandContext context = new CommandContext();
        context.setReadBuffCapacity(ByteBufferPool.SIZE_8192);
        assertEquals(ByteBufferPool.SIZE_8192, context.getReadBuffCapacity());
    }

    @Test
    public void testClose() throws Exception
    {
        CommandContext context = new CommandContext();
        context.close();
    }

    @Test
    public void testGetCommandBuffer() throws Exception
    {
        CommandContext context = new CommandContext();
        assertNotNull(context.getCommandBuffer());
    }
}