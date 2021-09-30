package liangchen.wang.matrix.framework.commons.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Liangchen.Wang 2021-08-19 9:38
 * Tag/Marker Class
 */
public class MatrixRuntimeException extends RuntimeException {
    /**
     * 错误代码
     */
    private String code;
    /**
     * 异常数据对象 非线程安全
     */
    private Map<String, Object> payload;


    public MatrixRuntimeException() {
    }

    public MatrixRuntimeException(String message) {
        super(message);
    }

    public MatrixRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MatrixRuntimeException(Throwable cause) {
        super(cause);
    }

    public MatrixRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MatrixRuntimeException withCode(String code) {
        this.code = code;
        return this;
    }

    public MatrixRuntimeException withPayload(Map<String, Object> payload) {
        this.payload = payload;
        return this;
    }

    public MatrixRuntimeException putPayload(String key, Object value) {
        if (null == payload) {
            payload = new HashMap<>();
        }
        payload.put(key, value);
        return this;
    }

    public String getCode() {
        return code;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }
}
