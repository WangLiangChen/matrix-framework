package wang.liangchen.matrix.framework.commons.validation;

import jakarta.validation.*;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import wang.liangchen.matrix.framework.commons.CollectionUtil;
import wang.liangchen.matrix.framework.commons.StringUtil;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.runtime.LocaleTimeZoneContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
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
    private static final Pattern VALIDATION_PATTERN = Pattern.compile("^\\{[^{}]+?}$");

    private final ValidatorFactory VALIDATOR_FACTORY;
    private Validator VALIDATOR;

    ValidationUtil() {
        MessageInterpolator messageInterpolator = new ResourceBundleMessageInterpolator(Collections.emptySet(), Locale.getDefault(), context -> LocaleTimeZoneContext.INSTANCE.getLocale(), false);
        Configuration<?> configuration = Validation.byDefaultProvider().configure().messageInterpolator(messageInterpolator);
        VALIDATOR_FACTORY = configuration.buildValidatorFactory();
        VALIDATOR = VALIDATOR_FACTORY.getValidator();
    }

    public void resetValidator(Validator validator) {
        if (null != VALIDATOR_FACTORY) {
            VALIDATOR_FACTORY.close();
        }
        VALIDATOR = validator;
    }

    public <T> T validate(ExceptionLevel exceptionLevel, T object, Class<?>... groups) {
        if (null == object) {
            throw new MatrixErrorException("The object must not be null");
        }
        Set<ConstraintViolation<T>> results = VALIDATOR.validate(object, groups);
        if (CollectionUtil.INSTANCE.isEmpty(results)) {
            return object;
        }
        handleValidationResult(exceptionLevel, results);
        return object;
    }

    public <T> T validate(T object, Class<?>... groups) {
        return validate(ExceptionLevel.ERROR, object, groups);
    }


    public <T> T validateProperty(ExceptionLevel exceptionLevel, T object, String propertyName, Class<?>... groups) {
        if (null == object) {
            throw new MatrixErrorException("The object must not be null");
        }
        if (StringUtil.INSTANCE.isNullOrBlank(propertyName)) {
            throw new MatrixErrorException("The propertyName must not be null or blank");
        }
        Set<ConstraintViolation<T>> results = VALIDATOR.validateProperty(object, propertyName, groups);
        if (CollectionUtil.INSTANCE.isEmpty(results)) {
            return object;
        }
        handleValidationResult(exceptionLevel, results);
        return object;
    }

    public <T> T validateProperty(T object, String propertyName, Class<?>... groups) {
        return validateProperty(ExceptionLevel.ERROR, object, propertyName, groups);
    }


    public boolean isTrue(ExceptionLevel exceptionLevel, boolean condition, String message, Object... args) {
        if (condition) {
            return true;
        }
        dynamicException(exceptionLevel, message, args);
        return false;
    }

    public boolean isTrue(boolean condition, String message, Object... args) {
        return isTrue(ExceptionLevel.ERROR, condition, message, args);
    }

    public boolean isTrue(ExceptionLevel exceptionLevel, boolean condition) {
        return isTrue(exceptionLevel, condition, "condition must be true");
    }

    public boolean isTrue(boolean condition) {
        return isTrue(condition, "condition must be true");
    }

    public boolean isFalse(ExceptionLevel level, boolean condition, String message, Object... args) {
        if (condition) {
            dynamicException(level, message, args);
            return true;
        }
        return false;
    }

    public boolean isFalse(boolean condition, String message, Object... args) {
        return isFalse(ExceptionLevel.ERROR, condition, message, args);
    }

    public boolean isFalse(ExceptionLevel level, boolean condition) {
        return isFalse(level, condition, "condition must be false");
    }

    public boolean isFalse(boolean condition) {
        return isFalse(condition, "condition must be false");
    }

    public <T> T isNull(ExceptionLevel exceptionLevel, T object, String message, Object... args) {
        if (null == object) {
            return null;
        }
        dynamicException(exceptionLevel, message, args);
        return object;
    }

    public <T> T isNull(T object, String message, Object... args) {
        return isNull(ExceptionLevel.ERROR, object, message, args);
    }

    public <T> T isNull(ExceptionLevel exceptionLevel, T object) {
        return isNull(exceptionLevel, object, "parameter must be null");
    }

    public <T> T isNull(T object) {
        return isNull(object, "parameter must be null");
    }

    public <T> T notNull(ExceptionLevel exceptionLevel, T object, String message, Object... args) {
        if (null == object) {
            dynamicException(exceptionLevel, message, args);
            return null;
        }
        return object;
    }

    public <T> T notNull(T object, String message, Object... args) {
        return notNull(ExceptionLevel.ERROR, object, message, args);
    }

    public <T> T notNull(ExceptionLevel exceptionLevel, T object) {
        return notNull(exceptionLevel, object, "parameter must not be null");
    }

    public <T> T notNull(T object) {
        return notNull(object, "parameter must not be null");
    }

    public <T> T isNullOrEmpty(ExceptionLevel exceptionLevel, T object, String message, Object... args) {
        if (ObjectUtil.INSTANCE.isEmpty(object)) {
            return object;
        }
        dynamicException(exceptionLevel, message, args);
        return object;
    }

    public <T> T isNullOrEmpty(T object, String message, Object... args) {
        return isNullOrEmpty(ExceptionLevel.ERROR, object, message, args);
    }

    public <T> T isNullOrEmpty(ExceptionLevel exceptionLevel, T object) {
        return isNullOrEmpty(exceptionLevel, object, "parameter must be empty");
    }

    public <T> T isNullOrEmpty(T object) {
        return isNullOrEmpty(object, "parameter must be empty");
    }

    public <T> T notNullAndEmpty(ExceptionLevel exceptionLevel, T object, String message, Object... args) {
        if (ObjectUtil.INSTANCE.isEmpty(object)) {
            dynamicException(exceptionLevel, message, args);
            return object;
        }
        return object;
    }

    public <T> T notNullAndEmpty(T object, String message, Object... args) {
        return notNullAndEmpty(ExceptionLevel.ERROR, object, message, args);
    }

    public <T> T notNullAndEmpty(ExceptionLevel exceptionLevel, T object) {
        return notNullAndEmpty(exceptionLevel, object, "parameter must not be empty");
    }

    public <T> T notNullAndEmpty(T object) {
        return notNullAndEmpty(object, "parameter must not be empty");
    }

    public String isNullOrBlank(ExceptionLevel level, String string, String message, Object... args) {
        if (StringUtil.INSTANCE.isNullOrEmpty(string)) {
            return string;
        }
        dynamicException(level, message, args);
        return string;
    }

    public String isNullOrBlank(String string, String message, Object... args) {
        return isNullOrBlank(ExceptionLevel.ERROR, string, message, args);
    }

    public String isNullOrBlank(ExceptionLevel exceptionLevel, String string) {
        return isNullOrBlank(exceptionLevel, string, "parameter must be blank");
    }

    public String isNullOrBlank(String string) {
        return isNullOrBlank(string, "parameter must be blank");
    }

    public String notNullAndBlank(ExceptionLevel exceptionLevel, String string, String message, Object... args) {
        if (StringUtil.INSTANCE.isNullOrEmpty(string)) {
            dynamicException(exceptionLevel, message, args);
            return string;
        }
        return string;
    }

    public String notNullAndBlank(String string, String message, Object... args) {
        return notNullAndBlank(ExceptionLevel.ERROR, string, message, args);
    }

    public String notNullAndBlank(ExceptionLevel exceptionLevel, String string) {
        return notNullAndBlank(exceptionLevel, string, "parameter must not be blank");
    }

    public String notNullAndBlank(String string) {
        return notNullAndBlank(string, "parameter must not be blank");
    }


    public boolean equals(ExceptionLevel exceptionLevel, Object from, Object to, String message, Object... args) {
        if (Objects.equals(from, to)) {
            return true;
        }
        dynamicException(exceptionLevel, message, args);
        return false;
    }

    public boolean equals(Object from, Object to, String message, Object... args) {
        return equals(ExceptionLevel.ERROR, from, to, message, args);
    }

    public boolean equals(ExceptionLevel exceptionLevel, Object from, Object to) {
        return equals(exceptionLevel, from, to, "parameters must be equal");
    }

    public boolean equals(Object from, Object to) {
        return equals(from, to, "parameters must be equal");
    }

    public boolean notEquals(ExceptionLevel exceptionLevel, Object from, Object to, String message, Object... args) {
        if (Objects.equals(from, to)) {
            dynamicException(exceptionLevel, message, args);
            return true;
        }
        return false;
    }

    public boolean notEquals(Object from, Object to, String message, Object... args) {
        return notEquals(ExceptionLevel.ERROR, from, to, message, args);
    }

    public boolean notEquals(ExceptionLevel exceptionLevel, Object from, Object to) {
        return notEquals(exceptionLevel, from, to, "parameters must not be equal");
    }

    public boolean notEquals(Object from, Object to) {
        return notEquals(from, to, "parameters must not be equal");
    }

    public String resolveI18n(String message) {
        if (StringUtil.INSTANCE.isNullOrBlank(message)) {
            return null;
        }
        if (message.length() < 3) {
            return null;
        }
        if (isI18nKey(message)) {
            return message.substring(1, message.length() - 1);
        }
        return null;
    }

    public String resolveMessage(String message, Object... args) {
        if (null == message) {
            return null;
        }
        if (isI18nKey(message)) {
            message = resolveMessage(DynamicMessage.newInstantce(message));
        }
        // 如果仍然是i18n,说明未匹配
        if (isI18nKey(message)) {
            return message;
        }
        // i18n匹配成功或者无需i18n，都需要格式化
        return StringUtil.INSTANCE.format(message, args);
    }

    private <T> void handleValidationResult(ExceptionLevel exceptionLevel, Set<ConstraintViolation<T>> results) {
        StringBuilder messageBuilder = new StringBuilder();
        results.forEach(e -> {
            messageBuilder.append(e.getMessage()).append(Symbol.OPEN_PAREN).append(e.getPropertyPath()).append(Symbol.CLOSE_PAREN).append(Symbol.SEMICOLON);
        });
        throwException(exceptionLevel, messageBuilder.toString());
    }

    public void throwException(ExceptionLevel exceptionLevel, String message) {
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
            throw new MatrixErrorException("The exception class hasn't Constructor(String message)");
        }
    }

    private void dynamicException(ExceptionLevel exceptionLevel, String message, Object... args) {
        throwException(exceptionLevel, resolveMessage(message, args));
    }

    private String resolveMessage(DynamicMessage dynamicMessage) {
        Set<ConstraintViolation<DynamicMessage>> results = VALIDATOR.validate(dynamicMessage);
        if (CollectionUtil.INSTANCE.isEmpty(results)) {
            return Symbol.EMPTY.getSymbol();
        }
        for (ConstraintViolation<DynamicMessage> result : results) {
            return result.getMessage();
        }
        return Symbol.EMPTY.getSymbol();
    }

    private boolean isI18nKey(String message) {
        return VALIDATION_PATTERN.matcher(message).matches();
    }
}
