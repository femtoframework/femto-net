package org.femtoframework.frame.niep.ser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.femtoframework.io.ser.AbstractDeserializer;
import org.femtoframework.util.CollectionUtil;

/**
 * List反串行化
 *
 * @author fengyun
 * @version 1.00 2004-8-3 16:03:23
 */
public class ListDeserializer
    extends AbstractDeserializer
{
    /**
     * 输出对象
     *
     * @param ois   对象输入流
     * @param clazz 类名
     * @throws java.io.IOException
     */
    public Object demarshal(ObjectInputStream ois, Class clazz)
        throws IOException, ClassNotFoundException
    {
        int size = ois.readInt();
        if (size < 0) {
            return null;
        }
        else if (size == 0) {
            return CollectionUtil.EMPTY_LIST;
        }
        else {
            List<Object> list = new ArrayList<Object>(size);
            for (int i = 0; i < size; i++) {
                list.add(ois.readObject());
            }
            return list;
        }
    }
}
