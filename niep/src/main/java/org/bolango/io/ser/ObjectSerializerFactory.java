package org.femtoframework.io.ser;


/**
 * Object串行化器工厂
 *
 * @author fengyun
 * @version 1.00 2004-8-2 13:38:47
 */
public interface ObjectSerializerFactory
{
    /**
     * 根据类名返回对象串行化器
     *
     * @param className 类名
     * @return
     */
    public ObjectSerializer getSerializer(String className);

    /**
     * 根据类名返回对象串行化器
     *
     * @param clazz 类名
     * @return
     */
    public ObjectSerializer getSerializer(Class clazz);

    /**
     * 根据类名返回对象串行化器
     *
     * @param obj 对象
     * @return
     */
    public ObjectSerializer getSerializer(Object obj);
}
