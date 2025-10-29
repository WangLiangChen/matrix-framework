package wang.liangchen.matrix.framework.spring.data.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Liangchen.Wang
 */
public class DefaultCacheOperator implements CacheOperator {
    private final Supplier<CacheManager> cacheManagerSupplier;

    static {
        System.setProperty("matrix.cache.ttl.random", "true");
    }

    public DefaultCacheOperator(Supplier<CacheManager> cacheManagerSupplier) {
        this.cacheManagerSupplier = cacheManagerSupplier;
    }

    @Override
    public void evict(String cacheName, String cacheKey) {
        findCache(cacheName).ifPresent(cache -> cache.evict(cacheKey));
    }

    @Override
    public void clear(String cacheName) {
        findCache(cacheName).ifPresent(Cache::clear);
    }

    @Override
    public <R> R load(String cacheName, String cacheKey, Supplier<R> cacheLoader) {
        return findCache(cacheName).map(cache -> cache.get(cacheKey, cacheLoader::get)).orElseGet(cacheLoader);
    }

    public Optional<Cache> findCache(String cacheName) {
        CacheManager cacheManager = this.cacheManagerSupplier.get();
        if (null == cacheManager) {
            return Optional.empty();
        }
        return Optional.ofNullable(cacheManager.getCache(cacheName));
    }
}
