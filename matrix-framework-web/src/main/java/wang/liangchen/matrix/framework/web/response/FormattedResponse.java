package wang.liangchen.matrix.framework.web.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.*;
import wang.liangchen.matrix.framework.commons.runtime.MessageWrapper;
import wang.liangchen.matrix.framework.commons.runtime.ReturnWrapper;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.springboot.jackson.DefaultObjectMapper;
import wang.liangchen.matrix.framework.web.context.WebContext;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author Liangchen.Wang
 */
public final class FormattedResponse<T> implements Serializable {
    private final static Logger logger = LoggerFactory.getLogger(FormattedResponse.class);
    private final static String DEFAULT_MESSAGE = "System error. Please try again later or contact your system administrator.";
    private final static String DEFAULT_MESSAGE_I18N = "SystemError";
    /**
     * 业务成功失败标识
     */
    private boolean success;
    /**
     * 提示类型/级别
     */
    private ExceptionLevel level = ExceptionLevel.OFF;
    /**
     * 业务/错误代码
     */
    private String code;
    /**
     * 提示信息
     */
    private String message;
    /**
     * 提示信息国际化Key
     */
    private String i18n;
    /**
     * 语言
     */
    private Locale locale;
    /**
     * 业务/错误数据
     */
    private Object payload = new HashMap<String, Object>();
    /**
     * 前端传递的requestId,原样返回
     * 用于标识同一个请求,或作为traceId向后传递
     */
    private String requestId;
    /**
     * 用于调试的异常堆栈信息
     */
    private String debug;


    public static <T> FormattedResponse<T> newInstance() {
        FormattedResponse<T> formattedResponse = ClassUtil.INSTANCE.instantiate(FormattedResponse.class.getName());
        formattedResponse.requestId = WebContext.INSTANCE.getRequestId();
        return formattedResponse;
    }

    public static <T> FormattedResponse<T> of(ReturnWrapper<T> returnWrapper) {
        FormattedResponse<T> formattedResponse = newInstance();
        formattedResponse.success = returnWrapper.isSuccess();
        formattedResponse.payload = returnWrapper.getObject();
        MessageWrapper messageWrapper = returnWrapper.getMessageWrapper();
        if (null != messageWrapper) {
            formattedResponse.locale = messageWrapper.getLocale();
            formattedResponse.i18n = messageWrapper.getI18n();
            formattedResponse.code = messageWrapper.getCode();
            formattedResponse.message = messageWrapper.getMessage();
        }
        return formattedResponse;
    }

    public static <T> FormattedResponse<T> success() {
        FormattedResponse<T> formattedResponse = newInstance();
        formattedResponse.success = true;
        return formattedResponse;
    }

    public static <T> FormattedResponse<T> failure() {
        FormattedResponse<T> formattedResponse = newInstance();
        formattedResponse.success = false;
        return formattedResponse;
    }

    public FormattedResponse<T> level(ExceptionLevel level) {
        this.level = level;
        return this;
    }

    public FormattedResponse<T> code(String code) {
        this.code = code;
        return this;
    }

    public FormattedResponse<T> message(String message, Object... args) {
        this.message = StringUtil.INSTANCE.format(message, args);
        return this;
    }

    public FormattedResponse<T> i18n(String i18n) {
        this.i18n = i18n;
        return this;
    }

    public FormattedResponse<T> locale(Locale Locale) {
        this.locale = locale;
        return this;
    }

    public FormattedResponse<T> payload(Object payload) {
        this.payload = payload;
        return this;
    }

    public FormattedResponse<T> debug(String debug) {
        this.debug = debug;
        return this;
    }

    public static <T> FormattedResponse<T> exception(Throwable throwable) {
        throwable = resolveMatrixRuntimeException(throwable);
        FormattedResponse<T> failure = failure();
        if (throwable instanceof MatrixRuntimeException) {
            MatrixRuntimeException ex = (MatrixRuntimeException) throwable;
            failure.code = ex.getCode();
            failure.locale = ex.getLocale();
            failure.code = ex.getCode();
            failure.message = ex.getMessage();
            failure.i18n = ex.getI18n();
        }
        if (throwable instanceof MatrixInfoException) {
            logger.debug(throwable.getMessage(), throwable);
            failure.level = ExceptionLevel.INFO;
            return failure;
        }
        // populate debug stacktrace
        failure.debug(stackTraceString(throwable, new StringBuilder()));
        if (throwable instanceof MatrixWarnException) {
            logger.info(throwable.getMessage(), throwable);
            failure.level = ExceptionLevel.WARN;
            return failure;
        }
        // MatrixErrorException or other Exception
        logger.error(throwable.getMessage(), throwable);
        failure.level = ExceptionLevel.ERROR;
        failure.i18n(DEFAULT_MESSAGE_I18N);
        String errorMessage = StringUtil.INSTANCE.isBlank(failure.code) ? DEFAULT_MESSAGE :
                DEFAULT_MESSAGE.concat("(").concat(failure.code).concat(")");
        failure.message(errorMessage);
        return failure;
    }

    public boolean isSuccess() {
        return success;
    }

    public ExceptionLevel getLevel() {
        return level;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getI18n() {
        return i18n;
    }

    public Locale getLocale() {
        return locale;
    }

    public Object getPayload() {
        return payload;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getDebug() {
        return debug;
    }

    @Override
    public String toString() {
        try {
            return DefaultObjectMapper.INSTANCE.objectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new MatrixErrorException(e);
        }
    }

    private static String stackTraceString(Throwable throwable, StringBuilder messageContainer) {
        if (null == throwable) {
            return messageContainer.toString();
        }
        messageContainer.append(throwable).append(Symbol.LINE_SEPARATOR.getSymbol());
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            messageContainer.append("    ").append(stackTrace[i]).append(Symbol.LINE_SEPARATOR.getSymbol());
            if (i > 3) {
                break;
            }
        }
        return stackTraceString(throwable.getCause(), messageContainer);
    }

    private static Throwable resolveMatrixRuntimeException(Throwable throwable) {
        Throwable current = throwable;
        while (true) {
            if (current instanceof MatrixRuntimeException) {
                return current;
            }
            if (null == current) {
                return throwable;
            }
            current = current.getCause();
        }
    }
}
