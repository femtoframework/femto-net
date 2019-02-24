package org.femtoframework.frame.niep.ser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import org.femtoframework.io.ser.AbstractDeserializer;
import org.femtoframework.util.CollectionUtil;

/**
 * Map反串行化
 *
 * @author fengyun
 * @version 1.00 2004-8-3 16:15:21
 */
public class MapDeserializer
    extends AbstractDeserializer
{
    /**
     * 输出对象
     *
     * @param ois   对象输入流
     * @param clazz 类
     * @throws java.io.IOException
     */
    public Object demarshal(ObjectInputStream ois, Class clazz)
        throws IOException, ClassNotFoundException
    {
        int size = ois.readInt();
        if (size < 0) {
            return null;
        }
        else if (size == 0) {
            return CollectionUtil.EMPTY_MAP;
        }
        else {
            Map<Object, Object> map = new HashMap<Object, Object>(size);
            Object key, value;
            for (int i = 0; i < size; i++) {
                key = ois.readObject();
                value = ois.readObject();
                map.put(key, value);
            }
            return map;
        }
    }
}
