package wang.liangchen.matrix.framework.commons.runtime;

import wang.liangchen.matrix.framework.commons.StringUtil;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author Liangchen.Wang 2022-10-16 10:50
 */
public class MessageWrapper implements Serializable {
    /**
     * 传给前端的i18n的messageKey,用于前端国际化
     */
    private final String i18n;
    private final Locale locale;
    /**
     * 消息代码/消息标识
     */
    private final String code;
    /**
     * 消息内容
     */
    private final String message;

    protected MessageWrapper(String i18n, Locale locale, String code, String message) {
        this.i18n = i18n;
        this.locale = locale;
        this.code = code;
        this.message = message;
    }

    public static MessageWrapper of(String code, String message, Object... args) {
        return new MessageWrapper(null, null, code, StringUtil.INSTANCE.format(message, args));
    }

    public static MessageWrapper of(String message, Object... args) {
        return of(null, message, args);
    }

    public String getI18n() {
        return i18n;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
