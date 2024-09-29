package wang.liangchen.matrix.framework.commons.exception;

/**
 * @author Liangchen.Wang 2021-08-19 20:19
 */
public class MatrixInfoException extends MatrixRuntimeException {
    private final ExceptionLevel level = ExceptionLevel.INFO;

    public MatrixInfoException() {
        super();
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

    @Override
    public ExceptionLevel getLevel() {
        return this.level;
    }
}
