package org.femtoframework.io.ser;


import org.femtoframework.pattern.Factory;

/**
 * 对象反串行化工厂
 *
 * @author fengyun
 * @version 1.00 2004-8-2 13:44:23
 */
public interface ObjectDeserializerFactory extends Factory<ObjectDeserializer>
{
    /**
     * 根据类名返回反串行化器
     *
     * @param className 类名
     */
    default ObjectDeserializer getDeserializer(String className) {
        return get(className);
    }

    /**
     * 根据类返回反串行化器
     *
     * @param clazz 类
     */
    default ObjectDeserializer getDeserializer(Class clazz) {
        return getDeserializer(clazz.getName());
    }
}
