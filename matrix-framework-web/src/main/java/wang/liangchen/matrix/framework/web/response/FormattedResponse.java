package wang.liangchen.matrix.framework.web.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.*;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.web.context.WebContext;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Liangchen.Wang
 */
public final class FormattedResponse implements Serializable {
    private final static Logger logger = LoggerFactory.getLogger(FormattedResponse.class);
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final static String MESSAGE = "System Error!";

    private boolean success = false;
    /**
     * 业务代码(success=true)/错误代码(success=false)
     */
    private String code = StringUtil.INSTANCE.blankString();
    /**
     * 提示级别/类型
     */
    private ExceptionLevel level = ExceptionLevel.OFF;
    /**
     * 提示信息/前端提示信息Key
     */
    private String message = StringUtil.INSTANCE.blankString();
    /**
     * 一般为异常堆栈信息
     */
    private String debug = StringUtil.INSTANCE.blankString();
    /**
     * 前端传递的requestId，原样返回
     * 用于标识同一个请求
     */
    private String requestId = StringUtil.INSTANCE.blankString();
    /**
     * 错误数据/业务数据
     */
    private Object payload = new HashMap<String, String>(0);

    public static FormattedResponse newInstance() {
        FormattedResponse formattedResponse = ClassUtil.INSTANCE.instantiate(FormattedResponse.class);
        String requestId = WebContext.INSTANCE.getRequestId();
        if (null != requestId) {
            formattedResponse.requestId = requestId;
        }
        return formattedResponse;
    }

    public static FormattedResponse exception(Throwable throwable) {
        FormattedResponse failure = failure().message(MESSAGE);
        if (throwable instanceof MatrixRuntimeException) {
            MatrixRuntimeException ex = (MatrixRuntimeException) throwable;
            String exCode = ex.getCode();
            if (null != exCode) {
                failure.code(exCode);
            }
            Map<String, Object> exPayload = ex.getPayload();
            if (null != exPayload) {
                failure.payload(exPayload);
            }
        }
        if (throwable instanceof MatrixInfoException) {
            logger.debug(throwable.getMessage(), throwable);
            failure.level(ExceptionLevel.INFO);
            failure.message(throwable.getMessage());
            return failure;
        }
        failure.debug(stackTraceString(throwable, new StringBuilder()));
        if (throwable instanceof MatrixWarnException) {
            logger.info(throwable.getMessage(), throwable);
            failure.level(ExceptionLevel.WARN);
            return failure;
        }
        // MatrixErrorException or other Exception
        logger.error(throwable.getMessage(), throwable);
        failure.level(ExceptionLevel.ERROR);
        return failure;
    }

    public static FormattedResponse failure() {
        FormattedResponse responseEntity = newInstance();
        responseEntity.success = false;
        return responseEntity;
    }

    public static FormattedResponse success() {
        FormattedResponse responseEntity = newInstance();
        responseEntity.success = true;
        return responseEntity;
    }

    public FormattedResponse code(String code) {
        this.code = code;
        return this;
    }

    public FormattedResponse level(ExceptionLevel level) {
        this.level = level;
        return this;
    }

    public FormattedResponse message(String message, Object... args) {
        this.message = StringUtil.INSTANCE.format(message, args);
        return this;
    }

    public FormattedResponse debug(String debug) {
        this.debug = debug;
        return this;
    }

    public FormattedResponse requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public FormattedResponse payload(Object payload) {
        this.payload = payload;
        return this;
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
