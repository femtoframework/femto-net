package org.femtoframework.nio;

import org.femtoframework.util.ObjectPool;

import java.nio.ByteBuffer;

/**
 * ByteBuffer连接池
 *
 * @author fengyun
 * @version 1.1 增加了32K的大型Buffer
 *          1.00 2004-8-18 20:28:32
 */
public class ByteBufferPool
{
    public static final int SIZE_512 = 512;
    public static final int SIZE_1024 = 1024;
    public static final int SIZE_2048 = 2048;
    public static final int SIZE_4096 = 4096;
    public static final int SIZE_8192 = 8192;

    public static final int SIZE_32K = 32 * 1024;

    public static final int DEFAULT_BUFFER_SIZE = SIZE_1024;

    //回收池 512
    private static ObjectPool pool0 = new ObjectPool(64);

    //回收池 1K
    private static ObjectPool pool1 = new ObjectPool(512);

    //回收池 2K
    private static ObjectPool pool2 = new ObjectPool(64);

    //回收池 4K
    private static ObjectPool pool3 = new ObjectPool(32);

    //回收池 8K
    private static ObjectPool pool4 = new ObjectPool(32);

    //回收池 32K
    private static ObjectPool pool5 = new ObjectPool(4);

    /**
     * 从Buffer Pool 中取出
     */
    public static ByteBuffer allocate()
    {
        ByteBuffer buffer = (ByteBuffer)pool1.get();
        if (buffer == null) {
            buffer = ByteBuffer.wrap(new byte[SIZE_1024]);
        }
        return buffer;
    }

    /**
     * 从Buffer Pool 中取出
     *
     * @param capacity 容积
     */
    public static ByteBuffer allocate(int capacity)
    {
        ObjectPool pool = getPool(capacity);
        ByteBuffer buffer = null;
        if (pool != null) {
            buffer = (ByteBuffer)pool.get();
        }
        if (buffer == null) {
            buffer = ByteBuffer.wrap(new byte[capacity]);
        }
        return buffer;
    }

    /**
     * 从Buffer Pool 中取出
     *
     * @param size 当前的大小，根据大小选取合适的ByteBuffe
     */
    public static ByteBuffer allocateBySize(int size)
    {
        if (size > SIZE_32K) {
            return ByteBuffer.wrap(new byte[size]);
        }
        int capacity = SIZE_8192;
        if (size <= SIZE_512) {
            capacity = SIZE_512;
        }
        else if (size <= SIZE_1024) {
            capacity = SIZE_1024;
        }
        else if (size <= SIZE_2048) {
            capacity = SIZE_2048;
        }
        else if (size <= SIZE_4096) {
            capacity = SIZE_4096;
        }
        else if (size <= SIZE_8192) {
            capacity = SIZE_8192;
        }
        else {
            capacity = SIZE_32K;
        }

        return allocate(capacity);
    }

    /**
     * 是否拥有回收池
     *
     * @param capacity 容积
     * @return 回收池
     */
    public static boolean hasPool(int capacity)
    {
        boolean hasPool = false;
        switch (capacity) {
            case SIZE_512:
            case SIZE_1024:
            case SIZE_2048:
            case SIZE_4096:
            case SIZE_8192:
            case SIZE_32K:
                hasPool = true;
                break;
        }
        return hasPool;
    }

    private static final ObjectPool getPool(int capacity)
    {
        ObjectPool pool = null;
        switch (capacity) {
            case SIZE_512:
                pool = pool0;
                break;
            case SIZE_1024:
                pool = pool1;
                break;
            case SIZE_2048:
                pool = pool2;
                break;
            case SIZE_4096:
                pool = pool3;
                break;
            case SIZE_8192:
                pool = pool4;
                break;
            case SIZE_32K:
                pool = pool5;
        }
        return pool;
    }

    /**
     * 回收ByteBuffer
     */
    public static void recycle(ByteBuffer buffer)
    {
        if (buffer == null) {
            return;
        }

        buffer.clear();
        int capacity = buffer.capacity();
        ObjectPool pool = getPool(capacity);
        if (pool != null) {
            pool.put(buffer);
        }
    }

}
