package wang.liangchen.matrix.framework.commons.runtime;

import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.util.Locale;

/**
 * @author Liangchen.Wang
 */
public class I18nExceptionMessage extends ExceptionMessage {
    private final String i18n;
    private final Locale locale = LocaleTimeZoneContext.INSTANCE.getLocale();

    public I18nExceptionMessage(ExceptionLevel exceptionLevel, String i18n, Object... args) {
        super(exceptionLevel, ValidationUtil.INSTANCE.resolveI18n(i18n, args));
        if (ValidationUtil.INSTANCE.isI18n(i18n)) {
            this.i18n = i18n;
            return;
        }
        this.i18n = null;
    }

    public static I18nExceptionMessage of(ExceptionLevel exceptionLevel, String i18n, Object... args) {
        return new I18nExceptionMessage(exceptionLevel, i18n, args);
    }

    @Override
    public I18nExceptionMessage withDebug(String debug) {
        return (I18nExceptionMessage) super.withDebug(debug);
    }

    @Override
    public I18nExceptionMessage withCode(String code) {
        return (I18nExceptionMessage) super.withCode(code);
    }

    public String getI18n() {
        return this.i18n;
    }

    public Locale getLocale() {
        return this.locale;
    }
}
