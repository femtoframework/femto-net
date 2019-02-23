package org.femtoframework.net.socket;

import java.util.ArrayList;
import java.util.List;

import org.bolango.tools.nutlet.Nutlet;
import org.bolango.util.ArrayUtil;

/**
 * 测试SimpleSocketChain
 *
 * @author fengyun
 * @version 1.00 2005-2-28 14:21:28
 */
public class SimpleSocketChainTest extends Nutlet
{
    /**
     * 测试handleNext
     */
    public void testHandleNext() throws Exception
    {
        SocketValve[] valves = new SocketValve[1];
        final boolean invoked[] = new boolean[1];
        valves[0] = new SocketValve()
        {

            /**
             * 处理连接
             *
             * @param context 连接上下文
             * @param chain   阀门控制链
             */
            public void handle(SocketContext context, SocketChain chain)
            {
                invoked[0] = true;
            }

            /**
             * 返回对象名称
             *
             * @return 对象名称
             */
            public String getName()
            {
                return null;
            }
        };
        List<SocketValve> list = new ArrayList<SocketValve>(4);
        ArrayUtil.addAll(list, valves);
        SimpleSocketChain chain = new SimpleSocketChain(list);
        chain.handleNext(null);
    }
}