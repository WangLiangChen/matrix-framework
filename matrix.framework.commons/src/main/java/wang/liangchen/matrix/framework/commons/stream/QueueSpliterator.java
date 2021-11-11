package wang.liangchen.matrix.framework.commons.stream;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author LiangChen.Wang
 */
public class QueueSpliterator<T> extends Spliterators.AbstractSpliterator<T> {
    private final BlockingQueue<T> queue;
    private final long timeout;
    private final TimeUnit timeUnit;

    public QueueSpliterator(final BlockingQueue<T> queue, final long timeout, final TimeUnit timeUnit) {
        super(Long.MAX_VALUE, Spliterator.CONCURRENT | Spliterator.NONNULL | Spliterator.ORDERED | Spliterator.DISTINCT);
        this.queue = queue;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    @Override
    public boolean tryAdvance(final Consumer<? super T> action) {
        try {
            final T next = this.queue.poll(this.timeout, this.timeUnit);
            if (next == null) {
                return false;
            }
            action.accept(next);
            return true;
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public Stream<T> stream(boolean parallel) {
        return StreamSupport.stream(this, parallel);
    }

    public Stream<T> stream() {
        return StreamSupport.stream(this, false);
    }
}
