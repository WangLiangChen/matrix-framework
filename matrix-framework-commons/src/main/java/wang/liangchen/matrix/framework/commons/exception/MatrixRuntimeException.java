package wang.liangchen.matrix.framework.commons.exception;

import wang.liangchen.matrix.framework.commons.StringUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.util.Locale;

/**
 * @author Liangchen.Wang 2021-08-19 9:38
 * Tag/Marker Class
 */
public class MatrixRuntimeException extends RuntimeException {
    /**
     * 错误代码
     */
    private String code;
    private String i18n;
    private Locale locale;


    public MatrixRuntimeException() {
    }

    public MatrixRuntimeException(String message, Object... args) {
        super(StringUtil.INSTANCE.format(message, args));
    }

    public MatrixRuntimeException(Throwable cause) {
        super(cause);
    }
    public MatrixRuntimeException(Throwable cause, String message, Object... args) {

    }


    public MatrixRuntimeException withCode(String code) {
        this.code = code;
        return this;
    }

    public String getCode() {
        return code;
    }

    public String getI18n() {
        return i18n;
    }

    public Locale getLocale() {
        return locale;
    }
}
