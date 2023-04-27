package wang.liangchen.matrix.framework.data.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import wang.liangchen.matrix.cache.sdk.cache.MatrixCacheManager;
import wang.liangchen.matrix.framework.data.cache.CacheOperator;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;

/**
 * @author Liangchen.Wang 2022-10-02 12:06
 */
@ConditionalOnClass(name = {"wang.liangchen.matrix.cache.sdk.cache.MatrixCacheManager"})
public class CachedStandaloneDaoConfiguration {
    @Bean
    public StandaloneDao cachedStandaloneDao(@Nullable MatrixCacheManager matrixCacheManager) {
        return new StandaloneDao(new CacheOperator(matrixCacheManager));
    }
}
