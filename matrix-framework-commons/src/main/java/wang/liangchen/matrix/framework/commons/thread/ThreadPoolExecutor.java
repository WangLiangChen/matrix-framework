package wang.liangchen.matrix.framework.commons.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author Liangchen.Wang 2023-03-22 20:09
 */
public class ThreadPoolExecutor extends java.util.concurrent.ThreadPoolExecutor {
    private final static Logger logger = LoggerFactory.getLogger(ThreadPoolExecutor.class);

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable) {
        // execute 抛出的异常
        if (null != throwable) {
            logger.error("Thread execute exception.", throwable);
            return;
        }
        // submit 抛出的异常
        if (runnable instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) runnable;
                // 通过get才能获取异常
                future.get();
            } catch (Exception exception) {
                logger.error("Thread submit exception.", exception);
            }
        }
    }
}
