package wang.liangchen.matrix.framework.web.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.*;
import wang.liangchen.matrix.framework.commons.runtime.MessageWrapper;
import wang.liangchen.matrix.framework.commons.runtime.ReturnWrapper;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.web.context.WebContext;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author Liangchen.Wang
 */
public final class FormattedResponse<T> implements Serializable {
    private final static Logger logger = LoggerFactory.getLogger(FormattedResponse.class);
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final static String DEFAULT_MESSAGE = "System Error!";
    /**
     * 业务代码(success=true)/错误代码(success=false)
     */
    private boolean success;
    /**
     * 错误数据/业务数据
     */
    private Object payload = new HashMap<String, Object>();
    /**
     * 前端传递的requestId，原样返回
     * 用于标识同一个请求
     */
    private String requestId;
    /**
     * 提示级别/类型
     */
    private ExceptionLevel level = ExceptionLevel.OFF;

    /**
     * 业务代码
     */
    private String code;
    /**
     * 前端提示信息
     */
    private String message;
    /**
     * i18n的Key
     */
    private String i18n;
    private Locale locale;
    /**
     * 一般为异常堆栈信息
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

    public FormattedResponse<T> payload(Object payload) {
        this.payload = payload;
        return this;
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

    public FormattedResponse<T> debug(String debug) {
        this.debug = debug;
        return this;
    }

    public static <T> FormattedResponse<T> exception(Throwable throwable) {
        FormattedResponse<T> failure = failure();
        // default message
        failure.message(DEFAULT_MESSAGE);
        if (throwable instanceof MatrixRuntimeException) {
            MatrixRuntimeException ex = (MatrixRuntimeException) throwable;
            failure.message = ex.getMessage();
            failure.code = ex.getCode();
            failure.i18n = ex.getI18n();
            failure.locale = ex.getLocale();
        }
        if (throwable instanceof MatrixInfoException) {
            logger.debug(throwable.getMessage(), throwable);
            failure.level = ExceptionLevel.INFO;
            return failure;
        }
        // populate stacktrace
        failure.debug(stackTraceString(throwable, new StringBuilder()));
        if (throwable instanceof MatrixWarnException) {
            logger.info(throwable.getMessage(), throwable);
            failure.level = ExceptionLevel.WARN;
            return failure;
        }
        // MatrixErrorException or other Exception
        logger.error(throwable.getMessage(), throwable);
        failure.level = ExceptionLevel.ERROR;
        return failure;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getCode() {
        return code;
    }

    public ExceptionLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getDebug() {
        return debug;
    }

    public String getRequestId() {
        return requestId;
    }

    public Object getPayload() {
        return payload;
    }

    public String getI18n() {
        return i18n;
    }

    public Locale getLocale() {
        return locale;
    }

    @Override
    public String toString() {
        try {
            return objectMapper.writeValueAsString(this);
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
}
