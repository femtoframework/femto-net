package org.femtoframework.net.comm;

import java.io.Closeable;
import java.io.IOException;

/**
 * 通讯客户端，管理多个连接
 *
 * @author fengyun
 * @version 1.00 2005-3-30 17:48:22
 */
public interface CommClient extends Closeable
{
    /**
     * 返回该通讯客户端是否存在Alive的连接
     *
     * @return
     */
    public boolean isAlive();

    /**
     * 强制建立连接，确保通道畅通
     *
     * @return 有效的连接个数
     * @throws IOException 异常
     */
    public int connect() throws IOException;

    /**
     * 添加连接
     *
     * @param conn 连接
     * @return 是否添加成功
     */
    public boolean addConnection(Connection conn);

    /**
     * 删除连接
     *
     * @param conn 连接
     * @return 是否已经删除
     */
    public boolean removeConnection(Connection conn);
}
