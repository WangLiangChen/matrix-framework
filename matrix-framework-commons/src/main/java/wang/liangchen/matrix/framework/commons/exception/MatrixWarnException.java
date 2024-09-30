package wang.liangchen.matrix.framework.commons.exception;

/**
 * @author Liangchen.Wang 2021-08-19 20:19
 */
public class MatrixWarnException extends MatrixRuntimeException {
    private final ExceptionLevel level = ExceptionLevel.WARN;

    public MatrixWarnException() {
        super();
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

    @Override
    public ExceptionLevel getLevel() {
        return level;
    }
}
