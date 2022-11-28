package wang.liangchen.matrix.framework.data.configuration;

import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

/**
 * @author Liangchen.Wang 2022-11-27 23:55
 */
public class JpaAutoConfiguration {
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder entityManagerFactoryBuilder, javax.sql.DataSource dataSource) {
        return entityManagerFactoryBuilder.dataSource(dataSource).packages(RootEntity.class).build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactoryBean.getObject());
        return jpaTransactionManager;
    }
}
