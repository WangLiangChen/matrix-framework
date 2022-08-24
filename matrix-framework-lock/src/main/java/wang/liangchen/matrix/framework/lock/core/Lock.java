package wang.liangchen.matrix.framework.lock.core;

/**
 * @author Liangchen.Wang 2022-08-22 22:28
 */
public interface Lock {
    boolean lock();

    void unlock();

    LockConfiguration lockConfiguration();

    String lockKey();
}
