package org.femtoframework.net.comm.packet;

import org.femtoframework.bean.info.BeanInfo;
import org.femtoframework.bean.info.BeanInfoUtil;
import org.femtoframework.bean.info.PropertyInfo;
import org.femtoframework.parameters.Parameters;
import org.femtoframework.text.NamingConvention;

import java.util.*;

/**
 * 连接参数代理，从CommClient对象中获取参数（相比较BeanParameters带有自动Cache的功能）
 *
 * @author fengyun
 * @version 1.00 2005-5-7 20:59:07
 */
public class ConnectionParameters extends AbstractMap<String, Object> implements Parameters<Object>
{
    /**
     * 客户端
     */
    private Object client;

    private BeanInfo beanInfo;

    /**
     * 附加信息
     */
    private Map<String, Object> extra = new HashMap<String, Object>(8);

    /**
     * 构建
     *
     * @param client
     */
    public ConnectionParameters(Object client)
    {
        this.client = client;
        this.beanInfo = BeanInfoUtil.getBeanInfo(client.getClass());
    }

    /**
     * 返回对象型参数
     *
     * @param key 键值
     * @return 如果找不到返回是<code>#DEFAULT_OBJECT</code>
     */
    public Object get(Object key)
    {
        Object value = extra.get(key);
        if (value != null) {
            return value;
        }

        PropertyInfo propertyInfo = beanInfo.getProperty(String.valueOf(key));
        if (propertyInfo != null && propertyInfo.isReadable()) {
            return propertyInfo.invokeGetter(client);
        }
        return null;
    }

    /**
     * 设置对象型参数
     *
     * @param key   键值
     * @param value 对象型
     */
    public Object put(String key, Object value)
    {
        PropertyInfo propertyInfo = beanInfo.getProperty(String.valueOf(key));
        if (propertyInfo != null && propertyInfo.isWritable()) {
            propertyInfo.invokeSetter(client, value);
            return null;
        }
        else {
            return extra.put(key, value);
        }
    }

    /**
     * 删除参数
     *
     * @param name 删除参数
     * @return 如果存在该名称的参数，返回删除的参数；否则返回<code>null</code>
     */
    public Object remove(Object name)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        HashMap<String, Object> allEntries = new HashMap<>();
        allEntries.putAll(extra);
        Collection<PropertyInfo> properties = beanInfo.getProperties();
        if (properties != null && !properties.isEmpty()) {
            for(PropertyInfo propertyInfo : properties) {
                String name = propertyInfo.getName();
                Object value = propertyInfo.invokeGetter(client);
                allEntries.put(NamingConvention.format(name), value);
            }
        }
        return allEntries.entrySet();
    }
}
