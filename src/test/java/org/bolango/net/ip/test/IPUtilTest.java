package org.bolango.net.ip.test;

import org.bolango.net.ip.IPUtil;
import org.bolango.tools.nutlet.Nutlet;

/**
 * 测试IPUtil
 *
 * @author fengyun
 * @version 1.00 Mar 30, 2006 11:27:32 AM
 */
public class IPUtilTest extends Nutlet
{
    /**
     * 测试toInts
     */
    public void testToInts() throws Exception
    {
        int[] addr = IPUtil.toInts("192.168.6.233");
        assertNotNull(addr);
    }
}