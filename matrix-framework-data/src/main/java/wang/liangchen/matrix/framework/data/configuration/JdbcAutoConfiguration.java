package wang.liangchen.matrix.framework.data.configuration;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.utils.PrettyPrinter;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.annotation.DataSourceAssign;
import wang.liangchen.matrix.framework.data.aop.advisor.MultiDataSourceBeanFactoryPointcutAdvisor;
import wang.liangchen.matrix.framework.data.datasource.MultiDataSourceContext;
import wang.liangchen.matrix.framework.data.datasource.dialect.AbstractDialect;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author Liangchen.Wang
 */
public class JdbcAutoConfiguration {
    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    @Bean
    public MultiDataSourceBeanFactoryPointcutAdvisor multiDataSourceBeanFactoryPointcutAdvisor() {
        // 启动时，所有的Advisor都会被AnnotationAwareAspectJAutoProxyCreator这个BeanPostProcessor解析处理
        // 注册数据源切换切面 advisor=pointcut+advice
        MultiDataSourceBeanFactoryPointcutAdvisor advisor = new MultiDataSourceBeanFactoryPointcutAdvisor();
        advisor.setOrder(Ordered.HIGHEST_PRECEDENCE);
        advisor.setAdvice((MethodInterceptor) methodInvocation -> {
            Method method = methodInvocation.getMethod();
            DataSourceAssign dataSourceAssign = method.getAnnotation(DataSourceAssign.class);
            if (null == dataSourceAssign) {
                dataSourceAssign = method.getDeclaringClass().getAnnotation(DataSourceAssign.class);
            }
            String dataSourceName = dataSourceAssign.value();
            ValidationUtil.INSTANCE.isTrue(ExceptionLevel.WARN, MultiDataSourceContext.INSTANCE.getDataSourceNames().contains(dataSourceName), "The annotated dataSource '{}' does not exist", dataSourceName);
            MultiDataSourceContext.INSTANCE.set(dataSourceName);
            try {
                return methodInvocation.proceed();
            } finally {
                MultiDataSourceContext.INSTANCE.clear();
            }
        });
        return advisor;
    }

    @Inject
    public void initSQL() {
        Set<String> dataSourceNames = MultiDataSourceContext.INSTANCE.getDataSourceNames();
        ResourceDatabasePopulator databasePopulator;
        DataSource dataSource;
        AbstractDialect dialect;
        try {
            for (String dataSourceName : dataSourceNames) {
                databasePopulator = new ResourceDatabasePopulator();
                databasePopulator.setSqlScriptEncoding("UTF-8");
                dialect = MultiDataSourceContext.INSTANCE.getDialect(dataSourceName);
                Resource[] sqlFiles = resourcePatternResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX.concat(dialect.getDataSourceType()).concat("_INIT.sql"));
                for (Resource sqlFile : sqlFiles) {
                    PrettyPrinter.INSTANCE.buffer("init sql script:{}", sqlFile.toString());
                    databasePopulator.addScript(sqlFile);
                }
                dataSource = MultiDataSourceContext.INSTANCE.getDataSource(dataSourceName);
                databasePopulator.execute(dataSource);
            }
        } catch (Exception e) {
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
