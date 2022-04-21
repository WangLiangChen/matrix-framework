package wang.liangchen.matrix.framework.springboot.api;

import java.util.function.Supplier;

/**
 * @author LiangChen.Wang
 * 锁定执行
 */
public interface ILock {

    void executeInLock(String lockKey, Runnable callback);

    <R> R executeInLock(String lockKey, Supplier<R> callback);
}
