package org.femtoframework.net.comm;

import org.femtoframework.parameters.Parameters;

import java.io.IOException;


/**
 * 连接工厂
 *
 * @author fengyun
 * @version 1.00 2005-5-5 22:45:48
 */
public interface ConnectionFactory
{
    /**
     * 创建连接（只配置基本的信息，比如说Socket）
     *
     * @param host       主机地址
     * @param port       主机端口
     * @param parameters 创建连接需要的参数
     * @return
     * @throws IOException 创建连接异常
     */
    Connection createConnection(String host, int port, Parameters parameters)
        throws IOException;
}
