package org.femtoframework.io.ser;

/**
 * 抽象的Deserializer工厂
 *
 * @author fengyun
 * @version 1.00 2004-8-2 13:51:04
 */
public abstract class AbstractDeserializerFactory
    implements ObjectDeserializerFactory
{
    /**
     * 根据类返回反串行化器
     *
     * @param clazz 类
     * @return
     */
    public ObjectDeserializer getDeserializer(Class clazz)
    {
        return getDeserializer(clazz.getName());
    }
}
