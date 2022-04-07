package wang.liangchen.matrix.framework.commons.thread;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Liangchen.Wang 2021-09-30 13:42
 */
public enum ThreadPoolUtil {
    /**
     * instance
     */
    INSTANCE;
    private final Executor unboundedExecutor;

    ThreadPoolUtil() {
        // 核心线程数0,最大线程数Integer.MAX_VALUE,空闲线程超时时间60 SECONDS , 线程等待队列SynchronousQueue(容量为0的队列)
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
                thread.setName(String.format("%s%d", threadName, counter.incrementAndGet()));
                return thread;
            }
        };
    }
}
