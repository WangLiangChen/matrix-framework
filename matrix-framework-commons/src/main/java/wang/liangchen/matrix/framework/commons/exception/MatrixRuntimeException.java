package wang.liangchen.matrix.framework.commons.exception;

import wang.liangchen.matrix.framework.commons.runtime.I18nMessage;
import wang.liangchen.matrix.framework.commons.runtime.Message;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

/**
 * @author Liangchen.Wang 2021-08-19 9:38
 */
public class MatrixRuntimeException extends RuntimeException {
    private final ExceptionLevel exceptionLevel = ExceptionLevel.OFF;
    private final Message exceptionMessage;

    public MatrixRuntimeException() {
        this.exceptionMessage = null;
    }

    public MatrixRuntimeException(Throwable cause, String message, Object... args) {
        super(cause);
        this.exceptionMessage = ValidationUtil.INSTANCE.isI18n(message) ? I18nMessage.of(message, args) : Message.of(message, args);
    }

    public MatrixRuntimeException(String message, Object... args) {
        this.exceptionMessage = ValidationUtil.INSTANCE.isI18n(message) ? I18nMessage.of(message, args) : Message.of(message, args);
    }

    public MatrixRuntimeException(Throwable cause) {
        super(cause);
        this.exceptionMessage = null;
    }

    public ExceptionLevel getExceptionLevel() {
        return exceptionLevel;
    }

    public Message getExceptionMessage() {
        return exceptionMessage;
    }

    @Override
    public String getMessage() {
        return null == this.exceptionMessage ? null : this.exceptionMessage.getMessage();
    }
}
