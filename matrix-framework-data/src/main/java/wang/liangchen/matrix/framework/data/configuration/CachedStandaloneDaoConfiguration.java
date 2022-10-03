package wang.liangchen.matrix.framework.data.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import wang.liangchen.matrix.cache.sdk.cache.CacheManager;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;

/**
 * @author Liangchen.Wang 2022-10-02 12:06
 */
@ConditionalOnClass(name = {"wang.liangchen.matrix.cache.sdk.cache.CacheManager"})
public class CachedStandaloneDaoConfiguration {
    @Bean
    public StandaloneDao cachedStandaloneDao(@Nullable CacheManager cacheManager) {
        return new StandaloneDao(cacheManager);
    }
}
