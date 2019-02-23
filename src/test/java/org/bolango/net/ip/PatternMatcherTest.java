package org.bolango.net.ip;

import org.bolango.tools.nutlet.Nutlet;

/**
 * @author fengyun
 * @version 1.00 2006-12-12 22:23:06
 */

public class PatternMatcherTest extends Nutlet
{
    public void testPatternMatcher0() throws Exception
    {
        PatternMatcher matcher = new PatternMatcher("1080::8:800:200C:*");
        assertTrue(matcher.isMatch("1080::8:800:200C:11"));
    }

    public void testPatternMatcher1() throws Exception
    {
        PatternMatcher matcher = new PatternMatcher("1080::8:800:200C:1-2000");
        assertFalse(matcher.isMatch("1080::8:800:200C:2100"));
    }

    public void testPatternMatcher2() throws Exception
    {
        PatternMatcher matcher = new PatternMatcher("192.168.6.33,44");
        for (int i = 0; i < 255; i++) {
            if (i != 33 && i != 44) {
                assertFalse(matcher.isMatch("192.168.6." + i));
            }
        }
        assertTrue(matcher.isMatch("192.168.6.33"));
        assertTrue(matcher.isMatch("192.168.6.44"));
    }

    public void testPatternMatcher3() throws Exception
    {
        PatternMatcher matcher = new PatternMatcher("192.168.6");
        for (int i = 0; i < 255; i++) {
            assertTrue(matcher.isMatch("192.168.6." + i));
        }
    }

    public void testPatternMatcher4() throws Exception
    {
        PatternMatcher matcher = new PatternMatcher("192.168.6.1?5");
        for (int i = 0; i < 255; i++) {
            if (i / 100 == 1 && i % 10 == 5) {
                continue;
            }
            assertFalse(matcher.isMatch("192.168.6." + i));
        }

        for (int i = 0; i < 255; i++) {
            if (i / 100 == 1 && i % 10 == 5) {
                assertTrue(matcher.isMatch("192.168.6." + i));
            }
        }
    }

    public void testPatternMatcher5() throws Exception
    {
        PatternMatcher matcher = new PatternMatcher("192.168.6.1*");
        for (int i = 0; i < 255; i++) {
            if (i / 100 == 1 || i / 10 == 1) {
                continue;
            }
            assertFalse(matcher.isMatch("192.168.6." + i));
        }

        for (int i = 0; i < 255; i++) {
            if (i / 100 == 1 || i / 10 == 1) {
                assertTrue(matcher.isMatch("192.168.6." + i));
            }
        }
    }
    
    public void testPatternMatcher6() throws Exception
    {
        PatternMatcher matcher = new PatternMatcher("192.168.6.??5");
        for (int i = 0; i < 255; i++) {
            if (i / 100 > 0 && i % 10 == 5) {
                continue;
            }
            assertFalse(matcher.isMatch("192.168.6." + i));
        }

        for (int i = 0; i < 255; i++) {
            if (i / 100 > 0 && i % 10 == 5) {
                assertTrue(matcher.isMatch("192.168.6." + i));
            }
        }
    }

    public void testPatternMatcher7() throws Exception
    {
        PatternMatcher matcher = new PatternMatcher("::");
        assertTrue(matcher.isMatch("0:0:0:0:0:0:0:0"));
        assertFalse(matcher.isMatch("0:0:0:0:3:0:0:0"));
    }

    public void testPatternMatcher8() throws Exception
    {
        PatternMatcher matcher = new PatternMatcher("888::");
        assertTrue(matcher.isMatch("888:0:0:0:0:0:0:0"));
        assertFalse(matcher.isMatch("888:0:0:0:3:0:0:0"));
    }

}