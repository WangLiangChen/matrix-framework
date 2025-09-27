package wang.liangchen.matrix.framework.spring.boot.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author Liangchen.Wang 2022-08-26 14:24
 * Advice of method of TaskScheduler
 */
public abstract class TaskSchedulerMethodAdvice implements MethodInterceptor {
    private final static Logger logger = LoggerFactory.getLogger(TaskSchedulerMethodAdvice.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object object = invocation.getThis();
        if (null == object) {
            return invocation.proceed();
        }
        Method method = invocation.getMethod();
        logger.info("invoking {} of {}", method.getName(), object.getClass().getSimpleName());

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
