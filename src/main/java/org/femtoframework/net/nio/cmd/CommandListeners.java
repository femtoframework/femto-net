package org.femtoframework.net.nio.cmd;

import org.femtoframework.pattern.EventListeners;

import java.util.List;

/**
 * 命令侦听者集合
 *
 * @author fengyun
 * @version 1.00 2005-10-13 16:15:06
 */
public class CommandListeners extends EventListeners<CommandListener> implements CommandListener
{

    public CommandListeners()
    {
    }

    public CommandListeners(CommandListener listener)
    {
        super(listener);
    }

    public CommandListeners(List<CommandListener> listeners)
    {
        super(listeners);
    }

    /**
     * 当有命令到达的时候调用
     *
     * @param context Command Context
     * @param command 命令数据
     * @param off     起始位置
     * @param len     长度
     */
    public void onCommand(CommandContext context, byte[] command, int off, int len)
    {
        for (int i = 0, size = listeners.size(); i < size; i++) {
            listeners.get(i).onCommand(context, command, off, len);
        }
    }
}
