package wang.liangchen.matrix.framework.commons.runtime;

import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;

/**
 * @author Liangchen.Wang
 */
public class ExceptionMessage extends Message {
    private final ExceptionLevel exceptionLevel;
    private String debug;

    protected ExceptionMessage(ExceptionLevel exceptionLevel, String message, Object... args) {
        super(message, args);
        this.exceptionLevel = exceptionLevel;
    }

    public static ExceptionMessage of(ExceptionLevel exceptionLevel, String message, Object... args) {
        return new ExceptionMessage(exceptionLevel, message, args);
    }

    public ExceptionMessage withDebug(String debug) {
        this.debug = debug;
        return this;
    }

    @Override
    public ExceptionMessage withCode(String code) {
        return (ExceptionMessage) super.withCode(code);
    }

    public ExceptionLevel getExceptionLevel() {
        return exceptionLevel;
    }

    public String getDebug() {
        return debug;
    }

}
