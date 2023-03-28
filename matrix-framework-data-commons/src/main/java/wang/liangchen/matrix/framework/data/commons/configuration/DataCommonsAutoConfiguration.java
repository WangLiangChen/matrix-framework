package wang.liangchen.matrix.framework.data.commons.configuration;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import wang.liangchen.matrix.framework.data.commons.domain.dictionary.DictionaryManager;
import wang.liangchen.matrix.framework.data.commons.domain.inifite.InfiniteManager;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;

/**
 * @author Liangchen.Wang 2023-03-26 12:20
 */
@AutoConfigureAfter(StandaloneDao.class)
public class DataCommonsAutoConfiguration {
    @Bean
    public DictionaryManager dictionaryManager(StandaloneDao standaloneDao) {
        return new DictionaryManager(standaloneDao);
    }

    @Bean
    public InfiniteManager infiniteManager(StandaloneDao standaloneDao) {
        return new InfiniteManager(standaloneDao);
    }
}
