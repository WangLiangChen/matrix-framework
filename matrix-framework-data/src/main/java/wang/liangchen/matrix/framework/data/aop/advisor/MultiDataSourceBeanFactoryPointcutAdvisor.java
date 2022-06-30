package wang.liangchen.matrix.framework.data.aop.advisor;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import wang.liangchen.matrix.framework.data.annotation.DataSourceAssign;

import java.lang.reflect.Method;

/**
 * @author LiangChen.Wang 2021/5/31
 * 采用切面的方式 使用注解切换数据源
 */
@SuppressWarnings("NullableProblems")
public class MultiDataSourceBeanFactoryPointcutAdvisor extends AbstractBeanFactoryPointcutAdvisor {
    @Override
    public Pointcut getPointcut() {
        return new Pointcut() {
            @Override
            public MethodMatcher getMethodMatcher() {
                return new MethodMatcher() {
                    @Override
                    public boolean matches(Method method, Class<?> targetClass, Object[] args) {
                        return matches(method, targetClass);
                    }

                    @Override
                    public boolean matches(Method method, Class<?> targetClass) {
                        if (method.getAnnotation(DataSourceAssign.class) != null) {
                            return true;
                        }
                        return targetClass.getAnnotation(DataSourceAssign.class) != null;
                    }

                    @Override
                    public boolean isRuntime() {
                        return true;
                    }
                };
            }

            @Override
            public ClassFilter getClassFilter() {
                return ClassFilter.TRUE;
                // return clazz -> clazz.getAnnotation(DataSourceSwitchable.class) != null;
            }
        };

    }
}
