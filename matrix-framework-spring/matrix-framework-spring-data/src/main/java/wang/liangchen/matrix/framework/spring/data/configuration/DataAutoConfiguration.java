package wang.liangchen.matrix.framework.spring.data.configuration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import wang.liangchen.matrix.framework.spring.data.cache.DefaultCacheOperator;
import wang.liangchen.matrix.framework.spring.data.repository.StandaloneRepository;

/**
 * @author Liangchen.Wang 2023-03-17 9:53
 */
@AutoConfiguration
public class DataAutoConfiguration {
    @Bean
    public StandaloneRepository cachedStandaloneRepository(ObjectProvider<CacheManager> cacheManagerProvider) {
        return new StandaloneRepository(new DefaultCacheOperator(cacheManagerProvider::getIfAvailable));
    }

}
