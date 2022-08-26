package wang.liangchen.matrix.framework.lock.core;

/**
 * @author Liangchen.Wang 2022-08-26 16:18
 */
public interface SupplierTask<R> {
    R get() throws Throwable;
}
