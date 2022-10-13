package wang.liangchen.matrix.framework.commons.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Liangchen.Wang 2022-10-13 9:22
 */
public class DynamicMessageValidator implements ConstraintValidator<DynamicMessageMaker, Object> {
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        //禁止默认消息返回
        constraintValidatorContext.disableDefaultConstraintViolation();
        //输出设置的信息
        constraintValidatorContext.buildConstraintViolationWithTemplate(String.valueOf(object)).addBeanNode().addConstraintViolation();
        return false;
    }
}
