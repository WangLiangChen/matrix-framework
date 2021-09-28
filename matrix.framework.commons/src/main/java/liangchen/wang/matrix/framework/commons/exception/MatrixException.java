package liangchen.wang.matrix.framework.commons.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Liangchen.Wang 2021-08-19 9:38
 * Tag/Marker Class
 */
public class MatrixException extends Exception {
    /**
     * 错误代码
     */
    private String code;
    /**
     * 异常数据对象 非线程安全
     */
    private Map<String, Object> payload;

    public MatrixException() {
    }

    public MatrixException(String message) {
        super(message);
    }

    public MatrixException(String message, Throwable cause) {
        super(message, cause);
    }

    public MatrixException(Throwable cause) {
        super(cause);
    }

    public MatrixException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public void withCode(String code) {
        this.code = code;
    }

    public void withPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public void putPayload(String key, Object value) {
        if (null == payload) {
            payload = new HashMap<>();
        }
        payload.put(key, value);
    }

    public String getCode() {
        return code;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }
}
