package wang.liangchen.matrix.framework.data.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;

/**
 * @author Liangchen.Wang 2022-10-02 12:06
 */
@ConditionalOnMissingClass({"wang.liangchen.matrix.cache.sdk.cache.CacheManager"})
public class StandaloneDaoConfiguration {
    @Bean
    public StandaloneDao standaloneDao() {
        return new StandaloneDao();
    }
}
