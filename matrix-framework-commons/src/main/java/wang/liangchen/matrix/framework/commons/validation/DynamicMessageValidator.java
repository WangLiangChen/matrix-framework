package wang.liangchen.matrix.framework.commons.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * @author Liangchen.Wang 2022-10-13 9:22
 */
public class DynamicMessageValidator implements ConstraintValidator<DynamicMessageMaker, String> {
    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        //禁止默认消息返回
        constraintValidatorContext.disableDefaultConstraintViolation();
        //输出设置的信息(校验的内容,也就是DynamicMessage中message的值)
        constraintValidatorContext.buildConstraintViolationWithTemplate(string).addBeanNode().addConstraintViolation();
        return false;
    }
}
