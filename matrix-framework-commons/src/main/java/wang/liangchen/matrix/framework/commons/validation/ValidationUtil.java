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
import java.util.Objects;
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

    public <T> T validate(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> results = VALIDATOR.validate(object, groups);
        if (CollectionUtil.INSTANCE.isEmpty(results)) {
            return object;
        }
        StringBuilder stringBuilder = new StringBuilder();
        results.forEach(e -> {
            stringBuilder.append(Symbol.LINE_SEPARATOR.getSymbol())
                    .append(e.getPropertyPath())
                    .append(e.getMessage()).append(Symbol.SEMICOLON.getSymbol());
        });
        throw new MatrixInfoException(stringBuilder.toString());
    }

    public boolean isTrue(boolean condition, String message, Object... args) {
        if (condition) {
            return true;
        }
        throw new MatrixInfoException(message, args);
    }

    public boolean isTrue(boolean condition) {
        return isTrue(condition, "condition must be true");
    }

    public boolean isFalse(boolean condition, String message, Object... args) {
        if (condition) {
            throw new MatrixInfoException(message, args);
        }
        return false;
    }

    public boolean isFalse(boolean condition) {
        return isFalse(condition, "condition must be false");
    }

    public String isBlank(String string, String message, Object... args) {
        if (StringUtil.INSTANCE.isNotBlank(string)) {
            throw new MatrixInfoException(message, args);
        }
        return string;
    }

    public String isBlank(String string) {
        return isBlank(string, "parameter must be blank");
    }

    public String notBlank(String string, String message, Object... args) {
        if (StringUtil.INSTANCE.isBlank(string)) {
            throw new MatrixInfoException(message, args);
        }
        return string;
    }

    public String notBlank(String string) {
        return notBlank(string, "parameter must not be blank");
    }

    public <T> T isNull(T object, String message, Object... args) {
        if (null == object) {
            return null;
        }
        throw new MatrixInfoException(message, args);
    }

    public <T> T isNull(T object) {
        return isNull(object, "parameter must be null");
    }


    public <T> T notNull(T object, String message, Object... args) {
        if (null == object) {
            throw new MatrixInfoException(message, args);
        }
        return object;
    }

    public <T> T notNull(T object) {
        return notNull(object, "parameter must not be null");
    }


    public <T> T isEmpty(T object, String message, Object... args) {
        if (ObjectUtil.INSTANCE.isEmpty(object)) {
            return object;
        }
        throw new MatrixInfoException(message, args);
    }

    public <T> T isEmpty(T object) {
        return isEmpty(object, "parameter must be empty");
    }

    public <T> T notEmpty(T object, String message, Object... args) {
        if (ObjectUtil.INSTANCE.isEmpty(object)) {
            throw new MatrixInfoException(message, args);
        }
        return object;
    }

    public <T> T notEmpty(T object) {
        return notEmpty(object, "parameter must not be empty");
    }

    public boolean equals(Object from, Object to, String message, Object... args) {
        if (Objects.equals(from, to)) {
            return true;
        }
        throw new MatrixInfoException(message, args);
    }

    public boolean equals(Object from, Object to) {
        return equals(from, to, "parameters must be equal");
    }

    public boolean notEquals(Object from, Object to, String message, Object... args) {
        if (Objects.equals(from, to)) {
            throw new MatrixInfoException(message, args);
        }
        return false;
    }

    public boolean notEquals(Object from, Object to) {
        return notEquals(from, to, "parameters must not be equal");
    }
}
