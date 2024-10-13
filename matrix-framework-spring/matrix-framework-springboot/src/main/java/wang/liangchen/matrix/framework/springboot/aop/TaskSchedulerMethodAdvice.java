package wang.liangchen.matrix.framework.springboot.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.scheduling.TaskScheduler;

/**
 * @author Liangchen.Wang 2022-08-26 14:24
 * advice
 */
public abstract class TaskSchedulerMethodAdvice implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object object = invocation.getThis();
        if (null == object) {
            return invocation.proceed();
        }
        Class<?> objectClass = object.getClass();
        // TaskScheduler method
        if (TaskScheduler.class.isAssignableFrom(objectClass)) {
            Object[] arguments = invocation.getArguments();
            if (arguments.length > 0 && arguments[0] instanceof Runnable) {
                // 包装arguments[0]的runnable
                arguments[0] = wrapRunnable((Runnable) arguments[0]);
            }
        }
        return invocation.proceed();
    }

    protected abstract Runnable wrapRunnable(Runnable runnable);
}
