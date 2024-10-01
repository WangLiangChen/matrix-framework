package wang.liangchen.matrix.framework.commons.runtime;

import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.util.Locale;


public class MessageWrapper {
    private final String message;
    private String code;
    private String i18n;
    private final Locale locale;

    protected MessageWrapper() {
        this.message = null;
        this.locale = LocaleTimeZoneContext.INSTANCE.getLocale();
    }

    protected MessageWrapper(MessageWrapper messageWrapper) {
        if (null == messageWrapper) {
            messageWrapper = MessageWrapper.of();
        }
        this.message = messageWrapper.getMessage();
        this.code = messageWrapper.getCode();
        this.i18n = messageWrapper.getI18n();
        this.locale = messageWrapper.getLocale();
    }

    protected MessageWrapper(String message, Object... args) {
        this.message = ValidationUtil.INSTANCE.resolveMessage(message, args);
        ValidationUtil.INSTANCE.resolveI18n(message).ifPresent(i18n -> this.i18n = i18n);
        this.locale = LocaleTimeZoneContext.INSTANCE.getLocale();
    }

    public static MessageWrapper of(String message, Object... args) {
        return new MessageWrapper(message, args);
    }

    public static MessageWrapper of(MessageWrapper messageWrapper) {
        return new MessageWrapper(messageWrapper);
    }

    public static MessageWrapper of() {
        return new MessageWrapper();
    }

    public MessageWrapper withCode(String code) {
        this.code = code;
        return this;
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
