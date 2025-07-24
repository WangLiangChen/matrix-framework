package wang.liangchen.matrix.framework.spring.data.cache;


import java.util.function.Supplier;

/**
 * @author Liangchen.Wang
 */
public interface CacheOperator {

    void evict(String cacheName, String cacheKey);

    void clear(String cacheName);

    <R> R load(String cacheName, String cacheKey, Supplier<R> cacheLoader);
}
