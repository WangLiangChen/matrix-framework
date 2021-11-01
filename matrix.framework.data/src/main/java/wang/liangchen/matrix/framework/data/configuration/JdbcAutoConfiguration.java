package wang.liangchen.matrix.framework.data.configuration;

import wang.liangchen.matrix.framework.commons.exception.AssertUtil;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.utils.PrettyPrinter;
import wang.liangchen.matrix.framework.data.annotation.DataSource;
import wang.liangchen.matrix.framework.data.aop.advisor.MultiDataSourceBeanFactoryPointcutAdvisor;
import wang.liangchen.matrix.framework.data.datasource.MultiDataSourceContext;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author Liangchen.Wang
 */
public class JdbcAutoConfiguration {
    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    /**
     * 注册数据源切换切面
     */
    @Bean
    public MultiDataSourceBeanFactoryPointcutAdvisor multiDataSourceBeanFactoryPointcutAdvisor() {
        MultiDataSourceBeanFactoryPointcutAdvisor advisor = new MultiDataSourceBeanFactoryPointcutAdvisor();
        advisor.setOrder(Ordered.HIGHEST_PRECEDENCE);
        advisor.setAdvice((MethodInterceptor) methodInvocation -> {
            Method method = methodInvocation.getMethod();
            DataSource dataSource = method.getAnnotation(DataSource.class);
            if (null == dataSource) {
                dataSource = method.getDeclaringClass().getAnnotation(DataSource.class);
            }
            String dataSourceName = dataSource.value();
            AssertUtil.INSTANCE.isTrue(MultiDataSourceContext.INSTANCE.getDataSourceNames().contains(dataSourceName), "The annotated dataSource '{}' does not exist", dataSourceName);
            MultiDataSourceContext.INSTANCE.set(dataSourceName);
            try {
                return methodInvocation.proceed();
            } finally {
                MultiDataSourceContext.INSTANCE.clear();
            }
        });
        return advisor;
    }

    /**
     * 执行初始化SQL
     */
    @Inject
    public void initSQL(javax.sql.DataSource dataSource) {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        try {
            // ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders("")
            Resource[] ddls = resourcePatternResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX.concat("ddl.sql"));
            for (Resource ddl : ddls) {
                PrettyPrinter.INSTANCE.buffer("init ddl sql script:{}", ddl.toString());
                databasePopulator.addScript(ddl);
            }
            Resource[] dmls = resourcePatternResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX.concat("dml.sql"));
            for (Resource dml : dmls) {
                PrettyPrinter.INSTANCE.buffer("init dml sql script:{}", dml.toString());
                databasePopulator.addScript(dml);
            }
            databasePopulator.setSqlScriptEncoding("UTF-8");
            databasePopulator.execute(dataSource);
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        } finally {
            PrettyPrinter.INSTANCE.flush();
        }
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
        return new NamedParameterJdbcTemplate(jdbcTemplate);
    }
}
