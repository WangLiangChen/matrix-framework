package wang.liangchen.matrix.framework.spring.data.annotation;

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
        MATRIX_FLAKE,
        AUTO_INCREMENT,
        UUID,
        NANO;
    }
}
