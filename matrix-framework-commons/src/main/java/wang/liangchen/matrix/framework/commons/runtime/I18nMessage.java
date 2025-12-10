package wang.liangchen.matrix.framework.commons.runtime;

import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.util.Locale;

public class I18nMessage extends Message {
    private final String i18n;
    private final Locale locale = LocaleTimeZoneContext.INSTANCE.getLocale();

    protected I18nMessage(String i18n, Object... args) {
        super(ValidationUtil.INSTANCE.resolveI18n(i18n, args));
        if (ValidationUtil.INSTANCE.isI18n(i18n)) {
            this.i18n = i18n;
            return;
        }
        this.i18n = null;
    }

    public static I18nMessage of(String i18n, Object... args) {
        return new I18nMessage(i18n, args);
    }

    @Override
    public I18nMessage withCode(String code) {
        return (I18nMessage) super.withCode(code);
    }

    public String getI18n() {
        return this.i18n;
    }

    public Locale getLocale() {
        return this.locale;
    }

}
