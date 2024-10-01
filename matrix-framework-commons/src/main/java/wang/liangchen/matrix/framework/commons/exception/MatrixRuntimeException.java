package wang.liangchen.matrix.framework.commons.exception;

import wang.liangchen.matrix.framework.commons.runtime.LocaleTimeZoneContext;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.util.Locale;

/**
 * @author Liangchen.Wang 2021-08-19 9:38
 */
public class MatrixRuntimeException extends RuntimeException {
    /**
     * 异常代码
     */
    private String code;
    private String i18n;
    private Locale locale;
    private final ExceptionLevel level = ExceptionLevel.OFF;


    public MatrixRuntimeException() {
        super();
    }

    public MatrixRuntimeException(String message, Object... args) {
        super(ValidationUtil.INSTANCE.resolveMessage(message, args));
        ValidationUtil.INSTANCE.resolveI18n(message).ifPresent(i18n -> this.i18n = i18n);
        this.locale = LocaleTimeZoneContext.INSTANCE.getLocale();
    }

    public MatrixRuntimeException(Throwable cause, String message, Object... args) {
        super(ValidationUtil.INSTANCE.resolveMessage(message, args), cause);
        ValidationUtil.INSTANCE.resolveI18n(message).ifPresent(i18n -> this.i18n = i18n);
        this.locale = LocaleTimeZoneContext.INSTANCE.getLocale();
    }

    public MatrixRuntimeException(Throwable cause) {
        super(cause);
    }

    public MatrixRuntimeException withCode(String code) {
        this.code = code;
        return this;
    }

    public String getCode() {
        return this.code;
    }

    public String getI18n() {
        return i18n;
    }

    public Locale getLocale() {
        return locale;
    }

    public ExceptionLevel getLevel() {
        return level;
    }
}
