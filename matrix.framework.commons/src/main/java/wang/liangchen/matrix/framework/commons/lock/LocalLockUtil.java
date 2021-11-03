package wang.liangchen.matrix.framework.commons.lock;

import liangchen.wang.gradf.framework.commons.validator.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

/**
 * @author LiangChen.Wang
 */
public enum LocalLockUtil {
    /**
     * instance
     */
    INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(LocalLockUtil.class);
    private final Map<String, ReadWriteLock> readWriteLockMap = new WeakHashMap<>();
    private final Map<String, StampedLock> stampedLockMap = new WeakHashMap<>();
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();


    public ReadWriteLock obtainReadWriteLock(String key) {
        Assert.INSTANCE.notBlank(key, "参数Key不能为空");
        //第一级 无锁查询
        ReadWriteLock readWriteLock = readWriteLockMap.get(key);
        if (null != readWriteLock) {
            return readWriteLock;
        }
        //第二级 读锁查询
        this.readLock.lock();
        try {
            readWriteLock = readWriteLockMap.get(key);
            if (null != readWriteLock) {
                return readWriteLock;
            }
            //第三级 释放读锁,加写锁写入
            this.readLock.unlock();
            this.writeLock.lock();
            try {
                //加写锁后 二次验证
                readWriteLock = readWriteLockMap.get(key);
                if (null != readWriteLock) {
                    return readWriteLock;
                }
                readWriteLock = new ReentrantReadWriteLock();
                readWriteLockMap.put(key, readWriteLock);
                return readWriteLock;
            } finally {
                //锁降级，释放写锁之前,再次获取读锁，防止在写锁释放的瞬间其它写锁修改数据，保证本次写线程读取数据的原子性，保证返回的是本次写入的内容。
                this.readLock.lock();
                this.writeLock.unlock();
            }
        } finally {
            this.readLock.unlock();
        }
    }

    public StampedLock obtainStampedLock(String key) {
        Assert.INSTANCE.notBlank(key, "参数Key不能为空");
        //第一级 无锁查询
        StampedLock stampedLock = stampedLockMap.get(key);
        if (null != stampedLock) {
            return stampedLock;
        }
        //第二级 读锁查询
        readLock.lock();
        try {
            stampedLock = stampedLockMap.get(key);
            if (null != stampedLock) {
                return stampedLock;
            }
            //第三级 释放读锁,加写锁写入
            readLock.unlock();
            writeLock.lock();
            try {
                //二次验证
                stampedLock = stampedLockMap.get(key);
                if (null != stampedLock) {
                    return stampedLock;
                }
                stampedLock = new StampedLock();
                stampedLockMap.put(key, stampedLock);
                return stampedLock;
            } finally {
                //锁降级，释放写锁之前,再次获取读锁，防止在写锁释放的瞬间其它写锁修改数据，保证本次写线程读取数据的原子性，保证返回的是本次写入的内容。
                readLock.lock();
                writeLock.unlock();
            }
        } finally {
            readLock.unlock();
        }
    }

    public <R> R readWriteInReadWriteLock(String key, LockReader<R> lockReader, LockWriter<R> lockWriter) throws RuntimeException {
        //第一级不加锁读取
        LockReader.LockValueWrapper<R> lockValueWrapper = lockReader.read();
        if (lockValueWrapper != null) {
            R result = lockValueWrapper.get();
            logger.debug("key:{},第一级不加锁,读取成功:{}", key, result);
            return result;
        }
        logger.debug("key:{},第一级不加锁,读取失败", key);
        // 获取个读写锁
        ReadWriteLock readWriteLock = obtainReadWriteLock(key);
        Lock readLock = readWriteLock.readLock();
        Lock writeLock = readWriteLock.writeLock();
        // 第二级加读锁读取
        readLock.lock();
        try {
            lockValueWrapper = lockReader.read();
            if (lockValueWrapper != null) {
                R result = lockValueWrapper.get();
                logger.debug("key:{},第二级加读锁,读取成功:{}", key, result);
                //finally release read lock
                return result;
            }
            logger.debug("key:{},第二级加读锁,读取失败", key);
            //Must release read lock before acquiring write lock，can't upgrade write lock from read lock
            readLock.unlock();
            //第三级加写锁写入
            writeLock.lock();
            try {
                //Recheck state because another thread might have acquired write lock and changed state before we did.
                lockValueWrapper = lockReader.read();
                if (lockValueWrapper != null) {
                    R result = lockValueWrapper.get();
                    logger.debug("key:{},第三级加写锁,验证读取成功:{}", key, result);
                    return result;
                }
                logger.debug("key:{},第三级加写锁,验证读取失败,开始写入", key);
                R result = lockWriter.write();
                logger.debug("key:{},第三级加写锁，写入成功:{}", key, result);
                return result;
            } finally {
                //锁降级遵循 先获取写锁，然后获取读锁，然后释放写锁，再释放读锁.
                //Downgrade by acquiring read lock before releasing write lock
                //still hold read lock，防止当前写锁释放后，调用返回前，有其它线再获取写锁,保证本次写线程读取数据的原子性，保证返回的是本次写入的内容。
                readLock.lock();
                writeLock.unlock();
            }
        } finally {
            readLock.unlock();
        }
    }
}
