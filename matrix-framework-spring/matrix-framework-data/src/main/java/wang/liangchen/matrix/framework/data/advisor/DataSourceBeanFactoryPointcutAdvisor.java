package wang.liangchen.matrix.framework.data.advisor;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import wang.liangchen.matrix.framework.data.annotation.DataSourceRouter;
import wang.liangchen.matrix.framework.data.context.DataSourceContext;

import java.lang.reflect.Method;

/**
 * @author LiangChen.Wang 2021/5/31
 * 采用切面的方式 使用注解切换数据源
 * 启动时，所有的Advisor都会被AnnotationAwareAspectJAutoProxyCreator这个BeanPostProcessor解析处理
 * 注册数据源切换切面 advisor=pointcut+advice
 */
@Component
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class DataSourceBeanFactoryPointcutAdvisor extends AbstractBeanFactoryPointcutAdvisor {
    @NonNull
    @Override
    public Pointcut getPointcut() {
        return new Pointcut() {
            @NonNull
            @Override
            public MethodMatcher getMethodMatcher() {
                return new MethodMatcher() {
                    @Override
                    public boolean matches(@NonNull Method method, @NonNull Class<?> targetClass, @NonNull Object[] args) {
                        return matches(method, targetClass);
                    }

                    @Override
                    public boolean matches(Method method, @NonNull Class<?> targetClass) {
                        if (method.getAnnotation(DataSourceRouter.class) != null) {
                            return true;
                        }
                        return targetClass.getAnnotation(DataSourceRouter.class) != null;
                    }

                    @Override
                    public boolean isRuntime() {
                        return true;
                    }
                };
            }

            @NonNull
            @Override
            public ClassFilter getClassFilter() {
                return ClassFilter.TRUE;
            }
        };
    }

    @NonNull
    @Override
    public Advice getAdvice() {
        return (MethodInterceptor) methodInvocation -> {
            Method method = methodInvocation.getMethod();
            DataSourceRouter dataSourceRouter = method.getAnnotation(DataSourceRouter.class);
            if (null == dataSourceRouter) {
                dataSourceRouter = method.getDeclaringClass().getAnnotation(DataSourceRouter.class);
            }
            String dataSourceName = dataSourceRouter.value();
            DataSourceContext.INSTANCE.set(dataSourceName);
            try {
                return methodInvocation.proceed();
            } finally {
                DataSourceContext.INSTANCE.clear();
            }
        };
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}