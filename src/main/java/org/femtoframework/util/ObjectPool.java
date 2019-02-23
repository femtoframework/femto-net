package org.femtoframework.util;

/**
 * 固定大小的对象池
 *
 * @author fengyun
 * @version 1.0
 */
public class ObjectPool<V>
{
    /**
     * 默认池大小（128）
     */
    public static final int DEFAULT_POOL_SIZE = 128;

    /**
     * 存放对象的数组（对象池）
     */
    private V pool[];

    /**
     * 对象池最大值
     */
    private int max;

    /**
     * 对象池顶位置
     */
    private int maxOffset;

    /**
     * 栈顶位置
     */
    private int current = -1;

    /**
     * 对象锁
     */
    private final Object lock;

    /**
     * 构建默认大小的对象池
     */
    public ObjectPool()
    {
        this(DEFAULT_POOL_SIZE);
    }

    /**
     * 构建大小为max的对象池
     *
     * @param max 池大小
     */
    public ObjectPool(int max)
    {
        this.max = max;
        this.maxOffset = max - 1;
        pool = (V[])new Object[max];
        lock = new Object();
    }

    /**
     * 把对象o放入对象池中
     *
     * @param o
     */
    public void put(V o)
    {
        synchronized (lock) {
            if (current < maxOffset) {
                current += 1;
                pool[current] = o;
            }
        }
    }

    /**
     * 从对象池中取出对象
     */
    public V get()
    {
        V item = null;
        synchronized (lock) {
            if (current >= 0) {
                item = pool[current];
                current -= 1;
            }
        }
        return item;
    }

    /**
     * 是否为空对象池
     *
     * @return boolean
     */
    public boolean isEmpty()
    {
        return size() == 0;
    }

    /**
     * 取得对象池大小
     *
     * @return int
     */
    public int size()
    {
        return max;
    }
}
