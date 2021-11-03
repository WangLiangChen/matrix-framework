package wang.liangchen.matrix.framework.commons.exception;

/**
 * @author Liangchen.Wang 2021-08-19 20:19
 */
public class MatrixPromptException extends MatrixRuntimeException {
    public MatrixPromptException() {
    }

    public MatrixPromptException(String message, Object... args) {
        super(message, args);
    }

    public MatrixPromptException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }

    public MatrixPromptException(Throwable cause) {
        super(cause);
    }

    public MatrixPromptException(Throwable cause, boolean enableSuppression, boolean writableStackTrace, String message, Object... args) {
        super(cause, enableSuppression, writableStackTrace, message, args);
    }
}
