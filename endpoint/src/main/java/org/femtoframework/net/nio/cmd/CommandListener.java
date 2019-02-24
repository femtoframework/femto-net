package org.femtoframework.net.nio.cmd;

import java.util.EventListener;

/**
 * 命令侦听者，用于侦听客户端请求的命令
 *
 * @author fengyun
 * @version 1.00 2005-10-13 16:12:13
 */
public interface CommandListener extends EventListener
{
    /**
     * 当有命令到达的时候调用
     *
     * @param context Command Context
     * @param command 命令数据
     * @param off     起始位置
     * @param len     长度
     */
    void onCommand(CommandContext context, byte[] command, int off, int len);
}
