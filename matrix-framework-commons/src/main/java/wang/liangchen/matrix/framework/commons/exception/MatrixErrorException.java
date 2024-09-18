package wang.liangchen.matrix.framework.commons.exception;

/**
 * @author Liangchen.Wang 2021-08-19 20:19
 */
public class MatrixErrorException extends MatrixRuntimeException {
    public MatrixErrorException() {
    }

    public MatrixErrorException(String message, Object... args) {
        super(message, args);
    }

    public MatrixErrorException(Throwable cause) {
        super(cause);
    }
    public MatrixErrorException(Throwable cause, String message, Object... args) {
        super(cause);
    }
}
