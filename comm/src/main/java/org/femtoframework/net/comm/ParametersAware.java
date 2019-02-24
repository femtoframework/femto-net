package org.femtoframework.net.comm;

import org.femtoframework.parameters.Parameters;

/**
 * 需要连接参数的，用于像报文协议传递连接参数
 *
 * @author fengyun
 * @version 1.00 2005-5-7 21:07:41
 */
public interface ParametersAware
{
    /**
     * 设置连接参数集合
     *
     * @param parameters 连接参数集合
     */
    void setParameters(Parameters parameters);
}
