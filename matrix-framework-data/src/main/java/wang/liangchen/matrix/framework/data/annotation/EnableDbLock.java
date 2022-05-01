package wang.liangchen.matrix.framework.data.annotation;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import wang.liangchen.matrix.framework.commons.utils.PrettyPrinter;
import wang.liangchen.matrix.framework.data.configuration.ShedLockAutoConfiguration;
import wang.liangchen.matrix.framework.data.dao.impl.ForUpdateLockImpl;
import wang.liangchen.matrix.framework.data.dao.impl.ReplaceIntoLockImpl;
import wang.liangchen.matrix.framework.data.dao.impl.UpdateLockImpl;

import java.lang.annotation.*;

/**
 * @author LiangChen.Wang
 * 开启并选择DB锁类型
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableSchedulerLock(defaultLockAtMostFor = "5m")
@Import({EnableDbLock.DbLockImportSelector.class})
public @interface EnableDbLock {
    DbLockMode dbLockMode();

    enum DbLockMode {
        UPDATE, FOR_UPDATE, REPLACE_INTO
    }

    class DbLockImportSelector implements ImportSelector {
        private static volatile boolean loaded = false;

        @Override
        public String[] selectImports(AnnotationMetadata annotationMetadata) {
            if (loaded) {
                return new String[0];
            }
            Class<?> annotationType = EnableDbLock.class;
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(annotationType.getName(), false));
            DbLockMode dbLockMode = (DbLockMode) attributes.get("dbLockMode");

            PrettyPrinter.INSTANCE.buffer("@EnableDbLock 开启了DB锁注解......mode:{}", dbLockMode.name());
            PrettyPrinter.INSTANCE.buffer("@EnableDbLock 匹配的类: {}", annotationMetadata.getClassName());
            String[] imports = new String[2];
            imports[0] = ShedLockAutoConfiguration.class.getName();
            switch (dbLockMode) {
                case UPDATE:
                    imports[1] = UpdateLockImpl.class.getName();
                    break;
                case FOR_UPDATE:
                    imports[1] = ForUpdateLockImpl.class.getName();
                    break;
                case REPLACE_INTO:
                    imports[1] = ReplaceIntoLockImpl.class.getName();
                    break;
                default:
                    break;
            }
            loaded = true;
            PrettyPrinter.INSTANCE.flush();
            return imports;
        }
    }
}
