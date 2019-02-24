package org.femtoframework.frame.niep.ser;

import java.util.HashMap;
import java.util.Map;

import org.femtoframework.io.ser.AbstractSerializerFactory;
import org.femtoframework.io.ser.ObjectSerializer;

/**
 * NIEP串行化工厂（不依赖基础JAR的实现）
 *
 * @author fengyun
 * @version 1.00 2004-8-3 16:20:11
 */
public class NiepSerializerFactory
    extends AbstractSerializerFactory
{
    /**
     * 映射
     */
    private Map<String, ObjectSerializer> map = new HashMap<String, ObjectSerializer>();

    {
        ObjectSerializer serializer = new MapSerializer();
        register("java.util.Map", serializer);
        register("java.util.HashMap", serializer);
        register("java.util.Hashtable", serializer);

        serializer = new ListSerializer();
        register("java.util.List", serializer);
        register("java.util.ArrayList", serializer);
        register("java.util.Vector", serializer);
    }

    /**
     * 根据类名返回对象串行化器
     *
     * @param className 类名
     * @return
     */
    public ObjectSerializer getSerializer(String className)
    {
        return map.get(className);
    }

    /**
     * 根据类名返回对象串行化器
     *
     * @param obj 对象
     * @return
     */
    public ObjectSerializer getSerializer(Object obj)
    {
        Class clazz = obj.getClass();
        return getSerializer(clazz.getName());
    }

    /**
     * 注册ObjectSerializer
     *
     * @param className  类名
     * @param serializer 串行化器
     */
    public void register(String className, ObjectSerializer serializer)
    {
        map.put(className, serializer);
    }
}
