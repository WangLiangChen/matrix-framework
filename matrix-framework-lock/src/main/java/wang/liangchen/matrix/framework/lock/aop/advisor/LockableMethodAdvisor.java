package wang.liangchen.matrix.framework.lock.aop.advisor;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.lock.annotation.MatrixLock;
import wang.liangchen.matrix.framework.lock.core.LockConfiguration;
import wang.liangchen.matrix.framework.lock.core.LockManager;
import wang.liangchen.matrix.framework.lock.core.TaskResult;
import wang.liangchen.matrix.framework.lock.resolver.LockConfigurationResolver;

/**
 * @author Liangchen.Wang 2022-08-26 15:40
 */
public class LockableMethodAdvisor extends AbstractPointcutAdvisor {
    @Override
    public Pointcut getPointcut() {
        return new AnnotationMatchingPointcut(null, MatrixLock.class, true);
    }

    @Override
    public Advice getAdvice() {
        return null;
    }

    private static class LockableMethodInterceptor implements MethodInterceptor {
        private final LockManager lockManager;

        private LockableMethodInterceptor(LockManager lockManager) {
            this.lockManager = lockManager;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Class<?> returnType = invocation.getMethod().getReturnType();
            if (void.class.equals(returnType)) {
                throw new MatrixInfoException("method can't return void");
            }
            if (returnType.isPrimitive()) {
                throw new MatrixInfoException("method can't return primitive type");
            }
            LockConfiguration lockConfiguration = LockConfigurationResolver.INSTANCE.resolve(invocation.getThis(), invocation.getMethod());
            TaskResult<Object> result = lockManager.executeInLock(lockConfiguration, invocation::proceed);
            return result.getObject();
        }
    }
}
