package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

/**
 * @author Liangchen.Wang 2022-04-16 21:42
 */
public abstract class SqlValue {
    private final Object value;

    protected SqlValue(Object value) {
        this.value = value;
    }

    public static SqlValue of(Object value) {
        return new SqlValue(value) {
        };
    }

    public static <E extends RootEntity> SqlValue of(EntityGetter<E> value) {
        return new SqlValue(value) {
        };
    }

    public Object getValue() {
        return value;
    }

}
