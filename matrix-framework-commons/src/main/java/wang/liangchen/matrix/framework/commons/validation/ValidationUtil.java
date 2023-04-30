package wang.liangchen.matrix.framework.commons.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.resourceloading.AggregateResourceBundleLocator;
import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
            .messageInterpolator(new TranscodingResourceBundleMessageInterpolator(
                    new AggregateResourceBundleLocator(Arrays.asList("wang.liangchen.matrix.framework.validator.messages", "i18n.messages"), new MatrixResourceBundleLocator()),
                    Collections.emptySet(),
                    Locale.getDefault(),
                    localeResolverContext -> ValidationUtil.getOrDefaultLocale(),
                    false))
            .buildValidatorFactory();
    private final static Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();

    public void setLocale(Locale locale) {
        threadLocale.set(locale);
    }

    public Locale getLocale() {
        return threadLocale.get();
    }

    public static Locale getOrDefaultLocale() {
        Locale locale = threadLocale.get();
        return null == locale ? Locale.getDefault() : locale;
    }

    public void removeLocale() {
        threadLocale.remove();
    }

    public void close() {
        if (null == VALIDATOR_FACTORY) {
            return;
        }
        VALIDATOR_FACTORY.close();
    }

    public String resolveDynamicMessage(String dynamicMessage, Object... args) {
        return resolveDynamicMessage(DynamicMessage.newInstantce(dynamicMessage), args);
    }

    public String resolveDynamicMessage(DynamicMessage dynamicMessage, Object... args) {
        Set<ConstraintViolation<DynamicMessage>> results = VALIDATOR.validate(dynamicMessage);
        if (CollectionUtil.INSTANCE.isEmpty(results)) {
            return Symbol.BLANK.getSymbol();
        }
        for (ConstraintViolation<DynamicMessage> result : results) {
            return StringUtil.INSTANCE.format(result.getMessage(), args);
        }
        return Symbol.BLANK.getSymbol();
    }

    public <T extends RuntimeException> void throwException(Class<T> exceptionClass, String message, Object... args) {
        try {
            Constructor<T> constructor = exceptionClass.getConstructor(String.class);
            throw constructor.newInstance(resolveDynamicMessage(message, args));
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new MatrixErrorException("The class of exception has't Constructor(String message)");
        }
    }

    public <T> T validate(ExceptionLevel exceptionLevel, T object, Class<?>... groups) {
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
        return validate(ExceptionLevel.WARN, object, groups);
    }

    public boolean isTrue(ExceptionLevel exceptionLevel, boolean condition, String message, Object... args) {
        if (condition) {
            return true;
        }
        dynamicException(exceptionLevel, message, args);
        return false;
    }

    public boolean isTrue(boolean condition, String message, Object... args) {
        return isTrue(ExceptionLevel.WARN, condition, message, args);
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
        return isFalse(ExceptionLevel.WARN, condition, message, args);
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
        dynamicException(level, message, args);
        return string;
    }

    public String isBlank(String string, String message, Object... args) {
        return isBlank(ExceptionLevel.WARN, string, message, args);
    }

    public String isBlank(ExceptionLevel exceptionLevel, String string) {
        return isBlank(exceptionLevel, string, "parameter must be blank");
    }

    public String isBlank(String string) {
        return isBlank(string, "parameter must be blank");
    }

    public String notBlank(ExceptionLevel exceptionLevel, String string, String message, Object... args) {
        if (StringUtil.INSTANCE.isBlank(string)) {
            dynamicException(exceptionLevel, message, args);
            return string;
        }
        return string;
    }

    public String notBlank(String string, String message, Object... args) {
        return notBlank(ExceptionLevel.WARN, string, message, args);
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
        dynamicException(exceptionLevel, message, args);
        return object;
    }

    public <T> T isNull(T object, String message, Object... args) {
        return isNull(ExceptionLevel.WARN, object, message, args);
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
        return notNull(ExceptionLevel.WARN, object, message, args);
    }

    public <T> T notNull(ExceptionLevel exceptionLevel, T object) {
        return notNull(exceptionLevel, object, "parameter must not be null");
    }

    public <T> T notNull(T object) {
        return notNull(object, "{jakarta.validation.constraints.NotNull.message}");
    }

    public <T> T isEmpty(ExceptionLevel exceptionLevel, T object, String message, Object... args) {
        if (ObjectUtil.INSTANCE.isEmpty(object)) {
            return object;
        }
        dynamicException(exceptionLevel, message, args);
        return object;
    }

    public <T> T isEmpty(T object, String message, Object... args) {
        return isEmpty(ExceptionLevel.WARN, object, message, args);
    }

    public <T> T isEmpty(ExceptionLevel exceptionLevel, T object) {
        return isEmpty(exceptionLevel, object, "parameter must be empty");
    }

    public <T> T isEmpty(T object) {
        return isEmpty(object, "parameter must be empty");
    }

    public <T> T notEmpty(ExceptionLevel exceptionLevel, T object, String message, Object... args) {
        if (ObjectUtil.INSTANCE.isEmpty(object)) {
            dynamicException(exceptionLevel, message, args);
            return object;
        }
        return object;
    }

    public <T> T notEmpty(T object, String message, Object... args) {
        return notEmpty(ExceptionLevel.WARN, object, message, args);
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
        dynamicException(exceptionLevel, message, args);
        return false;
    }

    public boolean equals(Object from, Object to, String message, Object... args) {
        return equals(ExceptionLevel.WARN, from, to, message, args);
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
        return notEquals(ExceptionLevel.WARN, from, to, message, args);
    }

    public boolean notEquals(ExceptionLevel exceptionLevel, Object from, Object to) {
        return notEquals(exceptionLevel, from, to, "parameters must not be equal");
    }

    public boolean notEquals(Object from, Object to) {
        return notEquals(from, to, "parameters must not be equal");
    }

    private void dynamicException(ExceptionLevel exceptionLevel, String message, Object... args) {
        throwException(exceptionLevel, resolveDynamicMessage(message, args));
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
}
