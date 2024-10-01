package wang.liangchen.matrix.framework.commons.exception;

import wang.liangchen.matrix.framework.commons.runtime.MessageWrapper;

/**
 * @author Liangchen.Wang 2021-08-19 20:19
 */
public class MatrixWarnException extends MatrixRuntimeException {
    private final ExceptionLevel level = ExceptionLevel.WARN;

    public MatrixWarnException() {
    }

    public MatrixWarnException(MessageWrapper messageWrapper) {
        super(messageWrapper);
    }

    public MatrixWarnException(String message, Object... args) {
        super(message, args);
    }

    public MatrixWarnException(Throwable throwable, MessageWrapper messageWrapper) {
        super(throwable, messageWrapper);
    }

    public MatrixWarnException(Throwable throwable, String message, Object... args) {
        super(throwable, message, args);
    }

    public MatrixWarnException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public ExceptionLevel getLevel() {
        return level;
    }
}
