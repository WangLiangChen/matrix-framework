package wang.liangchen.matrix.framework.lock.aop.advisor;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.lock.annotation.MatrixLock;
import wang.liangchen.matrix.framework.lock.core.LockConfiguration;
import wang.liangchen.matrix.framework.lock.core.LockManager;
import wang.liangchen.matrix.framework.lock.core.TaskResult;
import wang.liangchen.matrix.framework.lock.resolver.LockConfigurationResolver;
import wang.liangchen.matrix.framework.springboot.aop.advisor.AnnotationOrNameMatchMethodPointcut;
import wang.liangchen.matrix.framework.springboot.aop.advisor.TaskSchedulerInterceptor;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Liangchen.Wang 2022-08-26 15:40
 */
public class LockableMethodAdvisor extends AbstractPointcutAdvisor {
    private final LockManager lockManager;

    public LockableMethodAdvisor(LockManager lockManager) {
        this.lockManager = lockManager;
    }

    @Override
    public Pointcut getPointcut() {
        return new LockableMethodPointcut();
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
            LockConfiguration lockConfiguration = LockConfigurationResolver.INSTANCE.resolve(target, method);
            TaskResult<Object> result = lockManager.executeInLock(lockConfiguration, invocation::proceed);
            return result.getObject();
        }

        @Override
        protected Runnable wrapRunnable(Runnable runnable) {
            LockConfiguration lockConfiguration = LockConfigurationResolver.INSTANCE.resolve(runnable);
            if (null == lockConfiguration) {
                return runnable;
            }
            // wrap runnable
            return () -> {
                try {
                    lockManager.executeInLock(lockConfiguration, runnable::run);
                } catch (Throwable e) {
                    throw new MatrixErrorException(e);
                }
            };
        }
    }

    private static class LockableMethodPointcut implements Pointcut {
        private final Set<AnnotationOrNameMatchMethodPointcut.MappedMethod> mappedMethods = new HashSet<AnnotationOrNameMatchMethodPointcut.MappedMethod>() {{
            add(new AnnotationOrNameMatchMethodPointcut.MappedMethod(TaskScheduler.class, "execute"));
            add(new AnnotationOrNameMatchMethodPointcut.MappedMethod(TaskScheduler.class, "schedule"));
            add(new AnnotationOrNameMatchMethodPointcut.MappedMethod(TaskScheduler.class, "scheduleAtFixedRate"));
            add(new AnnotationOrNameMatchMethodPointcut.MappedMethod(TaskScheduler.class, "scheduleWithFixedDelay"));
        }};

        @Override
        public ClassFilter getClassFilter() {
            return ClassFilter.TRUE;
        }

        @Override
        public MethodMatcher getMethodMatcher() {
            AnnotationOrNameMatchMethodPointcut annotationOrNameMatchMethodPointcut = new AnnotationOrNameMatchMethodPointcut(MatrixLock.class, true, mappedMethods);
            // 排除Scheduled,防止重复拦截
            annotationOrNameMatchMethodPointcut.setExcludedAnnotationTypes(Scheduled.class);
            return annotationOrNameMatchMethodPointcut;
        }
    }
}
