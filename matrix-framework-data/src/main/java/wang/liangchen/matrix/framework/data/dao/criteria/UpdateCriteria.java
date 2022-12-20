package wang.liangchen.matrix.framework.data.dao.criteria;


import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class UpdateCriteria<E extends RootEntity> extends AbstractCriteria<E> {
    private final Map<EntityGetter<E>, Object> forceUpdateFields = new HashMap<>();
    private boolean flushCache = true;

    @SuppressWarnings("unchecked")
    private UpdateCriteria(E entity) {
        super(entity);
    }

    public static <E extends RootEntity> UpdateCriteria<E> of(E entity) {
        return new UpdateCriteria<E>(entity) {
        };
    }

    public UpdateCriteria<E> forceUpdate(EntityGetter<E> fieldGetter, Object value) {
        forceUpdateFields.put(fieldGetter, value);
        return this;
    }

    public UpdateCriteria<E> disableCache() {
        this.flushCache = false;
        return this;
    }

    @Override
    public UpdateCriteria<E> _equals(EntityGetter<E> fieldGetter) {
        return (UpdateCriteria<E>) super._equals(fieldGetter);
    }

    @Override
    public UpdateCriteria<E> _equals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (UpdateCriteria<E>) super._equals(fieldGetter, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _equals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (UpdateCriteria<E>) super._equals(fieldGetter, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _notEquals(EntityGetter<E> fieldGetter) {
        return (UpdateCriteria<E>) super._notEquals(fieldGetter);
    }

    @Override
    public UpdateCriteria<E> _notEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (UpdateCriteria<E>) super._notEquals(fieldGetter, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _notEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (UpdateCriteria<E>) super._notEquals(fieldGetter, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _in(EntityGetter<E> fieldGetter, Object... values) {
        return (UpdateCriteria<E>) super._in(fieldGetter, values);
    }

    @Override
    public UpdateCriteria<E> _in(EntityGetter<E> fieldGetter, Collection<?> values) {
        return (UpdateCriteria<E>) super._in(fieldGetter, values);
    }

    @Override
    public UpdateCriteria<E> _notIn(EntityGetter<E> fieldGetter, Object... values) {
        return (UpdateCriteria<E>) super._notIn(fieldGetter, values);
    }

    @Override
    public UpdateCriteria<E> _notIn(EntityGetter<E> fieldGetter, Collection<?> values) {
        return (UpdateCriteria<E>) super._notIn(fieldGetter, values);
    }

    @Override
    public UpdateCriteria<E> _greaterThan(EntityGetter<E> fieldGetter) {
        return (UpdateCriteria<E>) super._greaterThan(fieldGetter);
    }

    @Override
    public UpdateCriteria<E> _greaterThan(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (UpdateCriteria<E>) super._greaterThan(fieldGetter, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _greaterThan(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (UpdateCriteria<E>) super._greaterThan(fieldGetter, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter) {
        return (UpdateCriteria<E>) super._greaterThanOrEquals(fieldGetter);
    }

    @Override
    public UpdateCriteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (UpdateCriteria<E>) super._greaterThanOrEquals(fieldGetter, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (UpdateCriteria<E>) super._greaterThanOrEquals(fieldGetter, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _lessThan(EntityGetter<E> fieldGetter) {
        return (UpdateCriteria<E>) super._lessThan(fieldGetter);
    }

    @Override
    public UpdateCriteria<E> _lessThan(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (UpdateCriteria<E>) super._lessThan(fieldGetter, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _lessThan(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (UpdateCriteria<E>) super._lessThan(fieldGetter, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter) {
        return (UpdateCriteria<E>) super._lessThanOrEquals(fieldGetter);
    }

    @Override
    public UpdateCriteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (UpdateCriteria<E>) super._lessThanOrEquals(fieldGetter, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (UpdateCriteria<E>) super._lessThanOrEquals(fieldGetter, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _isNull(EntityGetter<E> fieldGetter) {
        return (UpdateCriteria<E>) super._isNull(fieldGetter);
    }

    @Override
    public UpdateCriteria<E> _isNotNull(EntityGetter<E> fieldGetter) {
        return (UpdateCriteria<E>) super._isNotNull(fieldGetter);
    }

    @Override
    public UpdateCriteria<E> _between(EntityGetter<E> fieldGetter, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (UpdateCriteria<E>) super._between(fieldGetter, valueMin, valueMax);
    }

    @Override
    public UpdateCriteria<E> _between(EntityGetter<E> fieldGetter, Object valueMin, Object valueMax) {
        return (UpdateCriteria<E>) super._between(fieldGetter, valueMin, valueMax);
    }

    @Override
    public UpdateCriteria<E> _notBetween(EntityGetter<E> fieldGetter, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (UpdateCriteria<E>) super._notBetween(fieldGetter, valueMin, valueMax);
    }

    @Override
    public UpdateCriteria<E> _notBetween(EntityGetter<E> fieldGetter, Object valueMin, Object valueMax) {
        return (UpdateCriteria<E>) super._notBetween(fieldGetter, valueMin, valueMax);
    }

    @Override
    public UpdateCriteria<E> _contains(EntityGetter<E> fieldGetter, String sqlValue) {
        return (UpdateCriteria<E>) super._contains(fieldGetter, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _notContains(EntityGetter<E> fieldGetter, String sqlValue) {
        return (UpdateCriteria<E>) super._notContains(fieldGetter, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _startWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (UpdateCriteria<E>) super._startWith(fieldGetter, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _notStartWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (UpdateCriteria<E>) super._notStartWith(fieldGetter, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _endWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (UpdateCriteria<E>) super._endWith(fieldGetter, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _notEndWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (UpdateCriteria<E>) super._notEndWith(fieldGetter, sqlValue);
    }

    public Map<EntityGetter<E>, Object> getForceUpdateFields() {
        return forceUpdateFields;
    }

    public boolean isFlushCache() {
        return flushCache;
    }
}
