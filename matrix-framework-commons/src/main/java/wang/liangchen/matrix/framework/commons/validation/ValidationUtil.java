package wang.liangchen.matrix.framework.commons.validation;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.AggregateResourceBundleLocator;
import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

/**
 * @author Liangchen.Wang 2022-04-29 15:50
 */
public enum ValidationUtil {
    /**
     * instance
     */
    INSTANCE;

    private final static ThreadLocal<Locale> threadLocale = InheritableThreadLocal.withInitial(Locale::getDefault);
    private final static ValidatorFactory VALIDATOR_FACTORY = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ResourceBundleMessageInterpolator(new AggregateResourceBundleLocator(new ArrayList<String>() {{
                add("i18n/messages");
            }}), Collections.emptySet(), Locale.getDefault(), localeResolverContext -> threadLocale.get(), false))
            .buildValidatorFactory();
    private final static Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();

    public void setLocale(Locale locale) {
        threadLocale.set(locale);
    }

    public void close() {
        if (null == VALIDATOR_FACTORY) {
            return;
        }
        VALIDATOR_FACTORY.close();
    }

    public <T> T validate(ExceptionLevel exceptionLevel, T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> results = VALIDATOR.validate(object, groups);
        if (CollectionUtil.INSTANCE.isEmpty(results)) {
            return object;
        }
        StringBuilder messageBuilder = new StringBuilder();
        results.forEach(e -> messageBuilder.append(Symbol.LINE_SEPARATOR.getSymbol())
                .append("'").append(e.getPropertyPath()).append("' ")
                .append(e.getMessage()).append(Symbol.SEMICOLON.getSymbol()));
        handleException(exceptionLevel, messageBuilder.toString());
        return object;
    }

    public <T> T validate(T object, Class<?>... groups) {
        return validate(ExceptionLevel.INFO, object, groups);
    }

    public boolean isTrue(ExceptionLevel exceptionLevel, boolean condition, String message, Object... args) {
        if (condition) {
            return true;
        }
        handleException(exceptionLevel, message, args);
        return false;
    }

    public boolean isTrue(boolean condition, String message, Object... args) {
        return isTrue(ExceptionLevel.INFO, condition, message, args);
    }

    public boolean isTrue(ExceptionLevel exceptionLevel, boolean condition) {
        return isTrue(exceptionLevel, condition, "condition must be true");
    }

    public boolean isTrue(boolean condition) {
        return isTrue(condition, "condition must be true");
    }

    public boolean isFalse(ExceptionLevel level, boolean condition, String message, Object... args) {
        if (condition) {
            handleException(level, message, args);
            return true;
        }
        return false;
    }

    public boolean isFalse(boolean condition, String message, Object... args) {
        return isFalse(ExceptionLevel.INFO, condition, message, args);
    }

    public boolean isFalse(ExceptionLevel level, boolean condition) {
        return isFalse(level, condition, "condition must be false");
    }

    public boolean isFalse(boolean condition) {
        return isFalse(condition, "condition must be false");
    }

    public String isBlank(ExceptionLevel level, String string, String message, Object... args) {
        if (StringUtil.INSTANCE.isBlank(string)) {
            return string;
        }
        handleException(level, message, args);
        return string;
    }

    public String isBlank(String string, String message, Object... args) {
        return isBlank(ExceptionLevel.INFO, string, message, args);
    }

    public String isBlank(ExceptionLevel exceptionLevel, String string) {
        return isBlank(exceptionLevel, string, "parameter must be blank");
    }

    public String isBlank(String string) {
        return isBlank(string, "parameter must be blank");
    }

    public String notBlank(ExceptionLevel exceptionLevel, String string, String message, Object... args) {
        if (StringUtil.INSTANCE.isBlank(string)) {
            handleException(exceptionLevel, message, args);
            return string;
        }
        return string;
    }

    public String notBlank(String string, String message, Object... args) {
        return notBlank(ExceptionLevel.INFO, string, message, args);
    }

    public String notBlank(ExceptionLevel exceptionLevel, String string) {
        return notBlank(exceptionLevel, string, "parameter must not be blank");
    }

    public String notBlank(String string) {
        return notBlank(string, "parameter must not be blank");
    }

    public <T> T isNull(ExceptionLevel exceptionLevel, T object, String message, Object... args) {
        if (null == object) {
            return null;
        }
        handleException(exceptionLevel, message, args);
        return object;
    }

    public <T> T isNull(T object, String message, Object... args) {
        return isNull(ExceptionLevel.INFO, object, message, args);
    }

    public <T> T isNull(ExceptionLevel exceptionLevel, T object) {
        return isNull(exceptionLevel, object, "parameter must be null");
    }

    public <T> T isNull(T object) {
        return isNull(object, "parameter must be null");
    }

    public <T> T notNull(ExceptionLevel exceptionLevel, T object, String message, Object... args) {
        if (null == object) {
            handleException(exceptionLevel, message, args);
            return null;
        }
        return object;
    }

    public <T> T notNull(T object, String message, Object... args) {
        return notNull(ExceptionLevel.INFO, object, message, args);
    }

    public <T> T notNull(ExceptionLevel exceptionLevel, T object) {
        return notNull(exceptionLevel, object, "parameter must not be null");
    }

    public <T> T notNull(T object) {
        return notNull(object, "parameter must not be null");
    }

    public <T> T isEmpty(ExceptionLevel exceptionLevel, T object, String message, Object... args) {
        if (ObjectUtil.INSTANCE.isEmpty(object)) {
            return object;
        }
        handleException(exceptionLevel, message, args);
        return object;
    }

    public <T> T isEmpty(T object, String message, Object... args) {
        return isEmpty(ExceptionLevel.INFO, object, message, args);
    }

    public <T> T isEmpty(ExceptionLevel exceptionLevel, T object) {
        return isEmpty(exceptionLevel, object, "parameter must be empty");
    }

    public <T> T isEmpty(T object) {
        return isEmpty(object, "parameter must be empty");
    }

    public <T> T notEmpty(ExceptionLevel exceptionLevel, T object, String message, Object... args) {
        if (ObjectUtil.INSTANCE.isEmpty(object)) {
            handleException(exceptionLevel, message, args);
            return object;
        }
        return object;
    }

    public <T> T notEmpty(T object, String message, Object... args) {
        return notEmpty(ExceptionLevel.INFO, object, message, args);
    }

    public <T> T notEmpty(ExceptionLevel exceptionLevel, T object) {
        return notEmpty(exceptionLevel, object, "parameter must not be empty");
    }

    public <T> T notEmpty(T object) {
        return notEmpty(object, "parameter must not be empty");
    }

    public boolean equals(ExceptionLevel exceptionLevel, Object from, Object to, String message, Object... args) {
        if (Objects.equals(from, to)) {
            return true;
        }
        handleException(exceptionLevel, message, args);
        return false;
    }

    public boolean equals(Object from, Object to, String message, Object... args) {
        return equals(ExceptionLevel.INFO, from, to, message, args);
    }

    public boolean equals(ExceptionLevel exceptionLevel, Object from, Object to) {
        return equals(exceptionLevel, from, to, "parameters must be equal");
    }

    public boolean equals(Object from, Object to) {
        return equals(from, to, "parameters must be equal");
    }

    public boolean notEquals(ExceptionLevel exceptionLevel, Object from, Object to, String message, Object... args) {
        if (Objects.equals(from, to)) {
            handleException(exceptionLevel, message, args);
            return true;
        }
        return false;
    }

    public boolean notEquals(Object from, Object to, String message, Object... args) {
        return notEquals(ExceptionLevel.INFO, from, to, message, args);
    }

    public boolean notEquals(ExceptionLevel exceptionLevel, Object from, Object to) {
        return notEquals(exceptionLevel, from, to, "parameters must not be equal");
    }

    public boolean notEquals(Object from, Object to) {
        return notEquals(from, to, "parameters must not be equal");
    }

    private void handleException(ExceptionLevel exceptionLevel, String message, Object... args) {
        switch (exceptionLevel) {
            case WARN:
                throw new MatrixWarnException(message, args);
            case ERROR:
                throw new MatrixErrorException(message, args);
            default:
                throw new MatrixInfoException(message, args);
        }
    }
}
