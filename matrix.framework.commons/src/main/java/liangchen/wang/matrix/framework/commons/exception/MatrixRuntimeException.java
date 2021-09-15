package liangchen.wang.matrix.framework.commons.exception;

/**
 * @author Liangchen.Wang 2021-08-19 9:38
 * Tag/Marker Class
 */
public class MatrixRuntimeException extends RuntimeException {
    /**
     * 异常代码，"" 或 null 表示无异常
     */
    private String code;

    public MatrixRuntimeException() {
    }

    public MatrixRuntimeException(String message) {
        super(message);
    }

    public MatrixRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MatrixRuntimeException(Throwable cause) {
        super(cause);
    }

    public MatrixRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
