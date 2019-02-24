package org.femtoframework.net.nio.cmd;

import java.io.IOException;

/**
 * 遇到命令无效的时候抛出的异常。
 *
 * @author fengyun
 * @version 1.00 2005-1-2 21:43:45
 */
public class CommandStrayLineException
    extends IOException
{
    public CommandStrayLineException()
    {
        super("Stray line");
    }
}
