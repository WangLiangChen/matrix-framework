package wang.liangchen.matrix.framework.commons.runtime;

import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.util.Locale;


public class MessageWrapper {
    private final String message;
    private String code;
    private String i18n;
    private final Locale locale;

    public MessageWrapper(String message, Object... args) {
        this.message = ValidationUtil.INSTANCE.resolveMessage(message, args);
        ValidationUtil.INSTANCE.resolveI18n(message).ifPresent(i18n -> this.i18n = i18n);
        this.locale = LocaleTimeZoneContext.INSTANCE.getLocale();
    }

    public void withCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public String getI18n() {
        return i18n;
    }

    public Locale getLocale() {
        return locale;
    }
}
