package liangchen.wang.matrix.framework.commons.exception;

import liangchen.wang.matrix.framework.commons.utils.StringUtil;

/**
 * @author Liangchen.Wang 2021-08-19 20:19
 */
public class MatrixInfoException extends MatrixRuntimeException {
    public MatrixInfoException() {
    }

    public MatrixInfoException(String message, String... args) {
        super(StringUtil.INSTANCE.format(message, args));
    }

    public MatrixInfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public MatrixInfoException(Throwable cause) {
        super(cause);
    }

    public MatrixInfoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
