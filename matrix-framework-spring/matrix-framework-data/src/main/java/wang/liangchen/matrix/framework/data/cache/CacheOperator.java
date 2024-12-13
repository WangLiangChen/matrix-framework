package wang.liangchen.matrix.framework.data.cache;


import java.util.function.Supplier;

/**
 * @author Liangchen.Wang
 */
public interface CacheOperator {
    void evict(String cacheName);

    <R> R load(String cacheName, String cacheKey, Supplier<R> cacheLoader);
}
