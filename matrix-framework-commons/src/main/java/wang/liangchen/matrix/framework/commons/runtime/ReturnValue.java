package wang.liangchen.matrix.framework.commons.runtime;

import java.io.Serializable;

/**
 * @author Liangchen.Wang 2022-10-16 10:06
 */
public class ReturnValue<T> extends I18nMessage implements Serializable {
    private final boolean success;
    private final T payload;

    protected ReturnValue(boolean success, T payload, String message, Object... args) {
        super(message, args);
        this.success = success;
        this.payload = payload;
    }

    protected ReturnValue(boolean success, T payload, I18nMessage i18nMessage) {
        super(i18nMessage);
        this.success = success;
        this.payload = payload;
    }

    protected ReturnValue(boolean success, I18nMessage i18nMessage) {
        super(i18nMessage);
        this.success = success;
        this.payload = null;
    }

    protected ReturnValue(ReturnValue<T> returnValue) {
        super(returnValue);
        this.success = returnValue.success;
        this.payload = returnValue.payload;
    }

    public static <T> ReturnValue<T> success(T payload, String message, Object... args) {
        return new ReturnValue<>(true, payload, message, args);
    }

    public static <T> ReturnValue<T> success(T payload) {
        return new ReturnValue<>(true, payload, null);
    }

    public static <T> ReturnValue<T> success(String message, Object... args) {
        return new ReturnValue<>(true, null, message, args);
    }

    public static <T> ReturnValue<T> success() {
        return new ReturnValue<>(true, null, null);
    }

    public static <T> ReturnValue<T> failure(T payload, String message, Object... args) {
        return new ReturnValue<>(false, payload, message, args);
    }

    public static <T> ReturnValue<T> failure(T payload) {
        return new ReturnValue<>(false, payload, null);
    }

    public static <T> ReturnValue<T> failure(String message, Object... args) {
        return new ReturnValue<>(false, null, message, args);
    }

    public static <T> ReturnValue<T> failure() {
        return new ReturnValue<>(false, null, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getPayload() {
        return payload;
    }
}
