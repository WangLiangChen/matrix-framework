package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class UpdateCriteria<E extends RootEntity> extends AbstractCriteria<E> {
    private E entity;
    private Map<EntityGetter<E>, Object> forceUpdateFields;

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
        if (null == forceUpdateFields) {
            forceUpdateFields = new HashMap<>();
        }
        forceUpdateFields.put(column, value);
        return this;
    }

    @Override
    public UpdateCriteria<E> equals(EntityGetter<E> column, SqlValue sqlValue) {
        return (UpdateCriteria<E>) super.equals(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> notEquals(EntityGetter<E> column, SqlValue sqlValue) {
        return (UpdateCriteria<E>) super.notEquals(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> greaterThan(EntityGetter<E> column, SqlValue sqlValue) {
        return (UpdateCriteria<E>) super.greaterThan(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> greaterThanOrEquals(EntityGetter<E> column, SqlValue sqlValue) {
        return (UpdateCriteria<E>) super.greaterThanOrEquals(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> lessThan(EntityGetter<E> column, SqlValue sqlValue) {
        return (UpdateCriteria<E>) super.lessThan(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> lessThanOrEquals(EntityGetter<E> column, SqlValue sqlValue) {
        return (UpdateCriteria<E>) super.lessThanOrEquals(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> isNull(EntityGetter<E> column) {
        return (UpdateCriteria<E>) super.isNull(column);
    }

    @Override
    public UpdateCriteria<E> isNotNull(EntityGetter<E> column) {
        return (UpdateCriteria<E>) super.isNotNull(column);
    }

    @Override
    public UpdateCriteria<E> in(EntityGetter<E> column, SqlValue... values) {
        return (UpdateCriteria<E>) super.in(column, values);
    }

    @Override
    public UpdateCriteria<E> notIn(EntityGetter<E> column, SqlValue... values) {
        return (UpdateCriteria<E>) super.notIn(column, values);
    }

    @Override
    public UpdateCriteria<E> between(EntityGetter<E> column, SqlValue valueMin, SqlValue valueMax) {
        return (UpdateCriteria<E>) super.between(column, valueMin, valueMax);
    }

    @Override
    public UpdateCriteria<E> notBetween(EntityGetter<E> column, SqlValue valueMin, SqlValue valueMax) {
        return (UpdateCriteria<E>) super.notBetween(column, valueMin, valueMax);
    }

    @Override
    public UpdateCriteria<E> contains(EntityGetter<E> column, String value) {
        return (UpdateCriteria<E>) super.contains(column, value);
    }

    @Override
    public UpdateCriteria<E> notContains(EntityGetter<E> column, String value) {
        return (UpdateCriteria<E>) super.notContains(column, value);
    }

    @Override
    public UpdateCriteria<E> startWith(EntityGetter<E> column, String value) {
        return (UpdateCriteria<E>) super.startWith(column, value);
    }

    @Override
    public UpdateCriteria<E> notStartWith(EntityGetter<E> column, String value) {
        return (UpdateCriteria<E>) super.notStartWith(column, value);
    }

    @Override
    public UpdateCriteria<E> endWith(EntityGetter<E> column, String value) {
        return (UpdateCriteria<E>) super.endWith(column, value);
    }

    @Override
    public UpdateCriteria<E> notEndWith(EntityGetter<E> column, String value) {
        return (UpdateCriteria<E>) super.notEndWith(column, value);
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

    protected Map<EntityGetter<E>, Object> getForceUpdateFields() {
        return forceUpdateFields;
    }
}
