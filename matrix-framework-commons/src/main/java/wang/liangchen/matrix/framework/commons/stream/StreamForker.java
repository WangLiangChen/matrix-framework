package wang.liangchen.matrix.framework.commons.stream;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author LiangChen.Wang
 */
public class StreamForker<T> {
    private final Stream<T> stream;
    //记录对流的操作列表
    private final Map<Object, Function<Stream<T>, ?>> actionMap = new HashMap<>();

    public StreamForker(Stream<T> stream) {
        this.stream = stream;
    }

    public StreamForker<T> fork(Object key, Function<Stream<T>, ?> function) {
        actionMap.put(key, function);
        return this;
    }

    public Results getResults() {
        ForkerConsumer<T> consumer = build();
        try {
            //将元素放入ForkerConsumer分发到子流中
            stream.sequential().forEach(consumer);
        } finally {
            //向子流发送结束标识
            consumer.finish();
        }
        return consumer;
    }

    public ForkerConsumer<T> build() {
        List<BlockingQueue<T>> queues = new ArrayList<>();
        //获取结果map
        Map<Object, Future<?>> futureMap = actionMap.entrySet().stream()
                .reduce(new HashMap<>(), (map, entry) -> {
                    map.put(entry.getKey(), getResult0(queues, entry.getValue()));
                    return map;
                }, (map1, map2) -> {
                    map1.putAll(map2);
                    return map1;
                });
        return new ForkerConsumer<>(queues, futureMap);
    }

    //异步获取子流的结果
    private Future<?> getResult0(List<BlockingQueue<T>> queues, Function<Stream<T>, ?> function) {
        BlockingQueue<T> queue = new LinkedBlockingQueue<>();
        queues.add(queue);
        Spliterator<T> spliterator = new SubSpliterator<>(queue);
        Stream<T> souce = StreamSupport.stream(spliterator, false);
        return CompletableFuture.supplyAsync(() -> function.apply(souce));
    }

    public interface Results {
        <R> R get(Object key);
    }

    static class SubSpliterator<T> implements Spliterator<T> {
        private final BlockingQueue<T> queue;

        public SubSpliterator(BlockingQueue<T> queue) {
            this.queue = queue;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            T t;
            while (true) {
                try {
                    t = queue.take();
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (t != ForkerConsumer.END_OF_STREAM) {
                action.accept(t);
                return true;
            }
            return false;
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
    }

    static class ForkerConsumer<T> implements Consumer<T>, Results {
        static final Object END_OF_STREAM = new Object();
        private final List<BlockingQueue<T>> queues;
        //存放子流结果
        private final Map<Object, Future<?>> results;

        public ForkerConsumer(List<BlockingQueue<T>> queues, Map<Object, Future<?>> results) {
            this.queues = queues;
            this.results = results;
        }

        @SuppressWarnings("unchecked")
        void finish() {
            /* 放入结束标识 */
            accept((T) END_OF_STREAM);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <R> R get(Object key) {
            try {
                return ((Future<R>) results.get(key)).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void accept(T t) {
            //接受到元素放入各个子流的队列中
            queues.forEach(q -> q.offer(t));
        }
    }
}
