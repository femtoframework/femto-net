package org.femtoframework.net.nio.cmd;

import java.util.List;

import org.femtoframework.pattern.EventListeners;

/**
 * 基础的Context侦听者集合
 *
 * @version 1.00 2004-9-29 14:59:02
 */
public class CommandContextListeners extends EventListeners<CommandContextListener>
    implements CommandContextListener
{
    /**
     * 构造
     */
    public CommandContextListeners()
    {
    }

    /**
     * 构造
     *
     * @param listener 处理器
     */
    public CommandContextListeners(CommandContextListener listener)
    {
        super(listener);
    }

    public CommandContextListeners(List<CommandContextListener> listeners)
    {
        super(listeners);
    }

    /**
     * @param context 上下文
     * @param action  动作种类
     * @return 是否退出
     */
    public boolean handle(CommandContext context, int action)
    {
        List<CommandContextListener> listeners = getListeners();
        boolean ret = false;

        for (int i = 0; i < listeners.size(); i++) {
            CommandContextListener listener = listeners.get(i);
//            if (listener instanceof Activable) {
//                if (!((Activable)listener).isActive()) {
//                    continue;
//                }
//            }
            ret = listener.handle(context, action);
            if (ret) {
                break;
            }
        }
        return ret;
    }
}

