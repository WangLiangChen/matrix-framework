package wang.liangchen.matrix.framework.commons.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Liangchen.Wang 2022-10-13 9:21
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = DynamicMessageValidator.class)
@Documented
@interface DynamicMessageMaker {


    String message() default "{defaultMessage}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
