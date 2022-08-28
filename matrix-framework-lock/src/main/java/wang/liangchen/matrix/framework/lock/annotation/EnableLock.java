package wang.liangchen.matrix.framework.lock.annotation;

import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import wang.liangchen.matrix.framework.lock.configuration.MethodProxyConfiguration;
import wang.liangchen.matrix.framework.lock.configuration.TaskSchedulerProxyConfiguration;

import java.lang.annotation.*;

/**
 * @author Liangchen.Wang 2022-04-19 9:06
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EnableLock.MatrixLockImportSelector.class)
public @interface EnableLock {
    ProxyMode proxyMode() default ProxyMode.TASK_SCHEDULER;

    enum ProxyMode {
        TASK_SCHEDULER,
        METHOD
    }

    class MatrixLockImportSelector implements ImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableLock.class.getName(), false));
            ProxyMode proxyMode = annotationAttributes.getEnum("proxyMode");
            if (ProxyMode.TASK_SCHEDULER == proxyMode) {
                return new String[]{AutoProxyRegistrar.class.getName(), TaskSchedulerProxyConfiguration.class.getName()};
            }
            return new String[]{AutoProxyRegistrar.class.getName(), MethodProxyConfiguration.class.getName()};
        }
    }
}
