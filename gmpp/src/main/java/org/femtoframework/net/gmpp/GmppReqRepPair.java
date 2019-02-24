package org.femtoframework.net.gmpp;

import org.femtoframework.bean.Identifiable;
import org.femtoframework.net.comm.AddressAware;
import org.femtoframework.net.message.MessageRegistry;
import org.femtoframework.net.message.ReqRepPair;
import org.femtoframework.net.message.RequestAware;
import org.femtoframework.net.message.packet.MessagePacket;

/**
 * GMPP请求和响应对
 *
 * @author fengyun
 * @version 1.00 2005-5-21 23:48:31
 */
public class GmppReqRepPair extends ReqRepPair
{
    /**
     * 请求报文
     */
    private MessagePacket packet;

    /**
     * 消息注册
     */
    private MessageRegistry registry;

    /**
     * 请求消息类型
     */
    private int reqMsgType;

    /**
     * 构造请求和响应对
     *
     * @param request 请求报文
     */
    public GmppReqRepPair(MessagePacket request, MessageRegistry registry)
    {
        this.packet = request;
        this.registry = registry;
        this.reqMsgType = packet.getMessageType();
        setId(packet.getMessageId());
    }

    /**
     * 返回请求
     *
     * @return 请求
     */
    public Object getRequest()
    {
        if (request == null && packet != null) {
            try {
                request = packet.getMessage();
                if (request instanceof AddressAware) {
                    AddressAware aware = (AddressAware) request;
                    aware.setHost(sender.getHost());
                    aware.setPort(sender.getPort());
                }
            }
            finally {
                packet.destroy();
                packet = null;
            }
        }
        return request;
    }

    /**
     * 返回响应
     *
     * @return 响应
     */
    public Object getResponse()
    {
        if (response == null) {
            int repType = -reqMsgType;
            response = registry.createMessage(repType);
            if (response instanceof Identifiable) {
                ((Identifiable) response).setId(getId());
            }
            if (response instanceof RequestAware) {
                ((RequestAware) response).setRequest(request);
            }
        }
        return response;
    }

    /**
     * 返回请求超时时间，如果请求超时时间是0，表示没有超时
     *
     * @return 请求超时时间
     */
    public int getTimeout()
    {
        return packet != null ? packet.getTimeout() : 0;
    }
}
