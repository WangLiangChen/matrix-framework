package wang.liangchen.matrix.framework.commons.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;

/**
 * @author Liangchen.Wang 2023-03-21 19:37
 */
public class ScheduledThreadPoolExecutor extends java.util.concurrent.ScheduledThreadPoolExecutor {
    private final static Logger logger = LoggerFactory.getLogger(ScheduledThreadPoolExecutor.class);

    public ScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    public ScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    public ScheduledThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler) {
        super(corePoolSize, handler);
    }

    public ScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
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
