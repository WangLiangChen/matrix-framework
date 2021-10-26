package liangchen.wang.matrix.framework.data.mybatis;

import liangchen.wang.matrix.framework.commons.object.NullValue;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

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
            if (((String) object).length() == 0) {
                return true;
            }
        } else if (object instanceof Collection) {
            if (((Collection) object).isEmpty()) {
                return true;
            }
        } else if (object.getClass().isArray()) {
            if (Array.getLength(object) == 0) {
                return true;
            }
        } else if (object instanceof Map) {
            if (((Map) object).isEmpty()) {
                return true;
            }
        } else {
            return false;
        }

        return false;
    }

    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    public static boolean isNullValue(Object object) {
        return object instanceof NullValue;
    }
}
