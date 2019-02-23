package org.bolango.net.ip;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.bolango.tools.nutlet.Nutlet;

/**
 * @author fengyun
 * @version 1.00 2006-12-12 23:14:18
 */

public class AccessControlBeanTest extends Nutlet
{
    public void testAddAllow() throws Exception
    {
        AccessControlBean bean = new AccessControlBean();
        bean.setAllowAll(true);
        bean.setDeny("192.168.6.33,44;192.168.62");
        for (int i = 0; i < 256; i++) {
            if (i != 33 && i != 44) {
                assertTrue(bean.accept("192.168.6." + i));
            }
        }
        assertFalse(bean.accept("192.168.6.33"));
        assertFalse(bean.accept("192.168.6.44"));

        for (int i = 0; i < 256; i++) {
            assertFalse(bean.accept("192.168.62." + i));
        }
    }

    public void testAddAllow1() throws Exception
    {
        AccessControlBean bean = new AccessControlBean();
        bean.setAllowAll(true);
        bean.setDeny("192.168.6.33,44;192.168.62");
        for (int i = 0; i < 256; i++) {
            if (i != 33 && i != 44) {
                assertTrue(bean.accept(
                    new InetSocketAddress(InetAddress.getByName("192.168.6." + i), 0)));
            }
        }
        assertFalse(bean.accept("192.168.6.33"));
        assertFalse(bean.accept("192.168.6.44"));

        for (int i = 0; i < 256; i++) {
            assertFalse(bean.accept(new InetSocketAddress(InetAddress.getByName("192.168.62." + i), 0)));
        }
    }
}