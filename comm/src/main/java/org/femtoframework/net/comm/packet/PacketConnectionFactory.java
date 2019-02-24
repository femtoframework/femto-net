package org.femtoframework.net.comm.packet;

import java.io.IOException;

import org.femtoframework.bean.Initializable;
import org.femtoframework.net.comm.AddressAware;
import org.femtoframework.net.comm.Connection;
import org.femtoframework.net.comm.ConnectionFactory;
import org.femtoframework.net.comm.ParametersAware;
import org.femtoframework.lang.reflect.Reflection;
import org.femtoframework.parameters.Parameters;
import org.femtoframework.pattern.Loggable;
import org.slf4j.Logger;

/**
 * 报文连接工厂
 *
 * @author fengyun
 * @version 1.00 2005-5-8 10:24:18
 */
public class PacketConnectionFactory implements ConnectionFactory
{

    private static final PacketConnectionFactory instance = new PacketConnectionFactory();

    /**
     * 构造
     */
    private PacketConnectionFactory()
    {
    }

    /**
     * 返回实例
     */
    public static PacketConnectionFactory getInstance()
    {
        return instance;
    }

    /**
     * 创建连接（只配置基本的信息，比如说Socket）
     *
     * @param host       主机地址
     * @param port       主机端口
     * @param parameters 创建连接需要的参数
     * @return
     * @throws java.io.IOException 创建连接异常
     */
    public Connection createConnection(String host, int port, Parameters parameters)
        throws IOException
    {
        Class clazz = (Class) parameters.get("connection_class");
        if (clazz == null) {
            throw new IOException("No connection class");
        }

        Connection conn = (Connection) Reflection.newInstance(clazz);
        if (conn instanceof Loggable) {
            Logger log = (Logger) parameters.get("logger");
            if (log != null) {
                ((Loggable) conn).setLogger(log);
            }
        }
        //接口注入
        if (conn instanceof AddressAware) {
            AddressAware aware = (AddressAware) conn;
            aware.setHost(host);
            aware.setPort(port);
        }
        if (conn instanceof ParametersAware) {
            ((ParametersAware) conn).setParameters(parameters);
        }
        //是否需要初始化
        if (conn instanceof Initializable) {
            ((Initializable) conn).initialize();
        }
        return conn;
    }
}
