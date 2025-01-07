package wang.liangchen.matrix.framework.commons.runtime;

import java.io.Serializable;

/**
 * @author Liangchen.Wang 2022-10-16 10:06
 */
public class ReturnWrapper<T> extends MessageWrapper implements Serializable {
    private final T payload;

    protected ReturnWrapper(T payload, boolean success, String message, Object... args) {
        super(success, message, args);
        this.payload = payload;
    }

    protected ReturnWrapper(T payload, MessageWrapper messageWrapper) {
        super(messageWrapper);
        this.payload = payload;
    }

    protected ReturnWrapper(MessageWrapper messageWrapper) {
        super(messageWrapper);
        this.payload = null;
    }

    protected ReturnWrapper(ReturnWrapper<T> returnWrapper) {
        super(returnWrapper);
        this.payload = returnWrapper.payload;
    }

    public static <T> ReturnWrapper<T> success(T payload, String message, Object... args) {
        return new ReturnWrapper<>(payload, true, message, args);
    }

    public static <T> ReturnWrapper<T> success(T payload) {
        return new ReturnWrapper<>(payload, true, null);
    }

    public static <T> ReturnWrapper<T> success(String message, Object... args) {
        return new ReturnWrapper<>(null, true, message, args);
    }

    public static <T> ReturnWrapper<T> success() {
        return new ReturnWrapper<>(null, true, null);
    }

    public static <T> ReturnWrapper<T> failure(T payload, String message, Object... args) {
        return new ReturnWrapper<>(payload, false, message, args);
    }

    public static <T> ReturnWrapper<T> failure(T payload) {
        return new ReturnWrapper<>(payload, false, null);
    }

    public static <T> ReturnWrapper<T> failure(String message, Object... args) {
        return new ReturnWrapper<>(null, false, message, args);
    }

    public static <T> ReturnWrapper<T> failure() {
        return new ReturnWrapper<>(null, false, null);
    }

    public ReturnWrapper<T> withCode(String code) {
        super.withCode(code);
        return this;
    }

    public T getPayload() {
        return payload;
    }
}
