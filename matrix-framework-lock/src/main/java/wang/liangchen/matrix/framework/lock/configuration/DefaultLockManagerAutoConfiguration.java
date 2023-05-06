package wang.liangchen.matrix.framework.lock.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import wang.liangchen.matrix.framework.lock.core.LockManager;
import wang.liangchen.matrix.framework.lock.rdbms.RdbmsLockManager;

import javax.sql.DataSource;

/**
 * @author Liangchen.Wang 2023-05-06 16:25
 */
public class DefaultLockManagerAutoConfiguration {

    @Bean
    @ConditionalOnBean({DataSource.class})
    @ConditionalOnMissingBean(LockManager.class)
    private LockManager lockManager(DataSource dataSource) {
        return new RdbmsLockManager(dataSource);
    }
}
