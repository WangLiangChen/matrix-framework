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


    public MatrixInfoException(Throwable throwable, String message, Object... args) {
        super(throwable, message, args);
    }

    public MatrixInfoException(Throwable throwable) {
        super(throwable);
    }

    @Override
    ExceptionLevel exceptionLevel() {
        return ExceptionLevel.INFO;
    }
}
