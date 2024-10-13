package wang.liangchen.matrix.framework.commons.batch;

import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.thread.ThreadUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author LiangChen.Wang 2020/11/5
 */
public final class BatchProcessor<E> {
    private final BlockingQueue<E> blockingQueue;
    private final List<E> batchList;
    private final int batchSize;
    private final Duration timeout;
    private Consumer<List<E>> consumer;
    private Runnable finishRunable;
    private boolean finished = false;

    public static <T> BatchProcessor<T> newInstance(int batchSize) {
        return new BatchProcessor<>(batchSize);
    }

    public static <T> BatchProcessor<T> newInstance(int batchSize, Duration timeout) {
        return new BatchProcessor<>(batchSize, timeout);
    }

    public BatchProcessor(int batchSize) {
        this(batchSize, Duration.ofSeconds(5));
    }

    public BatchProcessor(int batchSize, Duration timeout) {
        if (batchSize <= 0) {
            throw new MatrixWarnException("the batchsize must be positive integer");
        }
        this.batchSize = batchSize;
        this.blockingQueue = new ArrayBlockingQueue<>(batchSize * 4);
        this.batchList = new ArrayList<>(batchSize * 2);
        this.timeout = timeout;
    }

    public boolean put(E e) {
        if (finished) {
            return false;
        }
        if (null == consumer) {
            throw new MatrixWarnException("Set Consumer by method onConsume");
        }
        try {
            blockingQueue.put(e);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new MatrixErrorException(ex);
        }
        return true;
    }

    public void onConsume(Consumer<List<E>> consumer) {
        if (null != this.consumer) {
            throw new MatrixWarnException("Consumer has been set");
        }
        this.consumer = consumer;
        onDrain();
    }

    public void onFinish(Runnable finishRunnable) {
        this.finishRunable = finishRunnable;
    }

    private void onDrain() {
        ThreadUtil.INSTANCE.getUnboundedExecutor().execute(() -> {
            while (true) {
                batchList.clear();
                int count = 0;
                while (count < this.batchSize) {
                    E e;
                    try {
                        e = blockingQueue.poll(timeout.toMillis(), TimeUnit.MILLISECONDS);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        throw new MatrixErrorException(ex);
                    }
                    // 超时未读出数据--结束
                    if (null == e) {
                        this.finished = true;
                        break;
                    }
                    count++;
                    this.batchList.add(e);
                }
                // outter while
                if (count > 0) {
                    this.consumer.accept(batchList);
                }
                if (this.finished) {
                    this.finishRunable.run();
                    break;
                }
            }
        });
    }
}
