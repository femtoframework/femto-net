package org.femtoframework.net.niep.ser;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.femtoframework.io.ser.ObjectSerializer;

/**
 * java.util.Map串行化器
 *
 * @author fengyun
 * @version 1.00 2004-8-3 16:12:44
 */
public class MapSerializer implements ObjectSerializer
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
        Map map = (Map) obj;
        int size = map.size();
        oos.writeInt(size);
        if (size > 0) {
            for (Object o : map.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                oos.writeObject(entry.getKey());
                oos.writeObject(entry.getValue());
            }
        }
    }
}
