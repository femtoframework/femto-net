package org.femtoframework.net.nio.cmd;

/**
 * @author fengyun
 * @version 1.00 2005-1-29 0:29:44
 */
public class SimpleContextListener
    implements CommandContextListener
{
    /**
     * 处理BaseContext来的事件
     *
     * @param context 上下文
     * @param action  动作种类
     * @return 是否结束处理
     */
    public boolean handle(CommandContext context, int action)
    {
        return false;
    }
}
