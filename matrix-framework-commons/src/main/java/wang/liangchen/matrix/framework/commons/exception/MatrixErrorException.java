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


    public MatrixErrorException(Throwable throwable, String message, Object... args) {
        super(throwable, message, args);
    }

    public MatrixErrorException(Throwable throwable) {
        super(throwable);
    }

    @Override
    ExceptionLevel exceptionLevel() {
        return ExceptionLevel.ERROR;
    }
}
