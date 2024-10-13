package wang.liangchen.matrix.framework.commons.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Liangchen.Wang 2021-09-30 9:08
 */
public enum ThreadUtil {
    /**
     * instance
     */
    INSTANCE;
    private final static Logger logger = LoggerFactory.getLogger(ThreadUtil.class);
    private final Executor unboundedExecutor;

    ThreadUtil() {
        // Core thread number 0, maximum thread number Integer.MAX_VALUE, idle thread timeout 60 SECONDS, thread waiting queue SynchronousQueue (queue with a capacity of 0)
        unboundedExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                new SynchronousQueue<>(), getThreadFactory("unbounded-executor-", false));
    }

    public Executor getUnboundedExecutor() {
        return unboundedExecutor;
    }

    public ThreadFactory getThreadFactory(String threadName, boolean daemon) {
        return new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger();

            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setDaemon(daemon);
                thread.setName(String.format("%s(%d)", threadName, counter.incrementAndGet()));
                return thread;
            }
        };
    }

    public static void shutdownThreadPool(ExecutorService threadPool, long timeout, TimeUnit timeUnit) {
        if (threadPool == null || threadPool.isTerminated()) {
            return;
        }

        try {
            // shutdown and reject new tasks
            threadPool.shutdown();
        } catch (Exception e) {
            logger.error("Failed to shutdown thread pool", e);
            return;
        }

        try {
            if (!threadPool.awaitTermination(timeout, timeUnit)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
        }

    }

    public void sleep(TimeUnit timeUnit, long timeout) {
        try {
            timeUnit.sleep(timeout);
        } catch (InterruptedException e) {
            // Restore the interrupted status
            Thread.currentThread().interrupt();
            throw new MatrixErrorException(e);
        }
    }

    public void sleep(long timeoutMS) {
        sleep(TimeUnit.MILLISECONDS, timeoutMS);
    }

    public void sleep(Duration timeout) {
        sleep(timeout.toMillis());
    }

}
