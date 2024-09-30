package wang.liangchen.matrix.framework.web.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.exception.MatrixRuntimeException;
import wang.liangchen.matrix.framework.commons.runtime.ReturnWrapper;
import wang.liangchen.matrix.framework.web.context.WebContext;

public final class JsonResponse<T> extends ReturnWrapper<T> {
    private final static Logger logger = LoggerFactory.getLogger(JsonResponse.class);
    private final static String DEFAULT_MESSAGE_I18N = "{system.error}";
    /**
     * 前端传递的requestId,原样返回。
     * 用于标识同一个请求
     */
    private final String requestId = WebContext.INSTANCE.getRequestId();
    private String debug;
    private ExceptionLevel level = ExceptionLevel.OFF;


    private JsonResponse(boolean success, T payload, String message, Object... args) {
        super(success, payload, message, args);
    }

    public static <T> JsonResponse<T> of(ReturnWrapper<T> returnWrapper) {
        JsonResponse<T> jsonResponse = new JsonResponse<>(returnWrapper.isSuccess(), returnWrapper.getPayload(), returnWrapper.getMessage());
        jsonResponse.withCode(returnWrapper.getCode());
        return jsonResponse;
    }

    public static <T> JsonResponse<T> success(T payload, String message, Object... args) {
        return new JsonResponse<>(true, payload, message, args);
    }

    public static <T> JsonResponse<T> success(T payload) {
        return new JsonResponse<>(true, payload, null);
    }

    public static JsonResponse<?> success(String message, Object... args) {
        return new JsonResponse<>(true, null, message, args);
    }

    public static JsonResponse<?> success() {
        return new JsonResponse<>(true, null, null);
    }

    public static <T> JsonResponse<T> failure(T payload, String message, Object... args) {
        return new JsonResponse<>(false, payload, message, args);
    }

    public static <T> JsonResponse<T> failure(T payload) {
        return new JsonResponse<>(false, payload, null);
    }

    public static JsonResponse<?> failure(String message, Object... args) {
        return new JsonResponse<>(false, null, message, args);
    }

    public static JsonResponse<?> failure() {
        return new JsonResponse<>(false, null, null);
    }

    public static JsonResponse<?> throwable(Throwable throwable) {
        JsonResponse<?> jsonResponse = failure(throwable.getMessage());
        jsonResponse.debug = throwable.getStackTrace()[0].toString();
        if (throwable instanceof MatrixRuntimeException matrixRuntimeException) {
            jsonResponse.level = matrixRuntimeException.getLevel();
            jsonResponse.withCode(matrixRuntimeException.getCode());
        }
        return jsonResponse;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getDebug() {
        return debug;
    }

    public ExceptionLevel getLevel() {
        return level;
    }
}
