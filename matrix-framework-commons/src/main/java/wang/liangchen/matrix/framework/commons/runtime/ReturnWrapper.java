package wang.liangchen.matrix.framework.commons.runtime;

import java.io.Serializable;

/**
 * @author Liangchen.Wang 2022-10-16 10:06
 */
public class ReturnWrapper<T> implements Serializable {
    private final boolean success;
    private final T object;
    private MessageWrapper messageWrapper;

    private ReturnWrapper(boolean success, T object) {
        this.success = success;
        this.object = object;
    }

    public static <T> ReturnWrapper<T> success(T object) {
        return new ReturnWrapper<>(true, object);
    }

    public static <T> ReturnWrapper<T> failure(Class<T> objectClass) {
        return new ReturnWrapper<>(false, null);
    }

    public ReturnWrapper<T> message(MessageWrapper messageWrapper) {
        this.messageWrapper = messageWrapper;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getObject() {
        return object;
    }

    public MessageWrapper getMessageWrapper() {
        return messageWrapper;
    }
}
