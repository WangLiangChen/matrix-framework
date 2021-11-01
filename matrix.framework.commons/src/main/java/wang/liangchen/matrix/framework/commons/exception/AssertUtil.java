package wang.liangchen.matrix.framework.commons.exception;

import wang.liangchen.matrix.framework.commons.utils.CollectionUtil;
import wang.liangchen.matrix.framework.commons.utils.StringUtil;

/**
 * @author Liangchen.Wang 2021-08-19 20:23
 */
public enum AssertUtil {
    /**
     * instance
     */
    INSTANCE;

    public void isTrue(boolean condition, String message, Object... args) {
        isTrue(AssertLevel.INFO, condition, message, args);
    }

    public void isTrue(AssertLevel assertLevel, boolean condition, String message, Object... args) {
        if (!condition) {
            throwException(assertLevel, message, args);
        }
    }

    public void isFalse(boolean condition, String message, Object... args) {
        isFalse(AssertLevel.INFO,condition, message, args);
    }

    public void isFalse(AssertLevel assertLevel, boolean condition, String message, Object... args) {
        if (condition) {
            throwException(assertLevel, message, args);
        }
    }

    public void isBlank(String object, String message, Object... args) {
        isBlank(AssertLevel.INFO, object, message, args);
    }

    public void isBlank(AssertLevel assertLevel, String object, String message, Object... args) {
        if (StringUtil.INSTANCE.isNotBlank(object)) {
            throwException(assertLevel, message, args);
        }
    }

    public void notBlank(String object, String message, Object... args) {
        notBlank(AssertLevel.INFO, object, message, args);
    }

    public void notBlank(AssertLevel assertLevel, String object, String message, Object... args) {
        if (StringUtil.INSTANCE.isBlank(object)) {
            throwException(assertLevel, message, args);
        }
    }

    public void isNull(Object object, String message, Object... args) {
        isNull(AssertLevel.INFO, object, message, args);
    }

    public void isNull(AssertLevel assertLevel, Object object, String message, Object... args) {
        if (null != object) {
            throwException(assertLevel, message, args);
        }
    }

    public void notNull(Object object, String message, Object... args) {
        notNull(AssertLevel.INFO, object, message, args);
    }

    public void notNull(AssertLevel assertLevel, Object object, String message, Object... args) {
        if (null == object) {
            throwException(assertLevel, message, args);
        }
    }

    public void isEmpty(Object object, String message, Object... args) {
        isEmpty(AssertLevel.INFO, object, message, args);
    }

    public void isEmpty(AssertLevel assertLevel, Object object, String message, Object... args) {
        if (CollectionUtil.INSTANCE.isNotEmpty(object)) {
            throwException(assertLevel, message, args);
        }
    }

    public void notEmpty(Object object, String message, Object... args) {
        notEmpty(AssertLevel.INFO, object, message, args);
    }

    public void notEmpty(AssertLevel assertLevel, Object object, String message, Object... args) {
        if (CollectionUtil.INSTANCE.isEmpty(object)) {
            throwException(assertLevel, message, args);
        }
    }

    private void throwException(String message, Object... args) {
        throwException(AssertLevel.INFO, message, args);
    }

    private void throwException(AssertLevel assertLevel, String message, Object... args) {
        String format = StringUtil.INSTANCE.format(message, args);
        switch (assertLevel) {
            case ERROR:
                throw new MatrixErrorException(format);
            case INFO:
                throw new MatrixInfoException(format);
            case PROMPT:
                throw new MatrixPromptException(format);
            default:
                throw new MatrixRuntimeException(format);
        }
    }

}
