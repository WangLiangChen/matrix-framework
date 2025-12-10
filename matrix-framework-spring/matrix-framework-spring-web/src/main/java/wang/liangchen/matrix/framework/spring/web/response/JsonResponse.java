package wang.liangchen.matrix.framework.spring.web.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import wang.liangchen.matrix.framework.commons.exception.MatrixRuntimeException;
import wang.liangchen.matrix.framework.commons.runtime.LocaleTimeZoneContext;
import wang.liangchen.matrix.framework.commons.runtime.Message;
import wang.liangchen.matrix.framework.spring.web.context.WebContext;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class JsonResponse<T> {
    private final static Logger logger = LoggerFactory.getLogger(JsonResponse.class);
    private final static Map<Locale, String> SYSTEM_ERRORS = new HashMap<>() {{
        put(Locale.SIMPLIFIED_CHINESE, "系统错误,请联系管理员!");
        put(Locale.ENGLISH, "System error, please contact the administrator!");
        put(Locale.JAPANESE, "システムエラー、管理者に連絡してください！");
    }};
    /**
     * 前端传递的requestId,原样返回。
     * 用于标识同一个请求
     */
    private final boolean success;
    private final T payload;
    private final Message message;
    private final String requestId = WebContext.INSTANCE.getRequestId();

    private JsonResponse(boolean success, T payload, Message message) {
        this.success = success;
        this.payload = payload;
        this.message = message;
    }

    private JsonResponse(boolean success, Message message) {
        this(success, null, message);
    }

    private JsonResponse(boolean success, T payload) {
        this(success, payload, null);
    }

    private JsonResponse(boolean success) {
        this(success, null, null);
    }

    public static <T> JsonResponse<T> failure(Throwable throwable) {
        logger.error("JsonResponse.Failure", throwable);
        if (throwable instanceof MatrixRuntimeException matrixRuntimeException) {
            matrixRuntimeException.withDebug();
            return new JsonResponse<>(false, matrixRuntimeException.getExceptionMessage());
        }
        if (throwable instanceof NoResourceFoundException || throwable instanceof NoHandlerFoundException) {
            return new JsonResponse<>(false, null, Message.of(throwable.getMessage()).withCode("404"));
        }
        return new JsonResponse<>(false, null, Message.of(SYSTEM_ERRORS.get(LocaleTimeZoneContext.INSTANCE.getLocale())));
    }

    public static <T> JsonResponse<T> failure(T payload, Message message) {
        return new JsonResponse<>(false, payload, message);
    }

    public static <T> JsonResponse<T> failure(T payload) {
        return new JsonResponse<>(false, payload);
    }

    public static <T> JsonResponse<T> failure(Message message) {
        return new JsonResponse<>(false, message);
    }

    public static <T> JsonResponse<T> failure() {
        return new JsonResponse<>(false);
    }

    public static <T> JsonResponse<T> success(T payload, Message message) {
        return new JsonResponse<>(true, payload, message);
    }

    public static <T> JsonResponse<T> success(T payload) {
        return new JsonResponse<>(true, payload);
    }

    public static <T> JsonResponse<T> success(Message message) {
        return new JsonResponse<>(true, message);
    }

    public static <T> JsonResponse<T> success() {
        return new JsonResponse<>(true);
    }


    public boolean isSuccess() {
        return success;
    }

    public T getPayload() {
        return payload;
    }

    public Message getMessage() {
        return message;
    }

    public String getRequestId() {
        return requestId;
    }
}
