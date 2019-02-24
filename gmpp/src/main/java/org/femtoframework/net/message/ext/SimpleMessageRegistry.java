package org.femtoframework.net.message.ext;


import org.femtoframework.io.IOUtil;
import org.femtoframework.lang.reflect.Reflection;
import org.femtoframework.net.message.MessageMetadata;
import org.femtoframework.net.message.MessageRegistry;
import org.femtoframework.parameters.PropertyParameters;
import org.femtoframework.util.MessageProperties;
import org.femtoframework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息注册实现
 *
 * @author fengyun
 * @version 1.00 2005-5-21 20:03:58
 * @noinspection Singleton
 */
public class SimpleMessageRegistry implements MessageRegistry
{
    /**
     * 类到Metadata的映射(Class --> Meta)
     */
    private Map<Class, MessageMetadata> class2Meta = new HashMap<>();

    /**
     * 类型到Metadata的映射(Type --> Meta)
     */
    private Map<Integer, MessageMetadata> type2Meta = new HashMap<>(16);

    private static final String PROPERTIES = "META-INF/spec/" + MessageMetadata.class.getName() + ".properties";

    private static Logger logger = LoggerFactory.getLogger(SimpleMessageRegistry.class);

    {
        loadAll();
    }

    private void loadAll()
    {
        InputStream input = null;
        try {
            Enumeration<URL> en = Reflection.getResources(PROPERTIES);
            URL url;

            MessageProperties config = new MessageProperties();
            while (en.hasMoreElements()) {
                url = en.nextElement();

                input = url.openStream();
                config.load(input);

                load(config);
                config.clear();
                IOUtil.close(input);
            }
        }
        catch (IOException e) {
            logger.warn("IOException", e);
            IOUtil.close(input);
        }
    }

    private void load(MessageProperties config)
    {
        PropertyParameters parameters = new PropertyParameters(config);
        String[] array = parameters.getStrings("list");
        if (array != null) {
            for (String str : array) {
                if (StringUtil.isInvalid(str)) {
                    continue;
                }

                int type = parameters.getInt(str + ".type", -1);
                String clazz = parameters.getString("class", null);
                String listener = parameters.getString("listener", null);
                if (type == -1 || clazz == null || listener == null) {
                    logger.warn("ingnore invalid message registry:" + str);
                    continue;
                }
                SimpleMessageMetadata metadata = new SimpleMessageMetadata();
                metadata.setType(type);
                metadata.setMessageClass(clazz);
                metadata.setListener(listener);

                addMetadata(metadata);
            }
        }
    }

    /**
     * 根据消息对象返回类型
     *
     * @param message 消息对象
     * @return 如果不存在该类型，返回<code>NO_SUCH_TYPE</code>
     */
    public int getType(Object message)
    {
        MessageMetadata metadata = class2Meta.get(message.getClass());
        return metadata != null ? metadata.getType() : NO_SUCH_TYPE;
    }

    /**
     * 根据消息类型创建一个新的对象
     *
     * @param type 消息类型
     * @return 返回对象
     */
    public Object createMessage(int type)
    {
        MessageMetadata metadata = type2Meta.get(type);
        if (metadata == null) {
            throw new IllegalArgumentException("No such type:" + type);
        }
        return metadata.createMessage();
    }

    /**
     * 根据类型返回消息元数据
     *
     * @param type 消息类型
     * @return 返回对象
     */
    public MessageMetadata getMetadata(int type)
    {
        return type2Meta.get(type);
    }

    /**
     * 根据消息返回消息元数据
     *
     * @param message 消息
     * @return 返回对象
     */
    public MessageMetadata getMetadata(Object message)
    {
        int type = getType(message);
        return type == NO_SUCH_TYPE ? null : getMetadata(type);
    }

    /**
     * 添加消息元数据
     *
     * @param metadata 消息元数据
     * @throws org.femtoframework.lang.reflect.NoSuchClassException
     *          当类不存在的时候抛出
     * @throws org.femtoframework.lang.reflect.NoSuchMethodException
     *          当方法不存在的时候抛出
     */
    public void addMetadata(MessageMetadata metadata)
    {
        class2Meta.put(metadata.getMessageClass(), metadata);
        type2Meta.put(metadata.getType(), metadata);
    }

    /**
     * 删除消息元数据
     *
     * @param type 消息类型
     * @return 删除的消息元数据
     */
    public MessageMetadata removeMetadata(int type)
    {
        MessageMetadata metadata = type2Meta.remove(type);
        if (metadata != null) {
            class2Meta.remove(metadata.getMessageClass());
        }
        return metadata;
    }

    /**
     * 删除消息元数据
     *
     * @param clazz 消息类
     * @return 删除的消息元数据
     */
    public MessageMetadata removeMetadata(Class clazz)
    {
        MessageMetadata metadata = class2Meta.remove(clazz);
        if (metadata != null) {
            type2Meta.remove(metadata.getType());
        }
        return metadata;
    }
}
