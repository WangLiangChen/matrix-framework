package wang.liangchen.matrix.framework.data.configuration;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.annotation.DataSourceAssign;
import wang.liangchen.matrix.framework.data.aop.advisor.MultiDataSourceBeanFactoryPointcutAdvisor;
import wang.liangchen.matrix.framework.data.datasource.MultiDataSourceContext;

import java.lang.reflect.Method;

/**
 * @author Liangchen.Wang
 */
public class JdbcAutoConfiguration {
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

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
        return new NamedParameterJdbcTemplate(jdbcTemplate);
    }
}
