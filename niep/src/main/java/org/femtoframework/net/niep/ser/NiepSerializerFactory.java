package org.femtoframework.net.niep.ser;

import java.util.HashMap;
import java.util.Map;

import org.femtoframework.io.ser.ObjectSerializer;
import org.femtoframework.io.ser.ObjectSerializerFactory;
import org.femtoframework.lang.reflect.Reflection;
import org.femtoframework.pattern.ext.BaseFactory;

/**
 * NIEP串行化工厂
 *
 * @author fengyun
 * @version 1.00 2004-8-3 16:20:11
 */
public class NiepSerializerFactory extends BaseFactory<ObjectSerializer>
    implements ObjectSerializerFactory
{
    /**
     * 映射
     */
    private Map<String, ObjectSerializer> map = new HashMap<String, ObjectSerializer>();

    {
        ObjectSerializer serializer = new MapSerializer();
        add("java.util.Map", serializer);
        add("java.util.HashMap", serializer);
        add("java.util.Hashtable", serializer);

        serializer = new ListSerializer();
        add("java.util.List", serializer);
        add("java.util.ArrayList", serializer);
        add("java.util.Vector", serializer);
    }
    /**
     * 注册串行化器
     *
     * @param className  类名
     * @param serializer 串行化器
     */
    public void add(String className, ObjectSerializer serializer)
    {
        super.add(className, serializer);
    }

    /**
     * 注册串行化器
     *
     * @param className       类名
     * @param serializerClass 串行化器类名
     */
    public void add(String className, String serializerClass)
    {
        add(className, (ObjectSerializer) Reflection.newInstance(serializerClass));
    }
}
