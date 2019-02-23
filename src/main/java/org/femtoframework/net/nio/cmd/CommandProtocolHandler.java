package org.femtoframework.net.nio.cmd;

import org.femtoframework.net.nio.SocketChannelContext;
import org.femtoframework.net.nio.SocketChannelContextListener;

import java.util.Collections;
import java.util.List;

/**
 * 基础的协议处理器
 *
 * @author fengyun
 * @version 1.00 2005-1-2 15:12:39
 */
public abstract class CommandProtocolHandler
    extends SocketChannelContextListener
{
    public static final byte SPACE = (byte)' ';

    protected CommandHandler commandHandler;

    /**
     * 处理SocketChannelContext
     *
     * @param action  事件动作
     * @param context Socket上下文
     */
    protected void handleEvent(int action, SocketChannelContext context)
    {
        handleEvent(action, (CommandContext)context);
    }

    /**
     * 处理BaseContext
     *
     * @param action  事件动作
     * @param context Socket上下文
     */
    protected abstract void handleEvent(int action, CommandContext context);

    /**
     * 返回命令处理器
     *
     * @return 命令处理器
     */
    public CommandHandler getCommandHandler()
    {
        return commandHandler;
    }

    /**
     * 设置命令处理器
     *
     * @param commandHandler
     */
    public void setCommandHandler(CommandHandler commandHandler)
    {
        this.commandHandler = commandHandler;
    }

    /**
     * 添加Command监听者
     *
     * @param listener
     */
    public void addCommandListener(CommandListener listener)
    {
        if (commandHandler != null) {
            commandHandler.addCommandListener(listener);
        }
    }

    /**
     * 返回Context侦听者
     *
     * @return BaseCommandListener[]
     */
    public List<CommandListener> getCommandListeners()
    {
        if (commandHandler != null) {
            return commandHandler.getCommandListeners();
        }
        return Collections.emptyList();
    }

    /**
     * 删除Command监听者
     *
     * @param listener Command监听者
     */
    public void removeCommandListener(CommandListener listener)
    {
        if (commandHandler != null) {
            commandHandler.removeCommandListener(listener);
        }
    }

    /**
     * 添加Context监听者
     *
     * @param listener
     */
    public void addContextListener(CommandContextListener listener)
    {
        if (commandHandler != null) {
            commandHandler.addContextListener(listener);
        }
    }

    /**
     * 返回Context侦听者
     *
     * @return BaseCommandContextListener[]
     */
    public List<CommandContextListener> getContextListeners()
    {
        if (commandHandler != null) {
            return commandHandler.getContextListeners();
        }
        return Collections.emptyList();
    }

    /**
     * 删除Context监听者
     *
     * @param listener Context监听者
     */
    public void removeContextListener(CommandContextListener listener)
    {
        if (commandHandler != null) {
            commandHandler.removeContextListener(listener);
        }
    }
}
