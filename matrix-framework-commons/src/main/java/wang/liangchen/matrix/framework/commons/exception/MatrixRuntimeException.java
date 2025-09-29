package wang.liangchen.matrix.framework.commons.exception;

import wang.liangchen.matrix.framework.commons.runtime.I18nMessage;

/**
 * @author Liangchen.Wang 2021-08-19 9:38
 */
public class MatrixRuntimeException extends RuntimeException {
    private final ExceptionLevel level = ExceptionLevel.OFF;
    private final I18nMessage i18nMessage;

    public MatrixRuntimeException() {
        this.i18nMessage = null;
    }

    public MatrixRuntimeException(String message, Object... args) {
        this.i18nMessage = I18nMessage.of(message, args);
    }

    public MatrixRuntimeException(Throwable throwable, String message, Object... args) {
        super(throwable);
        this.i18nMessage = I18nMessage.of(message, args);
    }

    public MatrixRuntimeException(Throwable throwable) {
        super(throwable);
        this.i18nMessage = null;
    }

    @Override
    public String getMessage() {
        if (null == i18nMessage) {
            return super.getMessage();
        }
        return i18nMessage.getMessage();
    }

    public ExceptionLevel getLevel() {
        return level;
    }

    public I18nMessage getI18nMessage() {
        return i18nMessage;
    }
}
