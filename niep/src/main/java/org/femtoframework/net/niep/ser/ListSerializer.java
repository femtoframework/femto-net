package org.femtoframework.net.niep.ser;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import org.femtoframework.io.ser.ObjectSerializer;

/**
 * java.util.List的串行化器
 *
 * @author fengyun
 * @version 1.00 2004-8-3 15:38:40
 */
public class ListSerializer implements ObjectSerializer
{
    /**
     * 输出对象
     *
     * @param oos 对象输出流
     * @param obj 对象
     * @throws java.io.IOException
     */
    public void marshal(ObjectOutputStream oos, Object obj) throws IOException
    {
        List list = (List) obj;
        int size = list.size();
        oos.writeInt(size);
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                Object value = list.get(i);
                oos.writeObject(value);
            }
        }
    }
}
