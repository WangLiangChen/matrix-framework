package wang.liangchen.matrix.framework.commons.lock;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author LiangChen.Wang
 */
public enum LocalLockUtil {
    /**
     * instance
     */
    INSTANCE;

    public <R> R executeInReadWriteLock(LockReader<R> lockReader, LockWriter<R> lockWriter) throws RuntimeException {
        //第一级不加锁读取
        LockReader.LockValueWrapper<R> lockValueWrapper = lockReader.read();
        if (lockValueWrapper != null) {
            return lockValueWrapper.get();
        }
        // 创建读写锁
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        Lock readLock = readWriteLock.readLock();
        Lock writeLock = readWriteLock.writeLock();
        // 第二级加读锁读取
        readLock.lock();
        try {
            lockValueWrapper = lockReader.read();
            if (lockValueWrapper != null) {
                //finally release read lock
                return lockValueWrapper.get();
            }
            //Must release read lock before acquiring write lock，can't upgrade write lock from read lock
            readLock.unlock();
            //第三级加写锁写入
            writeLock.lock();
            try {
                //Recheck state because another thread might have acquired write lock and changed state before we did.
                lockValueWrapper = lockReader.read();
                if (lockValueWrapper != null) {
                    return lockValueWrapper.get();
                }
                return lockWriter.write();
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
