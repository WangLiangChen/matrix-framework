package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class UpdateCriteria<T extends RootEntity> extends AbstractCriteria<T> {
    private T entity;
    private Map<EntityGetter<T>, Object> forceUpdateColumns;

    @SuppressWarnings("unchecked")
    private UpdateCriteria(T entity) {
        super((Class<T>) entity.getClass());
        this.entity = entity;
    }

    public static <T extends RootEntity> UpdateCriteria<T> of(T entity) {
        return new UpdateCriteria<T>(entity) {
        };
    }

    public UpdateCriteria<T> forceUpdate(EntityGetter<T> column, Object value) {
        if (null == forceUpdateColumns) {
            forceUpdateColumns = new HashMap<>();
        }
        forceUpdateColumns.put(column, value);
        return this;
    }

    @Override
    public UpdateCriteria<T> equals(EntityGetter<T> column, SqlValue sqlValue) {
        return (UpdateCriteria<T>) super.equals(column, sqlValue);
    }

    @Override
    public UpdateCriteria<T> OR(SubCriteria<T> subCriteria) {
        return (UpdateCriteria<T>) super.OR(subCriteria);
    }

    @Override
    public UpdateCriteria<T> AND(SubCriteria<T> subCriteria) {
        return (UpdateCriteria<T>) super.AND(subCriteria);
    }

    protected T getEntity() {
        return entity;
    }

    protected Map<EntityGetter<T>, Object> getForceUpdateColumns() {
        return forceUpdateColumns;
    }
}
