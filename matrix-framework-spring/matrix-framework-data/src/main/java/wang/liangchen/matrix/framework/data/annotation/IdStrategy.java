package wang.liangchen.matrix.framework.data.annotation;

import java.lang.annotation.*;

/**
 * @author Liangchen.Wang 2022-04-19 9:06
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IdStrategy {
    Strategy value();

    enum Strategy {
        NONE,
        MatrixFlake,
        AutoIncrement,
        UUID,
        NANO;
    }
}
