package org.femtoframework.net.message;

import java.util.EventListener;

/**
 * 消息侦听者，当消息达到的时候被唤醒
 *
 * @author fengyun
 * @version 1.00 2005-5-21 19:45:22
 */
public interface MessageListener extends EventListener
{
    /**
     * 当消息到达的时候调用
     *
     * @param metadata 消息元数据
     * @param message  消息
     */
    public void onMessage(MessageMetadata metadata, Object message);
}
