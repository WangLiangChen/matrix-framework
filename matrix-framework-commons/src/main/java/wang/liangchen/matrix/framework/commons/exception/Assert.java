package wang.liangchen.matrix.framework.commons.exception;

import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

import java.util.Objects;

/**
 * @author Liangchen.Wang 2021-08-19 20:23
 */
public enum Assert {
    /**
     * instance
     */
    INSTANCE;

    public void isTrue(boolean condition, String message, Object... args) {
        if (!condition) {
            throw new MatrixInfoException(message, args);
        }
    }

    public void isFalse(boolean condition, String message, Object... args) {
        if (condition) {
            throw new MatrixInfoException(message, args);
        }
    }

    public void isBlank(String string, String message, Object... args) {
        if (StringUtil.INSTANCE.isNotBlank(string)) {
            throw new MatrixInfoException(message, args);
        }
    }


    public void notBlank(String string, String message, Object... args) {
        if (StringUtil.INSTANCE.isBlank(string)) {
            throw new MatrixInfoException(message, args);
        }
    }

    public void isNull(Object object, String message, Object... args) {
        if (null != object) {
            throw new MatrixInfoException(message, args);
        }
    }

    public void notNull(Object object, String message, Object... args) {
        if (null == object) {
            throw new MatrixInfoException(message, args);
        }
    }


    public void isEmpty(Object object, String message, Object... args) {
        if (ObjectUtil.INSTANCE.isNotEmpty(object)) {
            throw new MatrixInfoException(message, args);
        }
    }

    public void notEmpty(Object object, String message, Object... args) {
        if (ObjectUtil.INSTANCE.isEmpty(object)) {
            throw new MatrixInfoException(message, args);
        }
    }

    public void equals(Object from, Object to, String message, Object... args) {
        if (Objects.equals(from, to)) {
            return;
        }
        throw new MatrixInfoException(message, args);
    }

    public void notEquals(Object from, Object to, String message, Object... args) {
        if (Objects.equals(from, to)) {
            throw new MatrixInfoException(message, args);
        }
    }

}
