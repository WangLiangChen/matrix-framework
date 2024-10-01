package wang.liangchen.matrix.framework.commons.exception;

import wang.liangchen.matrix.framework.commons.runtime.MessageWrapper;

/**
 * @author Liangchen.Wang 2021-08-19 20:19
 */
public class MatrixErrorException extends MatrixRuntimeException {
    private final ExceptionLevel level = ExceptionLevel.ERROR;

    public MatrixErrorException() {
    }

    public MatrixErrorException(MessageWrapper messageWrapper) {
        super(messageWrapper);
    }

    public MatrixErrorException(String message, Object... args) {
        super(message, args);
    }

    public MatrixErrorException(Throwable throwable, MessageWrapper messageWrapper) {
        super(throwable, messageWrapper);
    }

    public MatrixErrorException(Throwable throwable, String message, Object... args) {
        super(throwable, message, args);
    }

    public MatrixErrorException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public ExceptionLevel getLevel() {
        return this.level;
    }
}
