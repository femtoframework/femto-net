package org.femtoframework.net.nio.cmd;

import java.util.EventListener;

/**
 * 基础的Context事件侦听者
 *
 * @author fengyun
 * @version 1.00 2005-1-2 15:15:09
 */
public interface CommandContextListener
    extends EventListener
{
    /**
     * 处理BaseContext来的事件
     *
     * @param context 上下文
     * @param action  动作种类
     * @return 是否结束处理
     */
    boolean handle(CommandContext context, int action);
}
