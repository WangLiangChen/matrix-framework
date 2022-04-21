package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class UpdateCriteria<E extends RootEntity> extends AbstractCriteria<E> {
    private E entity;
    private Map<EntityGetter<E>, Object> forceUpdateColumns;

    @SuppressWarnings("unchecked")
    private UpdateCriteria(E entity) {
        super((Class<E>) entity.getClass());
        this.entity = entity;
    }

    public static <E extends RootEntity> UpdateCriteria<E> of(E entity) {
        return new UpdateCriteria<E>(entity) {
        };
    }

    public UpdateCriteria<E> forceUpdate(EntityGetter<E> column, Object value) {
        if (null == forceUpdateColumns) {
            forceUpdateColumns = new HashMap<>();
        }
        forceUpdateColumns.put(column, value);
        return this;
    }

    @Override
    public UpdateCriteria<E> equals(EntityGetter<E> column, SqlValue sqlValue) {
        return (UpdateCriteria<E>) super.equals(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> OR(SubCriteria<E> subCriteria) {
        return (UpdateCriteria<E>) super.OR(subCriteria);
    }

    @Override
    public UpdateCriteria<E> AND(SubCriteria<E> subCriteria) {
        return (UpdateCriteria<E>) super.AND(subCriteria);
    }

    protected E getEntity() {
        return entity;
    }

    protected Map<EntityGetter<E>, Object> getForceUpdateColumns() {
        return forceUpdateColumns;
    }
}
