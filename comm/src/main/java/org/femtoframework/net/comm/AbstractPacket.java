package org.femtoframework.net.comm;


/**
 * 抽象报文
 *
 * @author fengyun
 * @version 1.00 Mar 14, 2002 3:18:34 PM
 * @see Packet
 */
public abstract class AbstractPacket
    implements Packet
{
    /**
     * 内部滑动窗口
     */
    private static int nextId = 1;

    /**
     * 包的标识
     */
    protected int id;

    /**
     * 包类型
     */
    private int type;

    /**
     * 返回下一个标识号
     *
     * @return 下一个标识号
     */
    protected static synchronized int nextId()
    {
        return (nextId++) & 0X7FFFFFFF;
    }

    /**
     * 构造
     *
     * @param type 包类型 [-0x8000000, 0x7FFFFFFF]
     */
    protected AbstractPacket(int type)
    {
        this.type = type;
        this.id = nextId();
    }

    /**
     * 构造
     *
     * @param type 包类型 [-128, 127]
     * @param id   标识
     */
    protected AbstractPacket(int type, int id)
    {
        this.type = type;
        this.id = id;
    }

    /**
     * 返回包类型
     */
    public int getType()
    {
        return type;
    }

    /**
     * 返回标识
     */
    public int getId()
    {
        return id;
    }

    /**
     * 判断两个包是不是等效的
     *
     * @param obj 对象
     */
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (obj instanceof AbstractPacket) {
            AbstractPacket ap = (AbstractPacket) obj;
            return type == ap.type && id == ap.id;
        }
        return false;
    }

    /**
     * 返回哈希码
     */
    public int hashCode()
    {
        return id;
    }

    /**
     * 变成字符串
     *
     * @return 字符串
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Type:");
        sb.append(type);
        sb.append(" ID:");
        sb.append(id);
        return sb.toString();
    }
}
