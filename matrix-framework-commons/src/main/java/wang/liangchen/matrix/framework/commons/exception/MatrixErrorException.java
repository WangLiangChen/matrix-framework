package wang.liangchen.matrix.framework.commons.exception;

/**
 * @author Liangchen.Wang 2021-08-19 20:19
 */
public class MatrixErrorException extends MatrixRuntimeException {
    private final ExceptionLevel level = ExceptionLevel.ERROR;

    public MatrixErrorException() {
        super();
    }

    public MatrixErrorException(String message, Object... args) {
        super(message, args);
    }

    public MatrixErrorException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }

    public MatrixErrorException(Throwable cause) {
        super(cause);
    }

    @Override
    public ExceptionLevel getLevel() {
        return this.level;
    }
}
