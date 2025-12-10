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

    public MatrixWarnException(Throwable throwable, String message, Object... args) {
        super(throwable, message, args);
    }

    public MatrixWarnException(Throwable throwable) {
        super(throwable);
    }

    @Override
    ExceptionLevel exceptionLevel() {
        return ExceptionLevel.WARN;
    }
}
