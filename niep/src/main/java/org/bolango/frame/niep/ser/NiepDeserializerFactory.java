package org.femtoframework.frame.niep.ser;

import java.util.HashMap;
import java.util.Map;

import org.femtoframework.io.ser.AbstractDeserializerFactory;
import org.femtoframework.io.ser.ObjectDeserializer;

/**
 * NIEP反串行化
 *
 * @author fengyun
 * @version 1.00 2004-8-3 16:20:43
 */
public class NiepDeserializerFactory
    extends AbstractDeserializerFactory
{
    private Map<String, ObjectDeserializer> map = new HashMap<String, ObjectDeserializer>();

    {
        ObjectDeserializer deserializer = new MapDeserializer();
        register("java.util.Map", deserializer);
        register("java.util.HashMap", deserializer);
        register("java.util.Hashtable", deserializer);

        deserializer = new ListDeserializer();
        register("java.util.List", deserializer);
        register("java.util.ArrayList", deserializer);
        register("java.util.Vector", deserializer);
    }

    /**
     * 根据类名返回反串行化器
     *
     * @param className 类名
     * @return
     */
    public ObjectDeserializer getDeserializer(String className)
    {
        return map.get(className);
    }

    /**
     * 注册ObjectDeserializer
     *
     * @param className    类名
     * @param deserializer 反串行化器
     */
    public void register(String className, ObjectDeserializer deserializer)
    {
        map.put(className, deserializer);
    }

    /**
     * 注册ObjectDeserializer
     *
     * @param clazz        类
     * @param deserializer 反串行化器
     */
    public void register(Class clazz, ObjectDeserializer deserializer)
    {
        register(clazz.getName(), deserializer);
    }
}
