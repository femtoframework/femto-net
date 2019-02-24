package org.femtoframework.io.ser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 对象串行化器（用于对某一类对象的串行化）
 *
 * @author fengyun
 * @version 1.00 2004-8-2 13:32:51
 */
public interface ObjectSerializer
    extends Serializable
{
    /**
     * 输出对象
     *
     * @param oos 对象输出流
     * @param obj 对象
     * @throws IOException
     */
    void marshal(ObjectOutputStream oos, Object obj) throws IOException;
}
