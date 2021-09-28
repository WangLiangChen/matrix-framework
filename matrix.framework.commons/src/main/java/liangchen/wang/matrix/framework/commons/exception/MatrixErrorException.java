package liangchen.wang.matrix.framework.commons.exception;

/**
 * @author Liangchen.Wang 2021-08-19 20:19
 */
public class MatrixErrorException extends MatrixRuntimeException {
    public MatrixErrorException() {
    }

    public MatrixErrorException(String message) {
        super(message);
    }

    public MatrixErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public MatrixErrorException(Throwable cause) {
        super(cause);
    }

    public MatrixErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
