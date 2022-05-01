package wang.liangchen.matrix.framework.data.configuration;

import org.springframework.context.annotation.Bean;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;

/**
 * @author Liangchen.Wang
 */
public class ComponentAutoConfiguration {

    @Bean
    public StandaloneDao standaloneDao() {
        return new StandaloneDao();
    }
}
