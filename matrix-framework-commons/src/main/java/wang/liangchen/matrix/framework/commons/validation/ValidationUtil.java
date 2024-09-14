package wang.liangchen.matrix.framework.commons.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import wang.liangchen.matrix.framework.commons.CollectionUtil;
import wang.liangchen.matrix.framework.commons.StringUtil;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.exception.MatrixExceptionLevel;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Liangchen.Wang 2022-04-29 15:50
 */
public enum ValidationUtil {
    /**
     * instance
     */
    INSTANCE;
    private final Pattern VALIDATION_PATTERN = Pattern.compile("^([^{]*\\{)[a-zA-Z]+[.a-zA-Z0-9_-]*(}[^}]*)$");

    private final ThreadLocal<Locale> localeThreadLocal = InheritableThreadLocal.withInitial(Locale::getDefault);
    private ValidatorFactory VALIDATOR_FACTORY;
    private volatile Validator VALIDATOR;

    ValidationUtil() {
        VALIDATOR_FACTORY = Validation.byDefaultProvider().configure().buildValidatorFactory();
        VALIDATOR = VALIDATOR_FACTORY.getValidator();
    }

    public synchronized void resetValidator(Validator validator) {
        if (null != VALIDATOR_FACTORY) {
            VALIDATOR_FACTORY.close();
        }
        VALIDATOR = validator;
    }

    public String resolveMessage(String message, Object... args) {
        // 包含且仅包含一个{}包裹的Message Key
        if (VALIDATION_PATTERN.matcher(message).matches()) {
            message = resolveMessage(DynamicMessage.newInstantce(message));
        }
        return StringUtil.INSTANCE.format(message, args);
    }

    public <T> T validate(MatrixExceptionLevel exceptionLevel, T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> results = VALIDATOR.validate(object, groups);
        if (CollectionUtil.INSTANCE.isEmpty(results)) {
            return object;
        }
        StringBuilder messageBuilder = new StringBuilder();
        results.forEach(e -> {
            messageBuilder.append(e.getMessage());
            messageBuilder.append(Symbol.OPEN_PAREN).append(e.getPropertyPath()).append(Symbol.CLOSE_PAREN).append(Symbol.SEMICOLON);
        });
        throwException(exceptionLevel, messageBuilder.toString());
        return object;
    }

    public <T> T validate(T object, Class<?>... groups) {
        return validate(MatrixExceptionLevel.WARN, object, groups);
    }


    public boolean isTrue(MatrixExceptionLevel exceptionLevel, boolean condition, String message, Object... args) {
        if (condition) {
            return true;
        }
        dynamicException(exceptionLevel, message, args);
        return false;
    }

    public boolean isTrue(boolean condition, String message, Object... args) {
        return isTrue(MatrixExceptionLevel.WARN, condition, message, args);
    }

    public boolean isTrue(MatrixExceptionLevel exceptionLevel, boolean condition) {
        return isTrue(exceptionLevel, condition, "condition must be true");
    }

    public boolean isTrue(boolean condition) {
        return isTrue(condition, "condition must be true");
    }

    public boolean isFalse(MatrixExceptionLevel level, boolean condition, String message, Object... args) {
        if (condition) {
            dynamicException(level, message, args);
            return true;
        }
        return false;
    }

    public boolean isFalse(boolean condition, String message, Object... args) {
        return isFalse(MatrixExceptionLevel.WARN, condition, message, args);
    }

    public boolean isFalse(MatrixExceptionLevel level, boolean condition) {
        return isFalse(level, condition, "condition must be false");
    }

    public boolean isFalse(boolean condition) {
        return isFalse(condition, "condition must be false");
    }

    public String isBlank(MatrixExceptionLevel level, String string, String message, Object... args) {
        if (StringUtil.INSTANCE.isEmpty(string)) {
            return string;
        }
        dynamicException(level, message, args);
        return string;
    }

    public String isBlank(String string, String message, Object... args) {
        return isBlank(MatrixExceptionLevel.WARN, string, message, args);
    }

    public String isBlank(MatrixExceptionLevel exceptionLevel, String string) {
        return isBlank(exceptionLevel, string, "parameter must be blank");
    }

    public String isBlank(String string) {
        return isBlank(string, "parameter must be blank");
    }

    public String notBlank(MatrixExceptionLevel exceptionLevel, String string, String message, Object... args) {
        if (StringUtil.INSTANCE.isEmpty(string)) {
            dynamicException(exceptionLevel, message, args);
            return string;
        }
        return string;
    }

    public String notBlank(String string, String message, Object... args) {
        return notBlank(MatrixExceptionLevel.WARN, string, message, args);
    }

    public String notBlank(MatrixExceptionLevel exceptionLevel, String string) {
        return notBlank(exceptionLevel, string, "parameter must not be blank");
    }

    public String notBlank(String string) {
        return notBlank(string, "parameter must not be blank");
    }

    public <T> T isNull(MatrixExceptionLevel exceptionLevel, T object, String message, Object... args) {
        if (null == object) {
            return null;
        }
        dynamicException(exceptionLevel, message, args);
        return object;
    }

    public <T> T isNull(T object, String message, Object... args) {
        return isNull(MatrixExceptionLevel.WARN, object, message, args);
    }

    public <T> T isNull(MatrixExceptionLevel exceptionLevel, T object) {
        return isNull(exceptionLevel, object, "parameter must be null");
    }

    public <T> T isNull(T object) {
        return isNull(object, "parameter must be null");
    }

    public <T> T notNull(MatrixExceptionLevel exceptionLevel, T object, String message, Object... args) {
        if (null == object) {
            dynamicException(exceptionLevel, message, args);
            return null;
        }
        return object;
    }

    public <T> T notNull(T object, String message, Object... args) {
        return notNull(MatrixExceptionLevel.WARN, object, message, args);
    }

    public <T> T notNull(MatrixExceptionLevel exceptionLevel, T object) {
        return notNull(exceptionLevel, object, "parameter must not be null");
    }

    public <T> T notNull(T object) {
        return notNull(object, "{jakarta.validation.constraints.NotNull.message}");
    }

    public <T> T isEmpty(MatrixExceptionLevel exceptionLevel, T object, String message, Object... args) {
        if (ObjectUtil.INSTANCE.isEmpty(object)) {
            return object;
        }
        dynamicException(exceptionLevel, message, args);
        return object;
    }

    public <T> T isEmpty(T object, String message, Object... args) {
        return isEmpty(MatrixExceptionLevel.WARN, object, message, args);
    }

    public <T> T isEmpty(MatrixExceptionLevel exceptionLevel, T object) {
        return isEmpty(exceptionLevel, object, "parameter must be empty");
    }

    public <T> T isEmpty(T object) {
        return isEmpty(object, "parameter must be empty");
    }

    public <T> T notEmpty(MatrixExceptionLevel exceptionLevel, T object, String message, Object... args) {
        if (ObjectUtil.INSTANCE.isEmpty(object)) {
            dynamicException(exceptionLevel, message, args);
            return object;
        }
        return object;
    }

    public <T> T notEmpty(T object, String message, Object... args) {
        return notEmpty(MatrixExceptionLevel.WARN, object, message, args);
    }

    public <T> T notEmpty(MatrixExceptionLevel exceptionLevel, T object) {
        return notEmpty(exceptionLevel, object, "parameter must not be empty");
    }

    public <T> T notEmpty(T object) {
        return notEmpty(object, "parameter must not be empty");
    }

    public boolean equals(MatrixExceptionLevel exceptionLevel, Object from, Object to, String message, Object... args) {
        if (Objects.equals(from, to)) {
            return true;
        }
        dynamicException(exceptionLevel, message, args);
        return false;
    }

    public boolean equals(Object from, Object to, String message, Object... args) {
        return equals(MatrixExceptionLevel.WARN, from, to, message, args);
    }

    public boolean equals(MatrixExceptionLevel exceptionLevel, Object from, Object to) {
        return equals(exceptionLevel, from, to, "parameters must be equal");
    }

    public boolean equals(Object from, Object to) {
        return equals(from, to, "parameters must be equal");
    }

    public boolean notEquals(MatrixExceptionLevel exceptionLevel, Object from, Object to, String message, Object... args) {
        if (Objects.equals(from, to)) {
            dynamicException(exceptionLevel, message, args);
            return true;
        }
        return false;
    }

    public boolean notEquals(Object from, Object to, String message, Object... args) {
        return notEquals(MatrixExceptionLevel.WARN, from, to, message, args);
    }

    public boolean notEquals(MatrixExceptionLevel exceptionLevel, Object from, Object to) {
        return notEquals(exceptionLevel, from, to, "parameters must not be equal");
    }

    public boolean notEquals(Object from, Object to) {
        return notEquals(from, to, "parameters must not be equal");
    }


    public void throwException(MatrixExceptionLevel exceptionLevel, String message) {
        switch (exceptionLevel) {
            case WARN:
                throw new MatrixWarnException(message);
            case ERROR:
                throw new MatrixErrorException(message);
            default:
                throw new MatrixInfoException(message);
        }
    }

    public <T extends RuntimeException> void throwException(Class<T> exceptionClass, String message, Object... args) {
        try {
            Constructor<T> constructor = exceptionClass.getConstructor(String.class);
            throw constructor.newInstance(resolveMessage(message, args));
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new MatrixErrorException("The class of exception has't Constructor(String message)");
        }
    }

    private void dynamicException(MatrixExceptionLevel exceptionLevel, String message, Object... args) {
        throwException(exceptionLevel, resolveMessage(message, args));
    }

    private String resolveMessage(DynamicMessage dynamicMessage) {
        Set<ConstraintViolation<DynamicMessage>> results = VALIDATOR.validate(dynamicMessage);
        if (CollectionUtil.INSTANCE.isEmpty(results)) {
            return Symbol.BLANK.getSymbol();
        }
        for (ConstraintViolation<DynamicMessage> result : results) {
            return result.getMessage();
        }
        return Symbol.BLANK.getSymbol();
    }

}
