package wang.liangchen.matrix.framework.springboot.aop.advisor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Liangchen.Wang 2022-08-26 14:24
 */
public abstract class TaskSchedulerInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] arguments = invocation.getArguments();
        if (arguments.length > 0 && arguments[0] instanceof Runnable) {
            // 包装arguments[0]的runnable
            arguments[0] = wrapRunnable((Runnable) arguments[0]);
        }
        return invocation.proceed();
    }

    protected abstract Runnable wrapRunnable(Runnable runnable);
}
