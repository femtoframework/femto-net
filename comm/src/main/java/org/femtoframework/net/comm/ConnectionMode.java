package org.femtoframework.net.comm;

/**
 * 连接模式
 *
 * @author fengyun
 * @version 1.1 2005-3-30 17:06:50 增加对Enum的扩展，实现Serializable
 *          1.00 Apr 10, 2003 8:56:33 PM
 */
public enum ConnectionMode
{
    READ_ONLY("read_only", 1), WRITE_ONLY("write_only", 2), READ_WRITE("read_write", 3);

    /**
     * 只读模式
     */
    public static final int READ_ONLY_MODE = 1;

    /**
     * 只写模式
     */
    public static final int WRITE_ONLY_MODE = 2;

    /**
     * 可读可写模式
     */
    public static final int READ_WRITE_MODE = 3;

    private String name;
    private int index;

    /**
     * 构造
     *
     * @param name  名称
     * @param index 索引
     */
    private ConnectionMode(String name, int index)
    {
        this.name = name;
        this.index = index;
    }

    public String getName()
    {
        return name;
    }

    public int getIndex()
    {
        return index;
    }

    /**
     * 是否可读
     *
     * @return
     */
    public boolean isReadable()
    {
        return isReadable(index);
    }

    /**
     * 是否可写
     *
     * @return 是否可写
     */
    public boolean isWritable()
    {
        return isWritable(index);
    }

    /**
     * 判断给定的模式，是否可以读
     *
     * @param mode 模式
     * @return
     */
    public static boolean isReadable(int mode)
    {
        return (mode & 0x1) > 0;
    }

    /**
     * 判断给定的模式是否可以写
     *
     * @param mode 模式
     * @return
     */
    public static boolean isWritable(int mode)
    {
        return (mode & 0x2) > 0;
    }

    /**
     * 模式反转
     *
     * @param mode 模式
     * @return 反转后的模式
     */
    public static int reverse(int mode)
    {
        int m = READ_WRITE_MODE;
        switch (mode) {
            case READ_ONLY_MODE:
                m = WRITE_ONLY_MODE;
                break;
            case WRITE_ONLY_MODE:
                m = READ_ONLY_MODE;
                break;
        }
        return m;
    }

    /**
     * 根据模式名称返回模式
     *
     * @param mode 模式名称
     * @return
     */
    public static ConnectionMode toMode(String mode)
    {
        if ("read_only".equals(mode)) {
            return READ_ONLY;
        }
        else if ("write_only".equals(mode)) {
            return WRITE_ONLY;
        }
        else {
            return READ_WRITE;
        }
    }

    private static final ConnectionMode[] values = {READ_ONLY, WRITE_ONLY, READ_WRITE};

    private Object readResolve()
    {
        return values[index - 1];
    }
}
