package wang.liangchen.matrix.framework.commons.runtime;

import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.util.Locale;


public class MessageWrapper {
    private final boolean success;
    private String code;
    private final String message;
    private final String i18n;
    private final Locale locale;

    // 只允许其子类修改success
    protected MessageWrapper(boolean success, String message, Object... args) {
        this.locale = LocaleTimeZoneContext.INSTANCE.getLocale();
        this.success = success;
        if (null == message) {
            this.message = this.i18n = null;
            return;
        }
        this.i18n = ValidationUtil.INSTANCE.resolveI18n(message);
        this.message = ValidationUtil.INSTANCE.resolveMessage(message, args);
    }

    protected MessageWrapper(MessageWrapper messageWrapper) {
        this.success = messageWrapper.success;
        this.locale = messageWrapper.locale;
        this.i18n = messageWrapper.i18n;
        this.message = messageWrapper.message;
    }

    public static MessageWrapper of(String message, Object... args) {
        return new MessageWrapper(false, message, args);
    }

    public MessageWrapper withCode(String code) {
        this.code = code;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getCode() {
        return code;
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
