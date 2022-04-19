package wang.liangchen.matrix.framework.data.mybatis;

import wang.liangchen.matrix.framework.commons.object.NullValue;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;

/**
 * @author LiangChen.Wang
 * 提供给mybatis判断使用
 * {@code <if test="@liangchen.wang.gradf.framework.data.mybatis.Ognl@isNotBlank(tenantId)">}
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
        return ObjectUtil.INSTANCE.isEmpty(object);
    }

    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    public static boolean isNullValue(Object object) {
        return object instanceof NullValue;
    }
}
