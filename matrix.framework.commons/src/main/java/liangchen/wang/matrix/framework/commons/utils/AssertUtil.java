package liangchen.wang.matrix.framework.commons.utils;

import liangchen.wang.matrix.framework.commons.exception.MatrixRuntimeException;

/**
 * @author Liangchen.Wang 2021-08-19 20:23
 */
public enum AssertUtil {
    /**
     * instance
     */
    INSTANCE;

    public void isTrue(boolean condition, Class<? extends MatrixRuntimeException> exceptionClass, String message, String... args) {
        if (!condition) {
        }
    }

}
