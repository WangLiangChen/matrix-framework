package wang.liangchen.matrix.framework.generator;


import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;

/**
 * @author Liangchen.Wang 2022-08-15 16:00
 */
public class GeneratorConfiguration {
    @Bean
    @ConditionalOnBean(StandaloneDao.class)
    public DomainGenerator domainGenerator(StandaloneDao standaloneDao){
        return new DomainGenerator(standaloneDao);
    }
}
