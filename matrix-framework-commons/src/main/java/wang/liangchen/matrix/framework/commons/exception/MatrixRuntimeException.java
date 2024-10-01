package wang.liangchen.matrix.framework.commons.exception;

import wang.liangchen.matrix.framework.commons.runtime.MessageWrapper;

/**
 * @author Liangchen.Wang 2021-08-19 9:38
 */
public class MatrixRuntimeException extends RuntimeException {
    private final ExceptionLevel level = ExceptionLevel.OFF;
    private final MessageWrapper messageWrapper;


    public MatrixRuntimeException() {
        this.messageWrapper = null;
    }

    public MatrixRuntimeException(MessageWrapper messageWrapper) {
        super(messageWrapper.getMessage());
        this.messageWrapper = messageWrapper;
    }

    public MatrixRuntimeException(String message, Object... args) {
        this(MessageWrapper.of(message, args));
    }

    public MatrixRuntimeException(Throwable throwable, MessageWrapper messageWrapper) {
        super(messageWrapper.getMessage(), throwable);
        this.messageWrapper = messageWrapper;
    }

    public MatrixRuntimeException(Throwable throwable, String message, Object... args) {
        this(throwable, MessageWrapper.of(message, args));
    }

    public MatrixRuntimeException(Throwable throwable) {
        super(throwable);
        this.messageWrapper = null;
    }

    public ExceptionLevel getLevel() {
        return level;
    }

    public MessageWrapper getMessageWrapper() {
        return messageWrapper;
    }
}
