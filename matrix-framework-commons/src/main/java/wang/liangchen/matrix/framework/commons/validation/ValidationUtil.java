package wang.liangchen.matrix.framework.commons.validation;

import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * @author Liangchen.Wang 2022-04-29 15:50
 */
public enum ValidationUtil {
    /**
     * instance
     */
    INSTANCE;
    private final static ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    private final static Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();

    public void close() {
        if (null == VALIDATOR_FACTORY) {
            return;
        }
        VALIDATOR_FACTORY.close();
    }

    public <T> void validate(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> results = VALIDATOR.validate(object, groups);
        if (CollectionUtil.INSTANCE.isEmpty(results)) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        results.forEach(e -> {
            stringBuilder.append(Symbol.LINE_SEPARATOR.getSymbol())
                    .append("Field '").append(e.getPropertyPath().toString()).append("' ")
                    .append(e.getMessage()).append(Symbol.SEMICOLON.getSymbol());
        });
        throw new MatrixInfoException(stringBuilder.toString());
    }

    public <T> T notNull(T object) {
        return notNull(object, null);
    }

    public <T> T notNull(T object, String message, Object... args) {
        if (ObjectUtil.INSTANCE.isNotNull(object)) {
            return object;
        }
        if (StringUtil.INSTANCE.isBlank(message)) {
            throw new MatrixInfoException("object can not be null");
        }
        throw new MatrixInfoException(message, args);
    }

    public <T> T notEmpty(T object) {
        return notEmpty(object, null);
    }

    public <T> T notEmpty(T object, String message, Object... args) {
        if (ObjectUtil.INSTANCE.isNotEmpty(object)) {
            return object;
        }
        if (StringUtil.INSTANCE.isBlank(message)) {
            throw new MatrixInfoException("object can not be empty");
        }
        throw new MatrixInfoException(message, args);
    }
}
