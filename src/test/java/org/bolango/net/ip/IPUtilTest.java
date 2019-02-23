package org.bolango.net.ip;

import java.net.InetAddress;

import org.bolango.tools.nutlet.Nutlet;

/**
 * 测试IPUtil
 *
 * @author fengyun
 * @version 1.00 2006-12-10 19:15:56
 */
public class IPUtilTest extends Nutlet
{
    /**
     * 测试isValidIPv6
     */
    public void testIsValidIPv6() throws Exception
    {
        assertTrue(IPUtil.isValidIPv6("::"));
        assertTrue(IPUtil.isValidIPv6("1080::8:800:200C:417A"));
        assertTrue(IPUtil.isValidIPv6("::13.1.68.3"));
        assertTrue(IPUtil.isValidIPv6("::FFFF:129.144.52.38"));
        assertFalse(IPUtil.isValidIPv6("13.1.68.3"));
    }

    /**
     * 测试isValid
     */
    public void testIsValid() throws Exception
    {
        assertTrue(IPUtil.isValid("::"));
        assertTrue(IPUtil.isValid("1080::8:800:200C:417A"));
        assertTrue(IPUtil.isValid("::13.1.68.3"));
        assertTrue(IPUtil.isValid("::FFFF:129.144.52.38"));
        assertTrue(IPUtil.isValid("13.1.68.3"));
        assertFalse(IPUtil.isValid("fengyun"));
    }

    public void testToString1() throws Exception
    {
        InetAddress addr = InetAddress.getByName("1080::8:800:200C:417A");
        assertEquals("1080:0:0:0:8:800:200C:417A", IPUtil.toString(addr.getAddress()));
    }

    public void testToString2() throws Exception
    {
        InetAddress addr = InetAddress.getByName("::13.1.68.3");
        assertEquals("13.1.68.3", IPUtil.toString(addr.getAddress()));

        addr = InetAddress.getByName("::FFFF:13.1.68.3");
        assertEquals("13.1.68.3", IPUtil.toString(addr.getAddress()));
    }

    public void testToString3() throws Exception
    {
        InetAddress addr = InetAddress.getByName("13.1.68.3");
        assertEquals("13.1.68.3", IPUtil.toString(addr.getAddress()));
    }

    /**
     * 测试toBytes
     */
    public void testToBytes1() throws Exception
    {
        InetAddress addr = InetAddress.getByName("1080::8:800:200C:417A");
        byte[] bytes = addr.getAddress();
        int[] values = IPUtil.toInts(bytes);
        assertEquals(bytes, IPUtil.toBytes(values));
    }

    /**
     * 测试toBytes
     */
    public void testToBytes2() throws Exception
    {
        InetAddress addr = InetAddress.getByName("13.1.68.3");
        byte[] bytes = addr.getAddress();
        int[] values = IPUtil.toInts(bytes);
        assertEquals(bytes, IPUtil.toBytes(values));
    }

    /**
     * 测试toInt
     */
    public void testToInt() throws Exception
    {
        assertEquals(IPUtil.toInt("13.1.68.3"), IPUtil.toInt("::13.1.68.3"));
        assertEquals(IPUtil.toInt("13.1.68.3"), IPUtil.toInt("::FFFF:13.1.68.3"));
    }
}