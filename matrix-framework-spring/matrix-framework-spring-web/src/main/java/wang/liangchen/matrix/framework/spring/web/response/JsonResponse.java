package wang.liangchen.matrix.framework.spring.web.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.exception.MatrixRuntimeException;
import wang.liangchen.matrix.framework.commons.runtime.I18nMessage;
import wang.liangchen.matrix.framework.commons.runtime.LocaleTimeZoneContext;
import wang.liangchen.matrix.framework.commons.runtime.ReturnValue;
import wang.liangchen.matrix.framework.spring.web.context.WebContext;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class JsonResponse<T> extends ReturnValue<T> {
    private final static Logger logger = LoggerFactory.getLogger(JsonResponse.class);
    private final static Map<Locale, String> SYSTEM_ERRORS = new HashMap<>() {{
        put(Locale.CHINA, "系统错误,请联系管理员!");
        put(Locale.US, "System error, please contact the administrator!");
        put(Locale.UK, "System error, please contact the administrator!");
        put(Locale.JAPAN, "システムエラー、管理者に連絡してください！");
    }};
    /**
     * 前端传递的requestId,原样返回。
     * 用于标识同一个请求
     */
    private final String requestId = WebContext.INSTANCE.getRequestId();
    private int httpStatus = 200;
    private String debug;
    private ExceptionLevel level = ExceptionLevel.OFF;

    private JsonResponse(boolean success, T payload, String message, Object... args) {
        super(success, payload, message, args);
    }

    private JsonResponse(boolean success, T payload, I18nMessage i18nMessage) {
        super(success, payload, i18nMessage);
    }

    private JsonResponse(boolean success, I18nMessage i18nMessage) {
        super(success, i18nMessage);
    }

    private JsonResponse(ReturnValue<T> returnValue) {
        super(returnValue);
    }

    public static <T> JsonResponse<T> of(boolean success, I18nMessage i18nMessage) {
        return new JsonResponse<>(success, i18nMessage);
    }

    public static <T> JsonResponse<T> of(ReturnValue<T> returnValue) {
        return new JsonResponse<>(returnValue);
    }

    public static <T> JsonResponse<T> success(T payload, String message, Object... args) {
        return new JsonResponse<>(true, payload, message, args);
    }

    public static <T> JsonResponse<T> success(T payload) {
        return new JsonResponse<>(true, payload, null);
    }

    public static <T> JsonResponse<T> success(String message, Object... args) {
        return new JsonResponse<>(true, null, message, args);
    }

    public static <T> JsonResponse<T> success() {
        return new JsonResponse<>(true, null, null);
    }

    public static <T> JsonResponse<T> failure(T payload, String message, Object... args) {
        return new JsonResponse<>(false, payload, message, args);
    }

    public static <T> JsonResponse<T> failure(T payload) {
        return new JsonResponse<>(false, payload, null);
    }

    public static <T> JsonResponse<T> failure(String message, Object... args) {
        return new JsonResponse<>(false, null, message, args);
    }

    public static <T> JsonResponse<T> failure() {
        return new JsonResponse<>(false, null, null);
    }

    public static <T> JsonResponse<T> failure(Throwable throwable) {
        logger.error("JsonResponse.failure", throwable);
        if (throwable instanceof MatrixRuntimeException matrixRuntimeException) {
            JsonResponse<T> jsonResponse = new JsonResponse<>(false, matrixRuntimeException.getI18nMessage());
            jsonResponse.withLevel(matrixRuntimeException.getLevel());
            return jsonResponse.withDebug(throwable);
        }
        JsonResponse<T> jsonResponse = new JsonResponse<>(false, null, SYSTEM_ERRORS.get(LocaleTimeZoneContext.INSTANCE.getLocale()));
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

    public JsonResponse<T> withHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }


    private String getStackTrace(Throwable throwable) {
        StringBuilder stringBuilder = new StringBuilder();
        String message = throwable.getMessage();
        if (null != message) {
            stringBuilder.append(message).append(Symbol.LINE_SEPARATOR.getSymbol()).append(Symbol.LINE_SEPARATOR.getSymbol());
        }
        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            stringBuilder.append(stackTraceElement.toString()).append(Symbol.LINE_SEPARATOR.getSymbol());
        }
        return stringBuilder.toString();
    }

    public String getRequestId() {
        return requestId;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getDebug() {
        return debug;
    }

    public ExceptionLevel getLevel() {
        return level;
    }
}
