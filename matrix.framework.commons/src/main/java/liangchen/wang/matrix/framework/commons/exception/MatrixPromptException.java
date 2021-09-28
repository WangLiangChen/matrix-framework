package liangchen.wang.matrix.framework.commons.exception;

/**
 * @author Liangchen.Wang 2021-08-19 20:19
 */
public class MatrixPromptException extends MatrixRuntimeException {
    public MatrixPromptException() {
    }

    public MatrixPromptException(String message) {
        super(message);
    }

    public MatrixPromptException(String message, Throwable cause) {
        super(message, cause);
    }

    public MatrixPromptException(Throwable cause) {
        super(cause);
    }

    public MatrixPromptException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
