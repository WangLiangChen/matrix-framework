package wang.liangchen.matrix.framework.commons.exception;

/**
 * @author Liangchen.Wang 2021-08-19 9:38
 */
public class MatrixRuntimeException extends AbstractMatrixRuntimeException {

    public MatrixRuntimeException() {
    }

    public MatrixRuntimeException(String message, Object... args) {
        super(message, args);
    }

    public MatrixRuntimeException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }

    public MatrixRuntimeException(Throwable cause) {
        super(cause);
    }

    @Override
    ExceptionLevel exceptionLevel() {
        return ExceptionLevel.OFF;
    }
}
