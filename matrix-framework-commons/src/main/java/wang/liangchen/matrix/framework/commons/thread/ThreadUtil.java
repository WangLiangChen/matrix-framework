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
        // thread waiting queue SynchronousQueue (queue with a capacity of 0)
        unboundedExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                new SynchronousQueue<>(), getThreadFactory("unbounded-executor-", false));
    }

    public Executor getUnboundedExecutor() {
        return unboundedExecutor;
    }

    public ForkJoinPool getForkJoinPool() {
        return new ForkJoinPool();
    }

    public ThreadFactory getThreadFactory(String threadNamePrefix, boolean daemon) {
        return new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger();

            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setDaemon(daemon);
                thread.setName(String.format("%s-%d", threadNamePrefix, counter.getAndIncrement()));
                return thread;
            }
        };
    }

    public ForkJoinPool.ForkJoinWorkerThreadFactory getForkJoinWorkerThreadFactory(String threadNamePrefix, boolean daemon) {
        return new ForkJoinPool.ForkJoinWorkerThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger();

            @Override
            public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
                ForkJoinWorkerThread thread = new ForkJoinWorkerThread(pool) {
                };
                thread.setDaemon(daemon);
                thread.setName(String.format("%s-%d", threadNamePrefix, counter.getAndIncrement()));
                return thread;
            }
        };
    }

    public void shutdownThreadPool(ExecutorService threadPool, long timeout, TimeUnit timeUnit) {
        if (threadPool == null || threadPool.isTerminated()) {
            return;
        }
        // shutdown and reject new tasks
        threadPool.shutdown();
        try {
            if (threadPool.awaitTermination(timeout, timeUnit)) {
                return;
            }
            threadPool.shutdownNow();
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void shutdownThreadPool(ExecutorService threadPool, Duration duration) {
        shutdownThreadPool(threadPool, duration.toMillis(), TimeUnit.MILLISECONDS);
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
