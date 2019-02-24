package org.femtoframework.net.comm;

import java.io.IOException;

/**
 * 抽象连接
 *
 * @author fengyun
 * @version 1.00 2005-5-6 23:16:52
 */
public abstract class AbstractConnection
    implements Connection
{
    /**
     * 是否处在激活状态
     */
    private boolean isAlive = false;

    /**
     * 是否还处于激活状态
     *
     * @return 如果还在活动状态返回<code>true</code>，否则返回<code>false</code>
     */
    public boolean isAlive()
    {
        return isAlive;
    }

    /**
     * 将连接标记为已读
     *
     * @param isAlive
     */
    protected void setAlive(boolean isAlive)
    {
        this.isAlive = isAlive;
    }

    /**
     * 连接请求
     *
     * @throws org.femtoframework.net.comm.CommException
     *          如果连接异常
     */
    public final synchronized void connect() throws IOException
    {
        if (!isAlive()) {
            doConnect();
            setAlive(true);
        }
    }

    /**
     * 关闭连接
     *
     * @throws IOException 关闭的时候异常
     */
    public final synchronized void close() throws IOException
    {
        if (isAlive()) {
            doClose();
            setAlive(false);
        }
    }

    /**
     * 连接请求
     *
     * @throws org.femtoframework.net.comm.CommException
     *          如果连接异常
     */
    protected abstract void doConnect() throws IOException;

    /**
     * 关闭连接
     *
     * @throws org.femtoframework.net.comm.CommException
     *          如果连接异常
     */
    protected abstract void doClose() throws IOException;
}
