package wang.liangchen.matrix.framework.commons.exception;

/**
 * @author Liangchen.Wang 2021-08-19 20:19
 */
public class MatrixInfoException extends MatrixRuntimeException {
    public MatrixInfoException() {
    }

    public MatrixInfoException(String message, Object... args) {
        super(message, args);
    }

    public MatrixInfoException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }

    public MatrixInfoException(Throwable cause) {
        super(cause);
    }

    public MatrixInfoException(Throwable cause, boolean enableSuppression, boolean writableStackTrace, String message, Object... args) {
        super(cause, enableSuppression, writableStackTrace, message, args);
    }
}
