package wang.liangchen.matrix.framework.spring.boot.aop;

import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.RootClassFilter;
import org.springframework.aop.support.StaticMethodMatcher;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.TaskScheduler;

import java.lang.reflect.Method;

/**
 * @author Liangchen.Wang 2022-08-26 14:08
 * <p>
 * Advisor of TaskScheduler
 *
 */
public class TaskSchedulerAdvisor extends AbstractPointcutAdvisor {
    private final static String methods = "execute, submit, schedule, scheduleAtFixedRate, scheduleWithFixedDelay";
    private final Advice advice;

    public TaskSchedulerAdvisor(TaskSchedulerMethodAdvice advice) {
        this.advice = advice;
    }

    @NonNull
    @Override
    public Pointcut getPointcut() {
        return new Pointcut() {
            @Override
            public ClassFilter getClassFilter() {
                return new RootClassFilter(TaskScheduler.class);
            }

            @Override
            public MethodMatcher getMethodMatcher() {
                return new StaticMethodMatcher() {
                    @Override
                    public boolean matches(Method method, Class<?> targetClass) {
                        return methods.contains(method.getName());
                    }
                };
            }
        };
    }

    @NonNull
    @Override
    public Advice getAdvice() {
        return this.advice;
    }

}
