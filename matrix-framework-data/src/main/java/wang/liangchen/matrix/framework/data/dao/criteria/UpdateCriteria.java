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
    public UpdateCriteria<E> _equals(EntityGetter<E> column, SqlValue sqlValue) {
        return (UpdateCriteria<E>) super._equals(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _notEquals(EntityGetter<E> column, SqlValue sqlValue) {
        return (UpdateCriteria<E>) super._notEquals(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _greaterThan(EntityGetter<E> column, SqlValue sqlValue) {
        return (UpdateCriteria<E>) super._greaterThan(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _greaterThanOrEquals(EntityGetter<E> column, SqlValue sqlValue) {
        return (UpdateCriteria<E>) super._greaterThanOrEquals(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _lessThan(EntityGetter<E> column, SqlValue sqlValue) {
        return (UpdateCriteria<E>) super._lessThan(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _lessThanOrEquals(EntityGetter<E> column, SqlValue sqlValue) {
        return (UpdateCriteria<E>) super._lessThanOrEquals(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _isNull(EntityGetter<E> column) {
        return (UpdateCriteria<E>) super._isNull(column);
    }

    @Override
    public UpdateCriteria<E> _isNotNull(EntityGetter<E> column) {
        return (UpdateCriteria<E>) super._isNotNull(column);
    }

    @Override
    public UpdateCriteria<E> _in(EntityGetter<E> column, SqlValue... values) {
        return (UpdateCriteria<E>) super._in(column, values);
    }

    @Override
    public UpdateCriteria<E> _notIn(EntityGetter<E> column, SqlValue... values) {
        return (UpdateCriteria<E>) super._notIn(column, values);
    }

    @Override
    public UpdateCriteria<E> _between(EntityGetter<E> column, SqlValue valueMin, SqlValue valueMax) {
        return (UpdateCriteria<E>) super._between(column, valueMin, valueMax);
    }

    @Override
    public UpdateCriteria<E> _notBetween(EntityGetter<E> column, SqlValue valueMin, SqlValue valueMax) {
        return (UpdateCriteria<E>) super._notBetween(column, valueMin, valueMax);
    }

    @Override
    public UpdateCriteria<E> _contains(EntityGetter<E> column, String value) {
        return (UpdateCriteria<E>) super._contains(column, value);
    }

    @Override
    public UpdateCriteria<E> _notContains(EntityGetter<E> column, String value) {
        return (UpdateCriteria<E>) super._notContains(column, value);
    }

    @Override
    public UpdateCriteria<E> _startWith(EntityGetter<E> column, String value) {
        return (UpdateCriteria<E>) super._startWith(column, value);
    }

    @Override
    public UpdateCriteria<E> _notStartWith(EntityGetter<E> column, String value) {
        return (UpdateCriteria<E>) super._notStartWith(column, value);
    }

    @Override
    public UpdateCriteria<E> _endWith(EntityGetter<E> column, String value) {
        return (UpdateCriteria<E>) super._endWith(column, value);
    }

    @Override
    public UpdateCriteria<E> _notEndWith(EntityGetter<E> column, String value) {
        return (UpdateCriteria<E>) super._notEndWith(column, value);
    }

    @Override
    public UpdateCriteria<E> _OR(SubCriteria<E> subCriteria) {
        return (UpdateCriteria<E>) super._OR(subCriteria);
    }

    @Override
    public UpdateCriteria<E> _AND(SubCriteria<E> subCriteria) {
        return (UpdateCriteria<E>) super._AND(subCriteria);
    }

    protected E getEntity() {
        return entity;
    }

    protected Map<EntityGetter<E>, Object> getForceUpdateFields() {
        return forceUpdateFields;
    }
}
