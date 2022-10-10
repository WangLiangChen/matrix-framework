package wang.liangchen.matrix.framework.data.cache;

import org.springframework.cache.Cache;
import wang.liangchen.matrix.cache.sdk.cache.CacheManager;
import wang.liangchen.matrix.framework.commons.random.RandomUtil;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Liangchen.Wang 2022-10-10 13:41
 */
public class CacheOperator {
    private final CacheManager cacheManager;

    public CacheOperator(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public <E extends RootEntity> void clear(Class<E> entityClass) {
        optionalCache(entityClass).ifPresent(Cache::clear);
    }

    private <E extends RootEntity> Optional<Cache> optionalCache(Class<E> entityClass) {
        if (null == cacheManager) {
            return Optional.empty();
        }
        return Optional.ofNullable(cacheManager.getCache(entityClass.getName(), Duration.ofMinutes(RandomUtil.INSTANCE.random(5, 60))));
    }

    public <R, E extends RootEntity> R load(Class<E> entityClass, Object cacheKey, Supplier<R> valueLoader) {
        return optionalCache(entityClass)
                .map(cache -> cache.get(cacheKey, () -> valueLoader.get()))
                .orElseGet(valueLoader);

    }
}
