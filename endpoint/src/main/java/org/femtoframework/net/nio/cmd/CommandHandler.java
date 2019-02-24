package org.femtoframework.net.nio.cmd;

import java.util.EventListener;
import java.util.List;

/**
 * 根据Ascii格式的协议特点，抽象的命令处理器
 *
 * @author fengyun
 * @version 1.00 2005-1-2 15:12:50
 */
public interface CommandHandler
    extends EventListener
{
    /**
     * 添加Command监听者
     *
     * @param listener
     */
    void addCommandListener(CommandListener listener);

    /**
     * 返回Command侦听者
     *
     * @return BaseCommandListener[]
     */
    List<CommandListener> getCommandListeners();

    /**
     * 删除Command监听者
     *
     * @param listener Command监听者
     */
    void removeCommandListener(CommandListener listener);


    /**
     * 添加Context监听者
     *
     * @param listener
     */
    void addContextListener(CommandContextListener listener);

    /**
     * 返回Context侦听者
     *
     * @return BaseContextListener[]
     */
    List<CommandContextListener> getContextListeners();

    /**
     * 删除Context监听者
     *
     * @param listener Context监听者
     */
    void removeContextListener(CommandContextListener listener);
}
