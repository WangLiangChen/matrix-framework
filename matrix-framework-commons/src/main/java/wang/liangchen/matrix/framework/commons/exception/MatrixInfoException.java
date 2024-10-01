package wang.liangchen.matrix.framework.commons.exception;

import wang.liangchen.matrix.framework.commons.runtime.MessageWrapper;

/**
 * @author Liangchen.Wang 2021-08-19 20:19
 */
public class MatrixInfoException extends MatrixRuntimeException {
    private final ExceptionLevel level = ExceptionLevel.INFO;

    public MatrixInfoException() {
    }

    public MatrixInfoException(MessageWrapper messageWrapper) {
        super(messageWrapper);
    }

    public MatrixInfoException(String message, Object... args) {
        super(message, args);
    }

    public MatrixInfoException(Throwable throwable, MessageWrapper messageWrapper) {
        super(throwable, messageWrapper);
    }

    public MatrixInfoException(Throwable throwable, String message, Object... args) {
        super(throwable, message, args);
    }

    public MatrixInfoException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public ExceptionLevel getLevel() {
        return this.level;
    }
}
