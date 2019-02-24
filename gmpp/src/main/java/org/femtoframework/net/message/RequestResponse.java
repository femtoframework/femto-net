package org.femtoframework.net.message;

/**
 * 请求和响应对，处理需要Ack的消息，由于响应需要需要记录请求消息报文的标识<br>
 * 因此拥有getId的方法<br>
 * 可以作为请求被传送，也可以作为响应被传递
 *
 * @author fengyun
 * @version 1.00 2005-5-21 23:32:52
 */
public interface RequestResponse extends RequestMessage, ResponseMessage
{

    /**
     * Id for request and response
     *
     * @return Id
     */
    int getId();

    /**
     * 返回请求
     *
     * @return 请求
     */
    public Object getRequest();

    /**
     * 返回响应
     *
     * @return 响应
     */
    public Object getResponse();

    /**
     * 设置请求
     *
     * @param request 请求
     */
    public void setRequest(Object request);

    /**
     * 设置响应
     *
     * @param response 响应
     */
    public void setResponse(Object response);

    /**
     * 注入响应写回去的消息发送器
     *
     * @param sender 消息发送者
     */
    public void setMessageSender(MessageSender sender);

    /**
     * 获取消息发送器
     *
     * @return 消息发送器
     */
    public MessageSender getMessageSender();

    /**
     * 任务完成
     * <p/>
     * 在请求完成之后调用，将响应写会给客户端
     */
    public void ack();
}
