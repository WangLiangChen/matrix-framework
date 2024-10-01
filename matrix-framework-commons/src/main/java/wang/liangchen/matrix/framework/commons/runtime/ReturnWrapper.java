package wang.liangchen.matrix.framework.commons.runtime;

import java.io.Serializable;

/**
 * @author Liangchen.Wang 2022-10-16 10:06
 */
public class ReturnWrapper<T> extends MessageWrapper implements Serializable {
    private final boolean success;
    private final T payload;

    protected ReturnWrapper(ReturnWrapper<T> returnWrapper) {
        super(returnWrapper);
        this.success = returnWrapper.isSuccess();
        this.payload = returnWrapper.getPayload();
    }

    protected ReturnWrapper(boolean success, T payload, MessageWrapper messageWrapper) {
        super(messageWrapper);
        this.success = success;
        this.payload = payload;
    }

    public static <T> ReturnWrapper<T> of(boolean success, T payload, MessageWrapper messageWrapper) {
        return new ReturnWrapper<>(success, payload, messageWrapper);
    }

    public static <T> ReturnWrapper<T> success(T payload, MessageWrapper messageWrapper) {
        return of(true, payload, messageWrapper);
    }

    public static <T> ReturnWrapper<T> success(T payload) {
        return of(true, payload, null);
    }

    public static <T> ReturnWrapper<T> success(MessageWrapper messageWrapper) {
        return of(true, null, messageWrapper);
    }

    public static <T> ReturnWrapper<T> success() {
        return of(true, null, null);
    }

    public static <T> ReturnWrapper<T> failure(T payload, MessageWrapper messageWrapper) {
        return of(false, payload, messageWrapper);
    }

    public static <T> ReturnWrapper<T> failure(T payload) {
        return of(false, payload, null);
    }

    public static <T> ReturnWrapper<T> failure(MessageWrapper messageWrapper) {
        return of(false, null, messageWrapper);
    }

    public static <T> ReturnWrapper<T> failure() {
        return of(false, null, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getPayload() {
        return payload;
    }
}
