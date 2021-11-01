package wang.liangchen.matrix.framework.web.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.exception.MatrixPromptException;
import wang.liangchen.matrix.framework.commons.exception.MatrixRuntimeException;
import wang.liangchen.matrix.framework.commons.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author Liangchen.Wang
 */
public final class FormattedResponse implements Serializable {
    private final static Logger logger = LoggerFactory.getLogger(FormattedResponse.class);
    private final static String MESSAGE = "System error, please contact the administrator!";

    private boolean success;
    /**
     * 业务代码/错误代码
     */
    private String code;
    /**
     * 提示信息/前端提示信息Key
     */
    private String message;
    /**
     * 一般为异常堆栈信息
     */
    private String debug;
    /**
     * 错误数据/业务数据
     */
    private Object payload;

    public static FormattedResponse exception(Throwable throwable) {
        FormattedResponse failure = failure().message(MESSAGE);
        if (throwable instanceof MatrixRuntimeException) {
            MatrixRuntimeException ex = (MatrixRuntimeException) throwable;
            failure.code(ex.getCode()).payload(ex.getPayload());
        }
        if (throwable instanceof MatrixPromptException) {
            logger.debug(throwable.getMessage(), throwable);
            failure.message(throwable.getMessage());
            return failure;
        }
        failure.debug(stackTraceString(throwable));
        if (throwable instanceof MatrixInfoException) {
            logger.info(throwable.getMessage(), throwable);
            return failure;
        }
        if (throwable instanceof MatrixErrorException) {
            logger.error(throwable.getMessage(), throwable);
            return failure;
        }
        return failure;
    }

    public static FormattedResponse failure() {
        FormattedResponse responseEntity = new FormattedResponse();
        responseEntity.success = false;
        return responseEntity;
    }

    public static FormattedResponse success() {
        FormattedResponse responseEntity = new FormattedResponse();
        responseEntity.success = true;
        return responseEntity;
    }

    public FormattedResponse code(String code) {
        this.code = code;
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


    public String getMessage() {
        return message;
    }

    public String getDebug() {
        return debug;
    }

    public Object getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new MatrixErrorException(e);
        }
    }

    private static String stackTraceString(Throwable throwable) {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            builder.append(stackTraceElement.toString()).append(Symbol.LINE_SEPARATOR.getSymbol());
        }
        return builder.toString();
    }
}
