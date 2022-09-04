package wang.liangchen.matrix.framework.data.dao.criteria;


import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class UpdateCriteria<E extends RootEntity> extends AbstractCriteria<E> {
    private Map<EntityGetter<E>, Object> forceUpdateFields;

    @SuppressWarnings("unchecked")
    private UpdateCriteria(E entity) {
        super(entity);
    }
    private UpdateCriteria(Class<E> entityClass) {
        super(entityClass);
    }

    public static <E extends RootEntity> UpdateCriteria<E> of(E entity) {
        return new UpdateCriteria<E>(entity) {
        };
    }
    public static <E extends RootEntity> UpdateCriteria<E> of(Class<E> entityClass) {
        return new UpdateCriteria<E>(entityClass) {
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
    public UpdateCriteria<E> _equals(EntityGetter<E> column) {
        return (UpdateCriteria<E>) super._equals(column);
    }

    @Override
    public UpdateCriteria<E> _equals(EntityGetter<E> column, Object sqlValue) {
        return (UpdateCriteria<E>) super._equals(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _equals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (UpdateCriteria<E>) super._equals(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _notEquals(EntityGetter<E> column) {
        return (UpdateCriteria<E>) super._notEquals(column);
    }

    @Override
    public UpdateCriteria<E> _notEquals(EntityGetter<E> column, Object sqlValue) {
        return (UpdateCriteria<E>) super._notEquals(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _notEquals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (UpdateCriteria<E>) super._notEquals(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _in(EntityGetter<E> column, Object... values) {
        return (UpdateCriteria<E>) super._in(column, values);
    }

    @Override
    public UpdateCriteria<E> _notIn(EntityGetter<E> column, Object... values) {
        return (UpdateCriteria<E>) super._notIn(column, values);
    }

    @Override
    public UpdateCriteria<E> _greaterThan(EntityGetter<E> column) {
        return (UpdateCriteria<E>) super._greaterThan(column);
    }

    @Override
    public UpdateCriteria<E> _greaterThan(EntityGetter<E> column, Object sqlValue) {
        return (UpdateCriteria<E>) super._greaterThan(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _greaterThan(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (UpdateCriteria<E>) super._greaterThan(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _greaterThanOrEquals(EntityGetter<E> column) {
        return (UpdateCriteria<E>) super._greaterThanOrEquals(column);
    }

    @Override
    public UpdateCriteria<E> _greaterThanOrEquals(EntityGetter<E> column, Object sqlValue) {
        return (UpdateCriteria<E>) super._greaterThanOrEquals(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _greaterThanOrEquals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (UpdateCriteria<E>) super._greaterThanOrEquals(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _lessThan(EntityGetter<E> column) {
        return (UpdateCriteria<E>) super._lessThan(column);
    }

    @Override
    public UpdateCriteria<E> _lessThan(EntityGetter<E> column, Object sqlValue) {
        return (UpdateCriteria<E>) super._lessThan(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _lessThan(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (UpdateCriteria<E>) super._lessThan(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _lessThanOrEquals(EntityGetter<E> column) {
        return (UpdateCriteria<E>) super._lessThanOrEquals(column);
    }

    @Override
    public UpdateCriteria<E> _lessThanOrEquals(EntityGetter<E> column, Object sqlValue) {
        return (UpdateCriteria<E>) super._lessThanOrEquals(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _lessThanOrEquals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
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
    public UpdateCriteria<E> _between(EntityGetter<E> column, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (UpdateCriteria<E>) super._between(column, valueMin, valueMax);
    }

    @Override
    public UpdateCriteria<E> _between(EntityGetter<E> column, Object valueMin, Object valueMax) {
        return (UpdateCriteria<E>) super._between(column, valueMin, valueMax);
    }

    @Override
    public UpdateCriteria<E> _notBetween(EntityGetter<E> column, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (UpdateCriteria<E>) super._notBetween(column, valueMin, valueMax);
    }

    @Override
    public UpdateCriteria<E> _notBetween(EntityGetter<E> column, Object valueMin, Object valueMax) {
        return (UpdateCriteria<E>) super._notBetween(column, valueMin, valueMax);
    }

    @Override
    public UpdateCriteria<E> _contains(EntityGetter<E> column, String sqlValue) {
        return (UpdateCriteria<E>) super._contains(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _notContains(EntityGetter<E> column, String sqlValue) {
        return (UpdateCriteria<E>) super._notContains(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _startWith(EntityGetter<E> column, String sqlValue) {
        return (UpdateCriteria<E>) super._startWith(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _notStartWith(EntityGetter<E> column, String sqlValue) {
        return (UpdateCriteria<E>) super._notStartWith(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _endWith(EntityGetter<E> column, String sqlValue) {
        return (UpdateCriteria<E>) super._endWith(column, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _notEndWith(EntityGetter<E> column, String sqlValue) {
        return (UpdateCriteria<E>) super._notEndWith(column, sqlValue);
    }

    public Map<EntityGetter<E>, Object> getForceUpdateFields() {
        return forceUpdateFields;
    }
}
