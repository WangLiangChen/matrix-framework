package liangchen.wang.matrix.framework.commons.exception;

import liangchen.wang.matrix.framework.commons.utils.StringUtil;

/**
 * @author Liangchen.Wang 2021-08-19 20:23
 */
public enum AssertUtil {
    /**
     * instance
     */
    INSTANCE;

    public void isTrue(boolean condition, String message, String... args) {
        isTrue(AssertLevel.INFO, condition, message, args);
    }

    public void isTrue(AssertLevel assertLevel, boolean condition, String message, String... args) {
        if (!condition) {
            throwException(assertLevel, message, args);
        }
    }

    public void isFalse(boolean condition, String message, String... args) {
        isFalse(condition, message, args);
    }

    public void isFalse(AssertLevel assertLevel, boolean condition, String message, String... args) {
        if (condition) {
            throwException(assertLevel, message, args);
        }
    }

    public void isBlank(String object, String message, String... args) {
        isBlank(AssertLevel.INFO, object, message, args);
    }

    public void isBlank(AssertLevel assertLevel, String object, String message, String... args) {
        if (StringUtil.INSTANCE.isNotBlank(object)) {
            throwException(assertLevel, message, args);
        }
    }

    public void notBlank(String object, String message, String... args) {
        notBlank(AssertLevel.INFO, object, message, args);
    }

    public void notBlank(AssertLevel assertLevel, String object, String message, String... args) {
        if (StringUtil.INSTANCE.isBlank(object)) {
            throwException(assertLevel, message, args);
        }
    }

    private void throwException(String message, String... args) {
        throwException(AssertLevel.INFO, message, args);
    }

    private void throwException(AssertLevel assertLevel, String message, String... args) {
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
