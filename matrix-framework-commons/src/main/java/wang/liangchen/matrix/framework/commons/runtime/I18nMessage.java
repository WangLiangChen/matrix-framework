package wang.liangchen.matrix.framework.commons.runtime;

import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.util.Locale;


public class I18nMessage {
    private String code = "0";
    private final String message;
    private final String i18n;
    private final Locale locale;

    protected I18nMessage(String message, Object... args) {
        this.locale = LocaleTimeZoneContext.INSTANCE.getLocale();
        if (null == message) {
            this.message = this.i18n = null;
            return;
        }
        this.i18n = ValidationUtil.INSTANCE.resolveI18nKey(message);
        this.message = ValidationUtil.INSTANCE.resolveMessage(message, args);
    }

    protected I18nMessage(I18nMessage i18nMessage) {
        this.code = i18nMessage.code;
        this.locale = i18nMessage.locale;
        this.i18n = i18nMessage.i18n;
        this.message = i18nMessage.message;
    }

    public static I18nMessage of(String message, Object... args) {
        return new I18nMessage(message, args);
    }

    public void withCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public String getI18n() {
        return i18n;
    }

    public Locale getLocale() {
        return locale;
    }
}
