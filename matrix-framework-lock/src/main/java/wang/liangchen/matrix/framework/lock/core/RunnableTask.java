package wang.liangchen.matrix.framework.lock.core;

/**
 * @author Liangchen.Wang 2022-08-26 16:17
 */
@FunctionalInterface
public interface RunnableTask{
    void run() throws Throwable;
}
