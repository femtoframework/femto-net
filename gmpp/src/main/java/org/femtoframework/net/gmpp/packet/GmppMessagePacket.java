package org.femtoframework.net.gmpp.packet;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.femtoframework.bean.Identifiable;
import org.femtoframework.io.*;
import org.femtoframework.net.gmpp.GmppConstants;
import org.femtoframework.net.message.MessageMetadata;
import org.femtoframework.net.message.MessageRegistry;
import org.femtoframework.net.message.MessageRegistryUtil;
import org.femtoframework.net.message.packet.MessagePacket;

/**
 * 客户端消息报文实现
 *
 * @author fengyun
 * @version 1.00 2004-8-14 13:40:40
 */
public class GmppMessagePacket extends PacketBase
    implements MessagePacket
{
    /**
     * 消息注册
     */
    private static MessageRegistry registry = MessageRegistryUtil.getRegistry();

    /**
     * 消息报文的最大数据量
     */
    private static final int MAX_PACKET_SIZE = 1024 * 1024;

    /**
     * 消息类型
     */
    public static final int TYPE_NULL = 0;

    /**
     * 消息类型<br>
     * <p/>
     * 用来指明当另外一端收到消息后如何处理消息。
     */
    protected int msgType = TYPE_NULL;

    /**
     * 消息标识
     */
    private int msgId = 0;

    /**
     * 超时
     */
    private int timeout;

    /**
     * 消息
     */
    private transient Object message;

    /**
     * 消息编码解码
     */
    private transient ObjectCodec codec = ObjectCodecUtil.getDefaultObjectCodec();

    /**
     * 消息对应的类
     */
    private transient Class messageClass;

    /**
     * 消息Metadata数据
     */
    private transient MessageMetadata metadata;

    /**
     * 消息对象的类装载器
     */
    private transient ClassLoader classLoader = null;

    /**
     * 下一个请求号
     */
    private static int nextId = 1;

    /**
     * 返回下一个请求号
     */
    protected static synchronized int getNextId()
    {
        return nextId++ & 0x7FFFFFFF;
    }

    /**
     * 构造
     */
    public GmppMessagePacket()
    {
        super(GmppConstants.PACKET_MESSAGE);
    }

    /**
     * 构造
     *
     * @param id 标识
     */
    public GmppMessagePacket(int id)
    {
        super(GmppConstants.PACKET_MESSAGE, id);
    }

    /**
     * 设置消息
     *
     * @param message 消息
     * @throws SerializationException 串行化异常的时候抛出
     */
    public void setMessage(Object message, int msgId) throws SerializationException
    {
        //只有消息是 Externalizable 的才可以注册成相应的类型
        int type = registry.getType(message);
        if (type == MessageRegistry.NO_SUCH_TYPE) {
            throw new IllegalArgumentException("No such message type:" + message.getClass());
        }
        this.msgType = type;
        this.msgId = msgId;
        this.message = message;
        this.messageClass = message.getClass();
        marshal(message);
    }

    /**
     * 设置消息
     *
     * @param message 消息
     * @throws SerializationException 串行化异常的时候抛出
     */
    public void setMessage(Object message) throws SerializationException
    {
        setMessage(message, getNextId());
    }

    /**
     * 返回消息的类型，用于判断消息属于什么类型
     */
    public int getMessageType()
    {
        return msgType;
    }

    /**
     * 返回消息的序列号
     *
     * @return 序列号
     */
    public int getMessageId()
    {
        return msgId;
    }

    /**
     * 返回消息
     *
     * @throws SerializationException 串行化异常的时候抛出
     */
    public Object getMessage() throws SerializationException
    {
        if (message == null) {
            message = demarshal();
        }
        return message;
    }

    /**
     * 预串行化
     */
    protected transient ByteBufferInputStream readBuff;
    protected transient ByteBufferOutputStream writeBuff;

    /**
     * 串行化消息
     *
     * @param message 消息
     * @throws SerializationException 串行化异常的时候抛出
     */
    protected void marshal(Object message)
        throws SerializationException
    {
        if (writeBuff == null) {
            writeBuff = new ByteBufferOutputStream();
            if (message instanceof Streamable) {
                try {
                    ((Streamable) message).writeTo(writeBuff);
                }
                catch (IOException e) {
                    throw new SerializationException("IOException", e);
                }
            }
            else {
                ObjectOutputStream oos;
                try {
                    oos = getObjectOutput(writeBuff);
                    writeMessage(oos, message);
                    oos.flush();
                    oos.reset();
                }
                catch (IOException e) {
                    throw new SerializationException("IOException", e);
                }
            }
        }
    }

    /**
     * 输出消息到对象输出流中
     *
     * @param oos     对象输出流
     * @param message 消息
     * @throws IOException
     */
    protected void writeMessage(ObjectOutputStream oos, Object message)
        throws IOException
    {
        if (message instanceof Externalizable) {
            ((Externalizable) message).writeExternal(oos);
        }
        else {
            oos.writeObject(message);
        }
    }

    /**
     * 反串行化消息
     *
     * @return
     * @throws org.femtoframework.io.SerializationException
     *          串行化异常的时候抛出
     */
    protected Object demarshal() throws SerializationException
    {
        Object msg = null;
        if (readBuff != null) {
            if (msgType != MessageRegistry.NO_SUCH_TYPE) {
                metadata = registry.getMetadata(msgType);
                messageClass = metadata.getMessageClass();

                if (Streamable.class.isAssignableFrom(messageClass)) {
                    msg = readStreamable();
                }
            }
            if (msg == null) {
                msg = readMessage();
            }
        }
        //自动注入消息标识
        if (msg instanceof Identifiable) {
            ((Identifiable) msg).setId(msgId);
        }
        return msg;
    }

    private Object readMessage() throws SerializationException
    {
        ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
        try {
            ObjectInputStream ois = getObjectInput(readBuff);
            return readMessage(ois);
        }
        catch (IOException e) {
            throw new SerializationException("IOException", e);
        }
        catch (ClassNotFoundException e) {
            throw new SerializationException("Class not found", e);
        }
        finally {
            IOUtil.close(readBuff);
            readBuff = null;
            if (classLoader != null) {
                Thread.currentThread().setContextClassLoader(origClassLoader);
            }
        }
    }

    private Object readStreamable() throws SerializationException
    {
        //直接读取
        Streamable message = metadata.createMessage();
        ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
        try {
            message.readFrom(readBuff);
        }
        catch (IOException e) {
            throw new SerializationException("IOException", e);
        }
        catch (ClassNotFoundException e) {
            throw new SerializationException("Class not found", e);
        }
        finally {
            IOUtil.close(readBuff);
            readBuff = null;
            if (classLoader != null) {
                Thread.currentThread().setContextClassLoader(origClassLoader);
            }
        }
        return message;
    }

    /**
     * 从对象输入流中读取消息
     *
     * @param ois 对象输入流
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    protected Object readMessage(ObjectInputStream ois)
        throws IOException, ClassNotFoundException
    {
        if (messageClass != null) {
            if (Externalizable.class.isAssignableFrom(messageClass)) {
                Externalizable message = (Externalizable) registry.createMessage(msgType);
                message.readExternal(ois);
                return message;
            }
            else {
                return ois.readObject();
            }
        }
        else {
            return ois.readObject();
        }
    }

    /**
     * 输出
     *
     * @param pos 包输出流
     */
    public void writePacket(OutputStream pos)
        throws IOException
    {
        super.writePacket(pos);
        CodecUtil.writeInt(pos, msgType);
        CodecUtil.writeInt(pos, msgId);
        CodecUtil.writeInt(pos, timeout);
        CodecUtil.writeInt(pos, writeBuff.size());
        writeBuff.writeTo(pos);
    }

    /**
     * 输入
     *
     * @param in 包输入流
     */
    public void readPacket(InputStream in)
        throws IOException
    {
        super.readPacket(in);
        this.msgType = CodecUtil.readInt(in);
        this.msgId = CodecUtil.readInt(in);
        this.timeout = CodecUtil.readInt(in);
        int size = CodecUtil.readInt(in);
        if (size > 0) {
            if (size > MAX_PACKET_SIZE) {
                throw new IOException("Invalid packet size:" + size);
            }
            readBuff = new ByteBufferInputStream(in, size);
        }
    }

    /**
     * 根据输出流返回对象输出流
     *
     * @param out 输出流
     * @return
     * @throws IOException
     */
    protected ObjectOutputStream getObjectOutput(OutputStream out)
        throws IOException
    {
        return codec.getObjectOutput(out);
    }

    /**
     * 根据输入流返回对象输入流
     *
     * @param in 输入流
     * @return
     * @throws IOException
     */
    protected ObjectInputStream getObjectInput(InputStream in)
        throws IOException
    {
        return codec.getObjectInput(in);
    }

    /**
     * 返回对象编码器
     *
     * @return 对象编码器
     */
    public ObjectCodec getCodec()
    {
        return codec;
    }

    /**
     * 设置对象编码器
     *
     * @param codec 对象编码器
     */
    public void setCodec(ObjectCodec codec)
    {
        this.codec = codec;
    }

    /**
     * 释放该对象
     */
    public void destroy()
    {
        if (message != null) {
            message = null;
        }

        if (readBuff != null) {
            IOUtil.close(readBuff);
            this.readBuff = null;
        }
        if (writeBuff != null) {
            IOUtil.close(writeBuff);
            this.writeBuff = null;
        }
    }

    /**
     * 返回类装载器
     *
     * @return 类装载器
     */
    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    /**
     * 设置类装载器
     *
     * @param classLoader 类装载器
     */
    public void setClassLoader(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    /**
     * 返回请求超时时间，如果请求超时时间是0，表示没有超时
     *
     * @return 请求超时时间
     */
    public int getTimeout()
    {
        return timeout;
    }

    /**
     * 设置超时时间
     *
     * @param timeout 超时时间
     */
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }


    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("GmppMessagePacket");
        buf.append("{msgType=").append(msgType);
        buf.append(",msgId=").append(msgId);
        buf.append(",timeout=").append(timeout);
        buf.append(",message=").append(message);
        buf.append('}');
        return buf.toString();
    }
}
