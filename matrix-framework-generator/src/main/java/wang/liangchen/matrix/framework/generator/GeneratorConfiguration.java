package wang.liangchen.matrix.framework.generator;


import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.generator.ddd.DDDStyleGenerator;

/**
 * @author Liangchen.Wang 2022-08-15 16:00
 */
public class GeneratorConfiguration {
    @Bean
    @ConditionalOnBean(StandaloneDao.class)
    public DDDGenerator domainGenerator(StandaloneDao standaloneDao) {
        return new DDDGenerator(standaloneDao);
    }

    @Bean
    @ConditionalOnBean(StandaloneDao.class)
    public DDDStyleGenerator dddStyleGenerator(StandaloneDao standaloneDao) {
        return new DDDStyleGenerator(standaloneDao);
    }
}
