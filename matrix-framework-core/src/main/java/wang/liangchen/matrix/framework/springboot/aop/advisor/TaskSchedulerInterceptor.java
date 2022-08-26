package wang.liangchen.matrix.framework.springboot.aop.advisor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Liangchen.Wang 2022-08-26 14:24
 */
public abstract class TaskSchedulerInterceptor implements MethodInterceptor {
    private final static Logger logger = LoggerFactory.getLogger(TaskSchedulerInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] arguments = invocation.getArguments();
        if (arguments.length > 0 && arguments[0] instanceof Runnable) {
            // 包装arguments[0] runnable
            arguments[0] = wrapRunnable((Runnable) arguments[0]);
        } else {
            logger.warn("Task scheduler first argument should be Runnable");
        }
        return invocation.proceed();
    }

    protected abstract Runnable wrapRunnable(Runnable runnable) throws Throwable;
}
