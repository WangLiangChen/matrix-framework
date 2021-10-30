package liangchen.wang.matrix.framework.springboot.api;

import java.util.function.Supplier;

/**
 * @author LiangChen.Wang
 */
public interface ILock {

    void executeInLock(String lockKey, Runnable callback);

    <R> R executeInLock(String lockKey, Supplier<R> callback);
}
