package wang.liangchen.matrix.framework.data.util;

import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.data.dao.IDBLock;
import wang.liangchen.matrix.framework.springboot.api.ILock;
import wang.liangchen.matrix.framework.springboot.context.BeanLoader;

import java.util.function.Supplier;

/**
 * @author LiangChen.Wang
 */
public enum DbLockUtil {
    //
    INSTANCE;

    public ILock obtainLock() {
        IDBLock dbLock = BeanLoader.INSTANCE.getBean(IDBLock.class);
        Assert.INSTANCE.notNull(dbLock, "IDbLock does not exists");
        return dbLock;
    }

    public void executeInLock(String lockKey, Runnable callback) {
        ILock lock = obtainLock();
        lock.executeInLock(lockKey, callback);
    }

    public <R> R executeInLock(String lockKey, Supplier<R> callback) {
        ILock lock = obtainLock();
        return lock.executeInLock(lockKey, callback);
    }
}
