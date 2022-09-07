package wang.liangchen.matrix.framework.commons.exception;

/**
 * @author Liangchen.Wang 2021-08-19 20:19
 */
public class MatrixWarnException extends MatrixRuntimeException {
    public MatrixWarnException() {
    }

    public MatrixWarnException(String message, Object... args) {
        super(message, args);
    }

    public MatrixWarnException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }

    public MatrixWarnException(Throwable cause) {
        super(cause);
    }

    public MatrixWarnException(Throwable cause, boolean enableSuppression, boolean writableStackTrace, String message, Object... args) {
        super(cause, enableSuppression, writableStackTrace, message, args);
    }
}
