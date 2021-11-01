package wang.liangchen.matrix.framework.data.mybatis;

import wang.liangchen.matrix.framework.commons.object.NullValue;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import static java.lang.reflect.Array.*;

/**
 * @author LiangChen.Wang
 * 提供给mybatis判断使用
 * <if test="@liangchen.wang.gradf.framework.data.mybatis.Ognl@isNotBlank(tenantId)">
 */
public class Ognl {
    public static boolean isTrue(Boolean bool) {
        return null != bool && bool;
    }

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean isNotNull(Object object) {
        return object != null;
    }

    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        }

        if (object instanceof String) {
            return ((String) object).length() == 0;
        } else if (object instanceof Collection) {
            return ((Collection<?>) object).isEmpty();
        } else if (object.getClass().isArray()) {
            return getLength(object) == 0;
        } else if (object instanceof Map) {
            return ((Map<?, ?>) object).isEmpty();
        } else {
            return false;
        }
    }

    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    public static boolean isNullValue(Object object) {
        return object instanceof NullValue;
    }
}
