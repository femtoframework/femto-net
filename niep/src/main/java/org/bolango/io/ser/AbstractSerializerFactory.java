package org.femtoframework.io.ser;

/**
 * 抽象的Serializer工厂
 *
 * @author fengyun
 * @version 1.00 2004-8-2 13:49:07
 */
public abstract class AbstractSerializerFactory
    implements ObjectSerializerFactory
{
    /**
     * 根据类名返回对象串行化器
     *
     * @param obj 对象
     * @return
     */
    public ObjectSerializer getSerializer(Object obj)
    {
        Class clazz = obj.getClass();
        return getSerializer(clazz);
    }

    /**
     * 根据类名返回对象串行化器
     *
     * @param clazz 类名
     * @return
     */
    public ObjectSerializer getSerializer(Class clazz)
    {
        return getSerializer(clazz.getName());
    }
}
