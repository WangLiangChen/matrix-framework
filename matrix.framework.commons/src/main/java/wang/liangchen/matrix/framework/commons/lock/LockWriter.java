package wang.liangchen.matrix.framework.commons.lock;

/**
 * @author LiangChen.Wang
 */
@FunctionalInterface
public interface LockWriter<R> {
    R write() throws RuntimeException;
}
