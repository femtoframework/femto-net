package org.femtoframework.io.ser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.femtoframework.lang.reflect.Reflection;

/**
 * 简单的串行化器实现
 *
 * @author fengyun
 * @version 1.00 2004-8-2 13:53:20
 */
public class SimpleSerializerFactory
    extends AbstractSerializerFactory
{
    /**
     * 映射
     */
    private Map<String, ObjectSerializer> map = new HashMap<String, ObjectSerializer>();

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
     * 注册串行化器
     *
     * @param className  类名
     * @param serializer 串行化器
     */
    public void register(String className, ObjectSerializer serializer)
    {
        map.put(className, serializer);
    }

    /**
     * 注册串行化器
     *
     * @param className       类名
     * @param serializerClass 串行化器类名
     */
    public void register(String className, String serializerClass)
    {
        map.put(className, (ObjectSerializer) Reflection.newInstance(serializerClass));
    }

    /**
     * 返回所有的类名
     *
     * @return Iterator
     */
    public Iterator getClassNames()
    {
        return map.keySet().iterator();
    }
}
