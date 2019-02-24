package org.femtoframework.net.message;

import org.femtoframework.bean.Identifiable;

/**
 * 请求和响应对的实现
 *
 * @author fengyun
 * @version 1.00 2005-5-21 23:37:59
 */
public abstract class ReqRepPair implements RequestResponse, Identifiable
{
    private int id;
    protected Object request;
    protected Object response;
    protected MessageSender sender;

    /**
     * 返回实体唯一标识
     *
     * @return 标识
     */
    public int getId()
    {
        return id;
    }

    /**
     * 设置标识
     *
     * @param id
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * 返回请求
     *
     * @return
     */
    public Object getRequest()
    {
        return request;
    }

    /**
     * 返回响应
     *
     * @return
     */
    public Object getResponse()
    {
        return response;
    }

    /**
     * 设置请求
     *
     * @param request 请求
     */
    public void setRequest(Object request)
    {
        this.request = request;
    }

    /**
     * 设置响应
     *
     * @param response 响应
     */
    public void setResponse(Object response)
    {
        this.response = response;
    }

    /**
     * 注入响应写回去的消息发送器
     *
     * @param sender 消息发送者
     */
    public void setMessageSender(MessageSender sender)
    {
        this.sender = sender;
    }

    /**
     * 获取消息发送器
     *
     * @return 消息发送器
     */
    public MessageSender getMessageSender()
    {
        return this.sender;
    }

    /**
     * 任务完成
     * <p/>
     * 在请求完成之后调用，将响应写会给客户端
     */
    public void ack()
    {
        if (sender != null) {
            //发送 RequestRespone回去，以保证id能够被获取
            sender.send(this);
        }
        //回收请求
    }
}
