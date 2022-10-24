package wang.liangchen.matrix.framework.lock.annotation;

import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import wang.liangchen.matrix.framework.lock.configuration.MethodProxyConfiguration;

import java.lang.annotation.*;

/**
 * @author Liangchen.Wang 2022-04-19 9:06
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EnableLock.MatrixLockImportSelector.class)
public @interface EnableLock {
    class MatrixLockImportSelector implements ImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{AutoProxyRegistrar.class.getName(), MethodProxyConfiguration.class.getName()};
        }
    }
}
