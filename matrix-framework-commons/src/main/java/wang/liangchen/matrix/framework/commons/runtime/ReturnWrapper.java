package wang.liangchen.matrix.framework.commons.runtime;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author Liangchen.Wang 2022-10-16 10:06
 */
public class ReturnWrapper<T> implements Serializable {
    private final boolean success;
    private final T payload;
    /**
     * 消息代码/标识
     */
    private String code;
    /**
     * 消息内容
     */
    private String message;
    /**
     * i18n的message key,用于国际化
     */
    private String i18n;
    private Locale locale;

    protected ReturnWrapper(boolean success, T payload) {
        this.success = success;
        this.payload = payload;
    }

    public ReturnWrapper<T> withCode(String code) {
        this.code = code;
        return this;
    }

    public ReturnWrapper<T> withMessage(String message) {
        this.message = message;
        return this;
    }

    public ReturnWrapper<T> withI18n(Locale locale, String i18n) {
        this.locale = locale;
        this.i18n = i18n;
        return this;
    }

    public static <T> ReturnWrapper<T> success(T payload) {
        return new ReturnWrapper<T>(true, payload);
    }

    public static <T> ReturnWrapper<T> success() {
        return new ReturnWrapper<T>(true, null);
    }

    public static <T> ReturnWrapper<T> failure(T payload) {
        return new ReturnWrapper<T>(false, payload);
    }

    public static <T> ReturnWrapper<T> failure() {
        return new ReturnWrapper<T>(false, null);
    }
}
