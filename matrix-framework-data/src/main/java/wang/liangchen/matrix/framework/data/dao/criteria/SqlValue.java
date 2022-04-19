package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

/**
 * @author Liangchen.Wang 2022-04-16 21:42
 */
public abstract class SqlValue<T> {
    private final T value;

    protected SqlValue(T value) {
        this.value = value;
    }

    public static <T> SqlValue of(T value) {
        return new SqlValue(value) {
        };
    }

    public static <E extends RootEntity> SqlValue<EntityGetter<E>> of(EntityGetter<E> value) {
        return new SqlValue(value) {
        };
    }

    public T getValue() {
        return value;
    }


}
