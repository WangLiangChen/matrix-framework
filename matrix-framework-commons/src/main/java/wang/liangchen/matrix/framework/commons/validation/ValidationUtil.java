package wang.liangchen.matrix.framework.commons.validation;

import jakarta.validation.*;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.runtime.LocaleTimeZoneContext;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

/**
 * @author Liangchen.Wang 2022-04-29 15:50
 */
public enum ValidationUtil {
    /**
     * instance
     */
    INSTANCE;
    private final static Pattern I18N_KEY_PATTERN = Pattern.compile("^\\{[^{}]+?}$");
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ValidatorFactory VALIDATOR_FACTORY;
    private volatile Validator VALIDATOR;

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
        throwDynamicException(exceptionLevel, message, args);
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
            throwDynamicException(level, message, args);
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
        throwDynamicException(exceptionLevel, message, args);
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
            throwDynamicException(exceptionLevel, message, args);
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
        throwDynamicException(exceptionLevel, message, args);
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
            throwDynamicException(exceptionLevel, message, args);
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
        throwDynamicException(level, message, args);
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
            throwDynamicException(exceptionLevel, message, args);
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
        throwDynamicException(exceptionLevel, message, args);
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
            throwDynamicException(exceptionLevel, message, args);
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

    public String extractI18nKey(String i18n) {
        if (StringUtil.INSTANCE.isNullOrBlank(i18n)) {
            return null;
        }
        if (i18n.length() < 3) {
            return null;
        }
        if (isI18n(i18n)) {
            return i18n.substring(1, i18n.length() - 1);
        }
        return null;
    }

    public boolean isI18n(String i18n) {
        if (null == i18n || i18n.isBlank()) {
            return false;
        }
        return I18N_KEY_PATTERN.matcher(i18n).matches();
    }

    public String resolveI18n(String i18n, Object... args) {
        return resolveDynamicMessage(i18n, args);
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

    private <T> void handleValidationResult(ExceptionLevel exceptionLevel, Set<ConstraintViolation<T>> results) {
        StringBuilder messageBuilder = new StringBuilder();
        results.forEach(e -> {
            messageBuilder.append(e.getMessage()).append(Symbol.OPEN_PAREN).append(e.getPropertyPath()).append(Symbol.CLOSE_PAREN).append(Symbol.SEMICOLON);
        });
        throwException(exceptionLevel, messageBuilder.toString());
    }


    private void throwDynamicException(ExceptionLevel exceptionLevel, String i18n, Object... args) {
        throwException(exceptionLevel, resolveI18n(i18n, args));
    }

    private String resolveDynamicMessage(String i18n, Object... args) {
        String message = resolveDynamicMessage(DynamicMessage.newInstance(i18n));
        // 替换值中的占位符
        return StringUtil.INSTANCE.format(message, args);
    }

    private String resolveDynamicMessage(DynamicMessage dynamicMessage) {
        Set<ConstraintViolation<DynamicMessage>> results = VALIDATOR.validate(dynamicMessage);
        if (CollectionUtil.INSTANCE.isEmpty(results)) {
            return Symbol.EMPTY.getSymbol();
        }
        for (ConstraintViolation<DynamicMessage> result : results) {
            return result.getMessage();
        }
        return Symbol.EMPTY.getSymbol();
    }
}
