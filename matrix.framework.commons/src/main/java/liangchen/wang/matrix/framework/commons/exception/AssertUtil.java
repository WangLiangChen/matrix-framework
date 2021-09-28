package liangchen.wang.matrix.framework.commons.exception;

import liangchen.wang.matrix.framework.commons.exception.MatrixRuntimeException;
import liangchen.wang.matrix.framework.commons.utils.StringUtil;

/**
 * @author Liangchen.Wang 2021-08-19 20:23
 */
public enum AssertUtil {
    /**
     * instance
     */
    INSTANCE;

    public void isTrue(AssertLevel assertLevel, boolean condition, String message, String... args) {
        if (!condition) {
            throw exceptionByLevel(assertLevel, message, args);
        }
    }

    private MatrixRuntimeException exceptionByLevel(AssertLevel assertLevel, String message, String... args) {
        String format = StringUtil.INSTANCE.format(message, args);
        switch (assertLevel) {
            case INFO:
                return new MatrixInfoException(format);
            case PROMPT:
                return new MatrixPromptException(format);
            default:
                return new MatrixRuntimeException(format);
        }
    }

}
