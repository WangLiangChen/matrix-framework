package wang.liangchen.matrix.framework.commons.runtime;

import java.io.Serializable;

/**
 * @author Liangchen.Wang 2022-10-16 10:06
 */
public class ReturnWrapper<T> extends MessageWrapper implements Serializable {
    private final boolean success;
    private final T payload;

    public ReturnWrapper(boolean success, T payload, String message, Object... args) {
        super(message, args);
        this.success = success;
        this.payload = payload;
    }

    public static <T> ReturnWrapper<T> success(T payload, String message, Object... args) {
        return new ReturnWrapper<>(true, payload, message, args);
    }

    public static <T> ReturnWrapper<T> success(T payload) {
        return new ReturnWrapper<>(true, payload, null);
    }

    public static ReturnWrapper<?> success(String message, Object... args) {
        return new ReturnWrapper<>(true, null, message, args);
    }

    public static ReturnWrapper<?> success() {
        return new ReturnWrapper<>(true, null, null);
    }


    public static <T> ReturnWrapper<T> failure(T payload, String message, Object... args) {
        return new ReturnWrapper<>(false, payload, message, args);
    }

    public static <T> ReturnWrapper<T> failure(T payload) {
        return new ReturnWrapper<>(false, payload, null);
    }

    public static ReturnWrapper<?> failure(String message, Object... args) {
        return new ReturnWrapper<>(false, null, message, args);
    }

    public static ReturnWrapper<?> failure() {
        return new ReturnWrapper<>(false, null, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getPayload() {
        return payload;
    }
}
