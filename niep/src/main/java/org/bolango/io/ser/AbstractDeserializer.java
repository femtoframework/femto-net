package org.femtoframework.io.ser;

import java.io.IOException;
import java.io.ObjectInputStream;


/**
 * 抽象翻船行化器
 *
 * @author fengyun
 * @version 1.00 2004-8-2 13:47:56
 */
public abstract class AbstractDeserializer
    implements ObjectDeserializer
{
    /**
     * 输出对象
     *
     * @param ois       对象输入流
     * @param className 类名
     * @throws java.io.IOException
     */
    public Object demarshal(ObjectInputStream ois, String className)
        throws IOException, ClassNotFoundException
    {
        Thread thread = Thread.currentThread();
        ClassLoader loader = thread.getContextClassLoader();
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        Class clazz = loader.loadClass(className);
        return demarshal(ois, clazz);
    }
}
