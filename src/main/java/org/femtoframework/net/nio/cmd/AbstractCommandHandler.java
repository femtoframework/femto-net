package org.femtoframework.net.nio.cmd;

import java.util.Collections;
import java.util.List;

/**
 * 抽象命令处理器
 *
 * @author fengyun
 * @version 1.00 2005-1-2 15:21:56
 */
public abstract class AbstractCommandHandler
    implements CommandHandler
{
    /**
     * 监听者列表
     */
    private CommandListener commandlisteners;

    /**
     * 监听者列表
     */
    private CommandContextListener contextlisteners;

    /**
     * 添加Command监听者
     *
     * @param listener
     */
    public void addCommandListener(CommandListener listener)
    {
        if (listener == null) {
            return;
        }
        if (commandlisteners == null) {
            commandlisteners = listener;
        }
        else if (commandlisteners instanceof CommandListeners) {
            ((CommandListeners)commandlisteners).addListener(listener);
        }
        else {
            commandlisteners = new CommandListeners(commandlisteners);
            ((CommandListeners)commandlisteners).addListener(listener);
        }
    }

    /**
     * 返回Context侦听者
     *
     * @return BaseCommandListener[]
     */
    public List<CommandListener> getCommandListeners()
    {
        if (commandlisteners != null) {
            if (commandlisteners instanceof CommandListeners) {
                return ((CommandListeners)commandlisteners).getListeners();
            }
            else {
                return Collections.singletonList(commandlisteners);
            }
        }
        return Collections.emptyList();
    }

    /**
     * 设置Context侦听者
     *
     * @param listeners context侦听者
     */
    public void setCommandListeners(List<CommandListener> listeners)
    {
        if (listeners == null || listeners.isEmpty()) {
            return;
        }
        this.commandlisteners = new CommandListeners(listeners);
    }

    /**
     * 删除Command监听者
     *
     * @param listener Command监听者
     */
    public void removeCommandListener(CommandListener listener)
    {
        if (listener == null) {
            return;
        }
        if (commandlisteners != null) {
            if (commandlisteners instanceof CommandListeners) {
                ((CommandListeners)commandlisteners).removeListener(listener);
            }
            else if (commandlisteners == listener) {
                commandlisteners = null;
            }
        }
    }

    /**
     * 当有命令到达的时候调用
     *
     * @param context Command Context
     * @param command 命令数据
     * @param off     起始位置
     * @param len     长度
     */
    public boolean fireCommandEvent(CommandContext context, byte[] command, int off, int len)
    {
        if (commandlisteners != null) {
            commandlisteners.onCommand(context, command, off, len);
            return true;
        }
        return false;
    }


    /**
     * 添加Context监听者
     *
     * @param listener
     */
    public void addContextListener(CommandContextListener listener)
    {
        if (listener == null) {
            return;
        }
        if (contextlisteners == null) {
            contextlisteners = listener;
        }
        else if (contextlisteners instanceof CommandContextListeners) {
            ((CommandContextListeners)contextlisteners).addListener(listener);
        }
        else {
            contextlisteners = new CommandContextListeners(contextlisteners);
            ((CommandContextListeners)contextlisteners).addListener(listener);
        }
    }

    /**
     * 返回Context侦听者
     *
     * @return BaseContextListener[]
     */
    public List<CommandContextListener> getContextListeners()
    {
        if (contextlisteners != null) {
            if (contextlisteners instanceof CommandContextListeners) {
                return ((CommandContextListeners)contextlisteners).getListeners();
            }
            else {
                return Collections.singletonList(contextlisteners);
            }
        }
        return Collections.emptyList();
    }

    /**
     * 设置Context侦听者
     *
     * @param listeners context侦听者
     */
    public void setContextListeners(List<CommandContextListener> listeners)
    {
        if (listeners == null || listeners.isEmpty()) {
            return;
        }
        this.contextlisteners = new CommandContextListeners(listeners);
    }

    /**
     * 删除Context监听者
     *
     * @param listener Context监听者
     */
    public void removeContextListener(CommandContextListener listener)
    {
        if (listener == null) {
            return;
        }
        if (contextlisteners != null) {
            if (contextlisteners instanceof CommandContextListeners) {
                ((CommandContextListeners)contextlisteners).removeListener(listener);
            }
            else if (contextlisteners == listener) {
                contextlisteners = null;
            }
        }
    }

    /**
     * 处理BaseContext来的事件
     *
     * @param context 上下文
     * @param action  动作种类
     * @return 是否结束处理
     */
    public boolean fireEvent(CommandContext context, int action)
    {
        if (contextlisteners != null) {
            return contextlisteners.handle(context, action);
        }
        return false;
    }

}
