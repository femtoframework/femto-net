package org.femtoframework.net.comm.packet;

import java.io.IOException;

import org.femtoframework.io.IOUtil;
import org.femtoframework.net.comm.AbstractConnection;
import org.femtoframework.net.comm.Packet;
import org.femtoframework.net.comm.PacketProtocol;
import org.femtoframework.net.comm.ParametersAware;
import org.femtoframework.lang.reflect.Reflection;
import org.femtoframework.parameters.Parameters;

/**
 * 报文协议相关的连接
 *
 * @author fengyun
 * @version 1.00 2005-5-8 10:18:39
 */
public abstract class PacketConnection extends AbstractConnection
    implements ParametersAware
{
    /**
     * Socket 报文协议
     */
    private PacketProtocol protocol;

    /**
     * 连接参数
     */
    protected Parameters parameters;

    /**
     * 返回报文协议
     *
     * @return 报文协议
     */
    public PacketProtocol getProtocol()
    {
        return protocol;
    }

    /**
     * 设置报文协议
     *
     * @param protocol 报文协议
     */
    public void setProtocol(PacketProtocol protocol) throws IOException
    {
        this.protocol = protocol;
        //初始化Socket 和 连接参数
        if (parameters != null && protocol instanceof ParametersAware) {
            ((ParametersAware) protocol).setParameters(parameters);
        }
    }

    /**
     * 读取一个信息包
     *
     * @return 返回一个信息包
     * @throws org.femtoframework.net.comm.CommException
     *          如果出现异常
     */
    public Packet readPacket() throws IOException
    {
        return protocol.readPacket();
    }

    /**
     * 写出一个信息包
     *
     * @param packet 信息包
     * @throws org.femtoframework.net.comm.CommException
     *          如果出现异常
     */
    public void writePacket(Packet packet) throws IOException
    {
        protocol.writePacket(packet);
    }

    /**
     * 关闭连接
     *
     * @throws org.femtoframework.net.comm.CommException
     *          如果连接异常
     */
    protected void doClose() throws IOException
    {
        parameters = null;
        IOUtil.close(protocol);
        protocol = null;
    }

    /**
     * 返回连接参数集合
     *
     * @return 连接参数集合
     */
    public Parameters getParameters()
    {
        return parameters;
    }

    /**
     * 设置连接参数集合
     *
     * @param parameters 连接参数集合
     */
    public void setParameters(Parameters parameters)
    {
        this.parameters = parameters;
        if (protocol != null) {
            if (protocol instanceof ParametersAware) {
                ((ParametersAware) protocol).setParameters(parameters);
            }
        }
        else {
            //没有协议被初始化
            Class protocolClass = (Class) parameters.get("protocol_class");
            if (protocolClass != null) {
                try {
                    setProtocol((PacketProtocol) Reflection.newInstance(protocolClass));
                }
                catch (IOException e) {
                }
            }
        }
    }
}
