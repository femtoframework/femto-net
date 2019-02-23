package org.femtoframework.net.nio.cmd;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.femtoframework.io.IOUtil;
import org.femtoframework.lang.OctetBuffer;
import org.femtoframework.lang.OctetString;
import org.femtoframework.net.nio.SimpleChannelContext;
import org.femtoframework.nio.ByteBufferPool;

/**
 * 基础的Context，根据Ascii格式协议的特点抽象的基础上下文
 *
 * @author fengyun
 * @version 1.00 2005-1-2 15:11:59
 */
public class CommandContext
    extends SimpleChannelContext
{
    public static final byte SPACE = (byte)' ';

    public static final byte[] CRLF_BYTES = "\r\n".getBytes();

    public static final byte[] SPACE_BYTES = {SPACE};


    /**
     * 写Buffer容积
     */
    private int writeBuffCapacity = 1024;


    /**
     * 写Buffer
     */
    protected ByteBuffer writeBuff;

    private boolean writeBuffFlipped = false;

    /**
     * 读Buffer容积
     */
    private int readBuffCapacity = 1024;

    /**
     * 读Buffer
     */
    protected ByteBuffer readBuff;

    /**
     * ReadBuffer的长度
     */
    protected int readBuffLength;

    /**
     * WriteBuffer的长度
     */
    protected int writeBuffLength;

    /**
     * 命令输出Buffer
     */
    protected OctetBuffer commandBuffer = new OctetBuffer();

    /**
     * 初始化CommandContext，将SocketChannel传递给它，并且初始化Buffer等。
     */
    public void init(SocketChannel channel)
    {
        super.init(channel);
        readBuff = ByteBufferPool.allocate(readBuffCapacity);
        writeBuff = ByteBufferPool.allocate(writeBuffCapacity);
        writeBuffFlipped = false;
    }

    /**
     * 读取数据
     *
     * @return
     * @throws java.io.IOException
     */
    public int read() throws IOException
    {
        int len = getChannel().read(readBuff);
        if (len < 0) {
            throw new SocketClosedException("Connection reset by peer");
        }
        else {
            return len;
        }
    }

    /**
     * 输出数据
     *
     * @return 是否已经输出结束
     * @throws IOException
     */
    public boolean write() throws IOException
    {
        if (flush()) {
            getRegistry().register(this);
            return true;
        }
        return false;
    }

    /**
     * 设置writeBuffer可以让其输出
     */
    public void flip()
    {
        if (!writeBuffFlipped) {
            writeBuff = (ByteBuffer)writeBuff.flip();
            writeBuffFlipped = true;
        }
    }

    /**
     * 填充writeBuffer
     *
     * @param input 输入流
     * @return 是否已经结束
     * @throws IOException
     */
    public boolean readFully(InputStream input) throws IOException
    {
        byte[] buf = writeBuff.array();
        int position = writeBuff.position();
        int len = buf.length - position;
        int read = IOUtil.readFully(input, buf, position, len);
        writeBuff.position(read + position);
        return read < len;
    }


    /**
     * 输出数据
     *
     * @return 是否已经输出结束
     * @throws IOException
     */
    public boolean flush() throws IOException
    {
        flip();
        setOperationSet(OP_WRITE);
        getChannel().write(writeBuff);

        if (!writeBuff.hasRemaining()) { //下一次侦听度操作
            onBufferWritten();
            writeBuff.clear();
            writeBuffFlipped = false;
            return true;
        }
        return false;
    }

    /**
     * 当Buffer已经输出完备之后调用
     */
    protected void onBufferWritten()
    {
        setOperationSet(OP_READ);
        readBuff.clear();
    }

    /**
     * 获取下一行，当有一行的时候结束
     *
     * @return
     * @throws IOException
     */
    public boolean nextLine() throws IOException
    {
        read();

        int len = readBuff.position();
        if (len < 2) {
            return false;
        }

        byte data[] = readBuff.array();
        if (data[len - 1] == '\n') {
            if (data[len - 2] == '\r') {
                readBuffLength = len - 2;
                return true;
            }
            else {
                throw new CommandStrayLineException();
            }
        }
        else if (len == readBuff.capacity()) {
            throw new IOException("The line is too long");
        }
        else {
            return false;
        }
    }

    /**
     * 获取下一个命令
     *
     * @return
     * @throws IOException
     */
    public boolean nextCommand() throws IOException
    {
        read();

        int len = readBuff.position();
        if (len < 2) {
            return false;
        }

        byte data[] = readBuff.array();
        if (data[len - 1] == '\n') {
            if (data[len - 2] == '\r') {
                readBuffLength = len - 2;
                return true;
            }
            else {
                throw new CommandStrayLineException();
            }
        }
        else {
            return false;
        }
    }

    /**
     * 返回整个命令的字节数据
     *
     * @return 整个命令的字节数据
     */
    public byte[] getCommandBytes()
    {
        return readBuff.array();
    }

    /**
     * 返回整个命令的长度
     *
     * @return 命令的长度
     */
    public int getCommandLength()
    {
        return readBuffLength;
    }


    private static final int CRLF_BYTES_LEN = CRLF_BYTES.length;

    private void ensureCapacity(int len)
    {
        int remaining = writeBuff.remaining();
        if (remaining <= len) {
            try {
                write();
            }
            catch (IOException e) {
                //todo
            }
        }
    }

    /**
     * 输出空格到输出BUFFER中
     */
    public void printSpace()
    {
        print(SPACE);
    }

    /**
     * 输出消息，只写入到输出BUFFER中
     *
     * @param message
     */
    public void print(byte message)
    {
        ensureCapacity(1);
        writeBuff.put(message);
    }

    /**
     * 输出整数，格式化成Ascii串
     *
     * @param value 整数
     */
    public void print(int value)
    {
        print(OctetString.valueOf(value));
    }

    /**
     * 输出整数，格式化成Ascii串
     *
     * @param value 长整数
     */
    public void print(long value)
    {
        print(OctetString.valueOf(value));
    }

    /**
     * 输出消息，只写入到输出BUFFER中
     *
     * @param message
     */
    public void print(byte[] message)
    {
        if (message == null) {
            throw new IllegalArgumentException("Can't print null message");
        }
        print(message, 0, message.length);
    }

    /**
     * 输出消息，只写入到输出BUFFER中
     *
     * @param message
     */
    public void print(byte[] message, int off, int len)
    {
        if (message == null) {
            throw new IllegalArgumentException("Can't print null message");
        }
        ensureCapacity(len);
        writeBuff.put(message, off, len);
    }

    /**
     * 输出消息，只写入到输出BUFFER中
     *
     * @param message
     */
    public void print(String message)
    {
        if (message == null) {
            throw new IllegalArgumentException("Can't print null message");
        }
        print(message.getBytes());
    }

    /**
     * 输出消息，只写入到输出BUFFER中
     *
     * @param message
     */
    public void print(OctetString message)
    {
        if (message == null) {
            throw new IllegalArgumentException("Can't print null message");
        }
        int len = message.length();
        ensureCapacity(len);
        int off = message.offset();
        byte[] value = message.getValue();
        writeBuff.put(value, off, len);
    }

    /**
     * 输出消息，只写入到输出BUFFER中
     *
     * @param message
     */
    public void println(byte[] message)
    {
        if (message == null) {
            throw new IllegalArgumentException("Can't print null message");
        }
        println(message, 0, message.length);
    }

    /**
     * 输出消息，只写入到输出BUFFER中
     *
     * @param message
     */
    public void println(byte[] message, int off, int len)
    {
        if (message == null) {
            throw new IllegalArgumentException("Can't print null message");
        }
        ensureCapacity(len + CRLF_BYTES_LEN);
        writeBuff.put(message, off, len);
        writeBuff.put(CRLF_BYTES);
    }

    /**
     * 输出字符串，只写入到输出BUFFER中
     *
     * @param message
     */
    public void println(String message)
    {
        if (message == null) {
            throw new IllegalArgumentException("Can't print null message");
        }
        println(message.getBytes());
    }

    /**
     * 输出一行，只写入到输出BUFFER中
     */
    public void println()
    {
        ensureCapacity(CRLF_BYTES_LEN);
        writeBuff.put(CRLF_BYTES);
    }

    public int getWriteBuffCapacity()
    {
        return writeBuffCapacity;
    }

    public void setWriteBuffCapacity(int writeBuffCapacity)
    {
        this.writeBuffCapacity = writeBuffCapacity;
    }

    public int getReadBuffCapacity()
    {
        return readBuffCapacity;
    }

    public void setReadBuffCapacity(int readBuffCapacity)
    {
        this.readBuffCapacity = readBuffCapacity;
    }


    /**
     * 关闭Socket上下文
     */
    public void close()
    {
        super.close();

        if (readBuff != null) {
            ByteBufferPool.recycle(readBuff);
            readBuff = null;
        }
        if (writeBuff != null) {
            ByteBufferPool.recycle(writeBuff);
            writeBuff = null;
        }
        readBuffLength = 0;
        writeBuffLength = 0;
    }

    /**
     * 返回命令OctetBuffer（恢复到初始化长度）
     *
     * @return 命令OctetBuffer（恢复到初始化长度）
     */
    public OctetBuffer getCommandBuffer()
    {
        commandBuffer.setLength(0);
        return commandBuffer;
    }

    public ByteBuffer getWriteBuffer()
    {
        return writeBuff;
    }

    public ByteBuffer getReadBuffer()
    {
        return readBuff;
    }

}
