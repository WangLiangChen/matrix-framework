package wang.liangchen.matrix.framework.generator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.generator.ddd.DDDGenerator;
import wang.liangchen.matrix.framework.generator.tier3.Tier3Generator;

/**
 * @author Liangchen.Wang 2022-08-15 16:00
 */
public class GeneratorConfiguration {
    @Bean
    @ConditionalOnBean(StandaloneDao.class)
    public DDDGenerator domainGenerator() {
        return new DDDGenerator();
    }

    @Bean
    @ConditionalOnBean(StandaloneDao.class)
    public Tier3Generator tier3Generator() {
        return new Tier3Generator();
    }
}
