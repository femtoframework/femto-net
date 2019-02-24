package org.femtoframework.io.ser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.femtoframework.lang.reflect.Reflection;

/**
 * 简单的反串行化器
 *
 * @author fengyun
 * @version 1.00 2004-8-2 13:53:51
 */
public class SimpleDeserializerFactory
    extends AbstractDeserializerFactory
{
    /**
     * 映射
     */
    private Map<String, ObjectDeserializer> map = new HashMap<String, ObjectDeserializer>();

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
     * 注册反串行化器
     *
     * @param className    类名
     * @param deserializer 反串行化器
     */
    public void register(String className, ObjectDeserializer deserializer)
    {
        map.put(className, deserializer);
    }

    /**
     * 注册反串行化器
     *
     * @param className         类名
     * @param deserializerClass 反串行化器类名
     */
    public void register(String className, String deserializerClass)
    {
        map.put(className, (ObjectDeserializer) Reflection.newInstance(deserializerClass));
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
