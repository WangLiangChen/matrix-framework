package wang.liangchen.matrix.framework.commons.runtime;

import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.util.Locale;


public class MessageWrapper {
    private final String message;
    private final String i18n;
    private final Locale locale;

    protected MessageWrapper(String message, Object... args) {
        this.locale = LocaleTimeZoneContext.INSTANCE.getLocale();
        if (null == message) {
            this.message = this.i18n = null;
            return;
        }
        this.i18n = ValidationUtil.INSTANCE.resolveI18nKey(message);
        this.message = ValidationUtil.INSTANCE.resolveMessage(message, args);
    }

    protected MessageWrapper(MessageWrapper messageWrapper) {
        this.locale = messageWrapper.locale;
        this.i18n = messageWrapper.i18n;
        this.message = messageWrapper.message;
    }

    public static MessageWrapper of(String message, Object... args) {
        return new MessageWrapper(message, args);
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
