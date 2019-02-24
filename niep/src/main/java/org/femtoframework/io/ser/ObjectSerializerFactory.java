package org.femtoframework.io.ser;


import org.femtoframework.pattern.Factory;

/**
 * Object串行化器工厂
 *
 * @author fengyun
 * @version 1.00 2004-8-2 13:38:47
 */
public interface ObjectSerializerFactory extends Factory<ObjectSerializer>
{
    /**
     * 根据类名返回对象串行化器
     *
     * @param className 类名
     */
    default ObjectSerializer getSerializer(String className) {
        return get(className);
    }

    /**
     * 根据类名返回对象串行化器
     *
     * @param clazz 类名
     */
    default ObjectSerializer getSerializer(Class clazz) {
        return getSerializer(clazz.getName());
    }

    /**
     * 根据类名返回对象串行化器
     *
     * @param obj 对象
     */
    default ObjectSerializer getSerializer(Object obj)
    {
        if (obj == null) {
            throw new IllegalArgumentException("Null object");
        }
        Class clazz = obj.getClass();
        return getSerializer(clazz);
    }
}
