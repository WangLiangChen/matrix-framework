package wang.liangchen.matrix.framework.web.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.exception.MatrixRuntimeException;
import wang.liangchen.matrix.framework.commons.runtime.MessageWrapper;
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

    private JsonResponse(T payload, boolean success, String message, Object... args) {
        super(payload, success, message, args);
    }

    private JsonResponse(T payload, MessageWrapper messageWrapper) {
        super(payload, messageWrapper);
    }

    private JsonResponse(MessageWrapper messageWrapper) {
        super(messageWrapper);
    }

    private JsonResponse(ReturnWrapper<T> returnWrapper) {
        super(returnWrapper);
    }

    public static <T> JsonResponse<T> of(MessageWrapper messageWrapper) {
        return new JsonResponse<>(messageWrapper);
    }

    public static <T> JsonResponse<T> of(ReturnWrapper<T> returnWrapper) {
        return new JsonResponse<>(returnWrapper);
    }

    public static <T> JsonResponse<T> success(T payload, String message, Object... args) {
        return new JsonResponse<>(payload, true, message, args);
    }

    public static <T> JsonResponse<T> success(T payload) {
        return new JsonResponse<>(payload, true, null);
    }

    public static <T> JsonResponse<T> success(String message, Object... args) {
        return new JsonResponse<>(null, true, message, args);
    }

    public static <T> JsonResponse<T> success() {
        return new JsonResponse<>(null, true, null);
    }

    public static <T> JsonResponse<T> failure(T payload, String message, Object... args) {
        return new JsonResponse<>(payload, false, message, args);
    }

    public static <T> JsonResponse<T> failure(T payload) {
        return new JsonResponse<>(payload, false, null);
    }

    public static <T> JsonResponse<T> failure(String message, Object... args) {
        return new JsonResponse<>(null, false, message, args);
    }

    public static <T> JsonResponse<T> failure() {
        return new JsonResponse<>(null, false, null);
    }

    public static <T> JsonResponse<T> failure(Throwable throwable) {
        logger.error("JsonResponse.failure", throwable);
        if (throwable instanceof MatrixRuntimeException matrixRuntimeException) {
            JsonResponse<T> jsonResponse = new JsonResponse<>(matrixRuntimeException.getMessageWrapper());
            jsonResponse.withLevel(matrixRuntimeException.getLevel());
            return jsonResponse.withDebug(throwable);
        }
        JsonResponse<T> jsonResponse = new JsonResponse<>(null, false, throwable.getMessage());
        return jsonResponse.withDebug(throwable);
    }

    public JsonResponse<T> withLevel(ExceptionLevel level) {
        this.level = level;
        return this;
    }

    public JsonResponse<T> withDebug(Throwable throwable) {
        this.debug = getStackTrace(throwable);
        return this;
    }

    public JsonResponse<T> withCode(String code) {
        super.withCode(code);
        return this;
    }

    private String getStackTrace(Throwable throwable) {
        StringBuilder stringBuilder = new StringBuilder();
        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            stringBuilder.append(stackTraceElement.toString()).append("\n");
        }
        return stringBuilder.toString();
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
