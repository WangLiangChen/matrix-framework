package wang.liangchen.matrix.framework.data.annotation;


import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import wang.liangchen.matrix.framework.commons.utils.PrettyPrinter;
import wang.liangchen.matrix.framework.data.configuration.ShedLockAutoConfiguration;

import java.lang.annotation.*;

/**
 * @author LiangChen.Wang
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableSchedulerLock(defaultLockAtMostFor = "5m")
@Import({EnableJdbcShedLock.ShedLockImportSelector.class})
public @interface EnableJdbcShedLock {
    class ShedLockImportSelector implements ImportSelector {
        private static boolean loaded = false;

        @Override
        public String[] selectImports(AnnotationMetadata annotationMetadata) {
            if (loaded) {
                return new String[0];
            }
            PrettyPrinter.INSTANCE.buffer("@EnableJdbcShedLock 开启了JdbcShedLock注解......");
            PrettyPrinter.INSTANCE.buffer("@EnableJdbcShedLock 匹配的类: {}", annotationMetadata.getClassName());
            String[] imports = new String[]{ShedLockAutoConfiguration.class.getName()};
            loaded = true;
            PrettyPrinter.INSTANCE.flush();
            return imports;
        }
    }
}
