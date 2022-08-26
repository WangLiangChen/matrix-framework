package wang.liangchen.matrix.framework.lock.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import wang.liangchen.matrix.framework.lock.annotation.MatrixLock;
import wang.liangchen.matrix.framework.lock.core.LockConfiguration;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;

/**
 * @author Liangchen.Wang 2022-08-26 14:56
 */
public enum LockConfigurationResolver {
    INSTANCE;
    private final static Logger logger = LoggerFactory.getLogger(LockConfigurationResolver.class);

    public LockConfiguration resolve(Runnable runnable) {
        if (!(runnable instanceof ScheduledMethodRunnable)) {
            logger.warn("Unsupported type: {}", runnable.getClass().getName());
            return null;
        }
        ScheduledMethodRunnable scheduledMethodRunnable = (ScheduledMethodRunnable) runnable;
        return resolve(scheduledMethodRunnable.getTarget(), scheduledMethodRunnable.getMethod());
    }

    public LockConfiguration resolve(Object target, Method method) {
        MatrixLock annotation = resolveAnnotation(target, method);
        if (null == annotation) {
            return null;
        }
        String lockKey = annotation.lockKey();
        String lockAtLeastString = annotation.lockAtLeast();
        String lockAtMostString = annotation.lockAtMost();
        Duration lockAtLeast = DurationResolver.INSTANCE.resolve(lockAtLeastString);
        Duration lockAtMost = DurationResolver.INSTANCE.resolve(lockAtMostString);
        return new LockConfiguration(lockKey, Instant.now(), lockAtLeast, lockAtMost);
    }

    private MatrixLock resolveAnnotation(Object target, Method method) {
        MatrixLock annotation = AnnotatedElementUtils.getMergedAnnotation(method, MatrixLock.class);
        if (null != annotation) {
            return annotation;
        }
        // 没有注解 尝试从代理类解析
        Class<?> proxyedClass = AopUtils.getTargetClass(target);
        try {
            Method proxyedMethod = proxyedClass.getMethod(method.getName(), method.getParameterTypes());
            return AnnotatedElementUtils.getMergedAnnotation(proxyedMethod, MatrixLock.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }


}
