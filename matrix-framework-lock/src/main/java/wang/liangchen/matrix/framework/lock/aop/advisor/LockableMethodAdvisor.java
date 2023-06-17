package wang.liangchen.matrix.framework.lock.aop.advisor;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.scheduling.TaskScheduler;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.lock.annotation.MatrixLock;
import wang.liangchen.matrix.framework.lock.core.LockManager;
import wang.liangchen.matrix.framework.lock.core.LockProperties;
import wang.liangchen.matrix.framework.lock.core.LockResult;
import wang.liangchen.matrix.framework.lock.resolver.LockPropertiesResolver;
import wang.liangchen.matrix.framework.springboot.aop.advisor.TaskSchedulerInterceptor;
import wang.liangchen.matrix.framework.springboot.aop.advisor.TaskSchedulerMethodMatcher;

import java.lang.reflect.Method;

/**
 * @author Liangchen.Wang 2022-08-26 15:40
 */
public class LockableMethodAdvisor extends AbstractPointcutAdvisor {
    private final static Pointcut lockableMethodPointcut = new LockableMethodPointcut();
    private final LockManager lockManager;

    public LockableMethodAdvisor(LockManager lockManager) {
        this.lockManager = lockManager;
    }

    @Override
    public Pointcut getPointcut() {
        return lockableMethodPointcut;
    }

    @Override
    public Advice getAdvice() {
        return new LockableMethodInterceptor(this.lockManager);
    }

    private static class LockableMethodInterceptor extends TaskSchedulerInterceptor {
        private final LockManager lockManager;

        private LockableMethodInterceptor(LockManager lockManager) {
            this.lockManager = lockManager;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Method method = invocation.getMethod();
            Object target = invocation.getThis();
            // TaskScheduler method
            if (TaskScheduler.class.isAssignableFrom(target.getClass())) {
                // use wrapRunnable
                return super.invoke(invocation);
            }
            // AOP method
            Class<?> returnType = method.getReturnType();
            if (void.class.equals(returnType)) {
                throw new MatrixWarnException("method can't return void");
            }
            if (returnType.isPrimitive()) {
                throw new MatrixWarnException("method can't return primitive type");
            }
            LockProperties lockProperties = LockPropertiesResolver.INSTANCE.resolve(target, method);
            LockResult<Object> result = lockManager.executeInLock(lockProperties, invocation::proceed);
            return result.getObject();
        }

        @Override
        protected Runnable wrapRunnable(Runnable runnable) {
            LockProperties lockProperties = LockPropertiesResolver.INSTANCE.resolve(runnable);
            if (null == lockProperties) {
                return runnable;
            }
            // wrap runnable
            return () -> {
                try {
                    lockManager.executeInLock(lockProperties, runnable::run);
                } catch (Throwable e) {
                    throw new MatrixErrorException(e);
                }
            };
        }
    }

    private static class LockableMethodPointcut implements Pointcut {
        private final static MethodMatcher annotatedAndTaskSchedulerMethodMatcher = new TaskSchedulerMethodMatcher() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                // 先匹配注解MatrixLock
                if (AnnotatedElementUtils.hasAnnotation(method, MatrixLock.class)) {
                    return true;
                }
                // 再匹配TaskScheudler
                if (TaskScheduler.class.isAssignableFrom(targetClass)) {
                    return super.matches(method, targetClass);
                }
                // 其它不匹配
                return false;
            }
        };

        @Override
        public ClassFilter getClassFilter() {
            return ClassFilter.TRUE;
        }

        @Override
        public MethodMatcher getMethodMatcher() {
            return annotatedAndTaskSchedulerMethodMatcher;
        }
    }
}
