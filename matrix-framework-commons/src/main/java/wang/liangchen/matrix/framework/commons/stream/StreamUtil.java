package wang.liangchen.matrix.framework.commons.stream;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author LiangChen.Wang
 *
 */
public enum StreamUtil {
    //
    INSTANCE;

    public <T> Stream<T> takeWhile(Stream<T> stream, Predicate<? super T> predicate) {
        Spliterator<T> spliterator = stream.spliterator();
        return StreamSupport.stream(new Spliterator<T>() {
            boolean stillGoing = false;

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                boolean hasNext = spliterator.tryAdvance(item -> {
                    if (!predicate.test(item)) {
                        action.accept(item);
                        stillGoing = true;
                    } else {
                        stillGoing = false;
                    }
                });
                return hasNext && stillGoing;
            }

            @Override
            public Spliterator<T> trySplit() {
                return null;
            }

            @Override
            public long estimateSize() {
                return 0;
            }

            @Override
            public int characteristics() {
                return 0;
            }
        }, false);
    }

    public <T> Stream<T> dropWhile(Stream<T> stream, Predicate<? super T> predicate) {
        Spliterator<T> spliterator = stream.spliterator();
        return StreamSupport.stream(new Spliterator<T>() {
            boolean canGo = false;

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                return spliterator.tryAdvance(item -> {
                    if (canGo) {
                        action.accept(item);
                    } else {
                        canGo = predicate.test(item);
                    }
                });

            }

            @Override
            public Spliterator<T> trySplit() {
                return null;
            }

            @Override
            public long estimateSize() {
                return 0;
            }

            @Override
            public int characteristics() {
                return 0;
            }
        }, false);
    }
}
