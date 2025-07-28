package wang.liangchen.matrix.framework.spring.boot.aop;

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
        Object proxyObject = invocation.getThis();
        if (null == proxyObject) {
            return invocation.proceed();
        }
        // TaskScheduler method arguments
        Object[] arguments = invocation.getArguments();
        if (arguments.length > 0 && arguments[0] instanceof Runnable runnable) {
            // 包装runnable
            arguments[0] = wrapRunnable(runnable);
        }
        return invocation.proceed();
    }

    protected abstract Runnable wrapRunnable(Runnable runnable);
}
