package org.femtoframework.net.nio.cmd;

import org.bolango.tools.nutlet.Nutlet;

/**
 * @author fengyun
 * @version 1.00 2005-1-2 22:19:01
 */

public class CommandStrayLineExceptionTest extends Nutlet
{
    public void testCommandStrayLineException() throws Exception
    {
        CommandStrayLineException ex = new CommandStrayLineException();
        assertEquals("Stray line", ex.getMessage());
    }
}