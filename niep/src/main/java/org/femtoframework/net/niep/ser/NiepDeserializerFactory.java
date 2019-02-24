package org.femtoframework.net.niep.ser;

import java.util.HashMap;
import java.util.Map;

import org.femtoframework.io.ser.ObjectDeserializer;
import org.femtoframework.io.ser.ObjectDeserializerFactory;
import org.femtoframework.pattern.ext.BaseFactory;

/**
 * NIEP反串行化
 *
 * @author fengyun
 * @version 1.00 2004-8-3 16:20:43
 */
public class NiepDeserializerFactory extends BaseFactory<ObjectDeserializer>
        implements ObjectDeserializerFactory
{
    private Map<String, ObjectDeserializer> map = new HashMap<String, ObjectDeserializer>();

    {
        ObjectDeserializer deserializer = new MapDeserializer();
        add("java.util.Map", deserializer);
        add("java.util.HashMap", deserializer);
        add("java.util.Hashtable", deserializer);

        deserializer = new ListDeserializer();
        add("java.util.List", deserializer);
        add("java.util.ArrayList", deserializer);
        add("java.util.Vector", deserializer);
    }

    /**
     * 注册ObjectDeserializer
     *
     * @param className    类名
     * @param deserializer 反串行化器
     */
    public void add(String className, ObjectDeserializer deserializer)
    {
        super.add(className, deserializer);
    }

    /**
     * 注册ObjectDeserializer
     *
     * @param clazz        类
     * @param deserializer 反串行化器
     */
    public void add(Class clazz, ObjectDeserializer deserializer)
    {
        add(clazz.getName(), deserializer);
    }
}
