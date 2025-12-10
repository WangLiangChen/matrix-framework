package wang.liangchen.matrix.framework.commons.exception;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.json.jackson.JacksonUtil;
import wang.liangchen.matrix.framework.commons.runtime.ExceptionMessage;
import wang.liangchen.matrix.framework.commons.runtime.I18nExceptionMessage;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

public abstract class AbstractMatrixRuntimeException extends RuntimeException {
    private final ExceptionMessage exceptionMessage;

    public AbstractMatrixRuntimeException() {
        this.exceptionMessage = null;
    }

    public AbstractMatrixRuntimeException(String message, Object... args) {
        this.exceptionMessage = createExceptionMessage(message, args);
    }

    public AbstractMatrixRuntimeException(Throwable cause, String message, Object... args) {
        super(cause);
        this.exceptionMessage = createExceptionMessage(message, args);
    }

    public AbstractMatrixRuntimeException(Throwable cause) {
        super(cause);
        this.exceptionMessage = createExceptionMessage(cause.getMessage());
    }

    abstract ExceptionLevel exceptionLevel();

    private ExceptionMessage createExceptionMessage(String message, Object... args) {
        return ValidationUtil.INSTANCE.isI18n(message) ? I18nExceptionMessage.of(exceptionLevel(), message, args) : ExceptionMessage.of(exceptionLevel(), message, args);
    }

    public ExceptionMessage getExceptionMessage() {
        return exceptionMessage;
    }

    public void withDebug() {
        if (null == this.exceptionMessage) {
            return;
        }
        this.exceptionMessage.withDebug(getStackTrace(this));
    }

    @Override
    public String getMessage() {
        return JacksonUtil.INSTANCE.writeValueAsString(this.exceptionMessage);
    }

    private String getStackTrace(Throwable throwable) {
        StringBuilder stringBuilder = new StringBuilder();
        String message = throwable.getMessage();
        if (null != message) {
            stringBuilder.append(message).append(Symbol.LINE_SEPARATOR.getSymbol()).append(Symbol.LINE_SEPARATOR.getSymbol());
        }
        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            stringBuilder.append(stackTraceElement.toString()).append(Symbol.LINE_SEPARATOR.getSymbol());
        }
        return stringBuilder.toString();
    }
}
