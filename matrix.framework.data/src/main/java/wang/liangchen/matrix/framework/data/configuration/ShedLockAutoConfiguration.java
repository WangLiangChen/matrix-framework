package wang.liangchen.matrix.framework.data.configuration;

import net.javacrumbs.shedlock.core.LockProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import wang.liangchen.matrix.framework.data.shedlock.JdbcTemplateLockProvider;

/**
 * @author LiangChen.Wang
 */
public class ShedLockAutoConfiguration {

    @Bean
    public LockProvider jdbcTemplateLockProvider(JdbcTemplate jdbcTemplate) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(jdbcTemplate)
                        //.usingDbTime()
                        .build()
        );
    }
}
