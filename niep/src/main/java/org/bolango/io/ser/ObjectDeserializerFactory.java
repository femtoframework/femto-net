package org.femtoframework.io.ser;


/**
 * 对象反串行化工厂
 *
 * @author fengyun
 * @version 1.00 2004-8-2 13:44:23
 */
public interface ObjectDeserializerFactory
{
    /**
     * 根据类名返回反串行化器
     *
     * @param className 类名
     * @return
     */
    public ObjectDeserializer getDeserializer(String className);

    /**
     * 根据类返回反串行化器
     *
     * @param clazz 类
     * @return
     */
    public ObjectDeserializer getDeserializer(Class clazz);
}
