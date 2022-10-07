package wang.liangchen.matrix.framework.data.dao.criteria;


import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.Collection;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class DeleteCriteria<E extends RootEntity> extends AbstractCriteria<E> {
    private boolean flushCache = true;

    private DeleteCriteria(Class<E> entityClass) {
        super(entityClass);
    }

    public static <E extends RootEntity> DeleteCriteria<E> of(Class<E> entityClass) {
        return new DeleteCriteria<E>(entityClass) {
        };
    }

    public DeleteCriteria<E> disableCache() {
        this.flushCache = false;
        return this;
    }

    @Override
    public DeleteCriteria<E> _equals(EntityGetter<E> column) {
        return (DeleteCriteria<E>) super._equals(column);
    }

    @Override
    public DeleteCriteria<E> _equals(EntityGetter<E> column, Object sqlValue) {
        return (DeleteCriteria<E>) super._equals(column, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _equals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (DeleteCriteria<E>) super._equals(column, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _notEquals(EntityGetter<E> column) {
        return (DeleteCriteria<E>) super._notEquals(column);
    }

    @Override
    public DeleteCriteria<E> _notEquals(EntityGetter<E> column, Object sqlValue) {
        return (DeleteCriteria<E>) super._notEquals(column, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _notEquals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (DeleteCriteria<E>) super._notEquals(column, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _in(EntityGetter<E> column, Object... values) {
        return (DeleteCriteria<E>) super._in(column, values);
    }

    @Override
    public DeleteCriteria<E> _in(EntityGetter<E> column, Collection<?> values) {
        return (DeleteCriteria<E>) super._in(column, values);
    }

    @Override
    public DeleteCriteria<E> _notIn(EntityGetter<E> column, Object... values) {
        return (DeleteCriteria<E>) super._notIn(column, values);
    }

    @Override
    public DeleteCriteria<E> _notIn(EntityGetter<E> column, Collection<?> values) {
        return (DeleteCriteria<E>) super._notIn(column, values);
    }

    @Override
    public DeleteCriteria<E> _greaterThan(EntityGetter<E> column) {
        return (DeleteCriteria<E>) super._greaterThan(column);
    }

    @Override
    public DeleteCriteria<E> _greaterThan(EntityGetter<E> column, Object sqlValue) {
        return (DeleteCriteria<E>) super._greaterThan(column, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _greaterThan(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (DeleteCriteria<E>) super._greaterThan(column, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _greaterThanOrEquals(EntityGetter<E> column) {
        return (DeleteCriteria<E>) super._greaterThanOrEquals(column);
    }

    @Override
    public DeleteCriteria<E> _greaterThanOrEquals(EntityGetter<E> column, Object sqlValue) {
        return (DeleteCriteria<E>) super._greaterThanOrEquals(column, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _greaterThanOrEquals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (DeleteCriteria<E>) super._greaterThanOrEquals(column, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _lessThan(EntityGetter<E> column) {
        return (DeleteCriteria<E>) super._lessThan(column);
    }

    @Override
    public DeleteCriteria<E> _lessThan(EntityGetter<E> column, Object sqlValue) {
        return (DeleteCriteria<E>) super._lessThan(column, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _lessThan(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (DeleteCriteria<E>) super._lessThan(column, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _lessThanOrEquals(EntityGetter<E> column) {
        return (DeleteCriteria<E>) super._lessThanOrEquals(column);
    }

    @Override
    public DeleteCriteria<E> _lessThanOrEquals(EntityGetter<E> column, Object sqlValue) {
        return (DeleteCriteria<E>) super._lessThanOrEquals(column, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _lessThanOrEquals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (DeleteCriteria<E>) super._lessThanOrEquals(column, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _isNull(EntityGetter<E> column) {
        return (DeleteCriteria<E>) super._isNull(column);
    }

    @Override
    public DeleteCriteria<E> _isNotNull(EntityGetter<E> column) {
        return (DeleteCriteria<E>) super._isNotNull(column);
    }

    @Override
    public DeleteCriteria<E> _between(EntityGetter<E> column, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (DeleteCriteria<E>) super._between(column, valueMin, valueMax);
    }

    @Override
    public DeleteCriteria<E> _between(EntityGetter<E> column, Object valueMin, Object valueMax) {
        return (DeleteCriteria<E>) super._between(column, valueMin, valueMax);
    }

    @Override
    public DeleteCriteria<E> _notBetween(EntityGetter<E> column, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (DeleteCriteria<E>) super._notBetween(column, valueMin, valueMax);
    }

    @Override
    public DeleteCriteria<E> _notBetween(EntityGetter<E> column, Object valueMin, Object valueMax) {
        return (DeleteCriteria<E>) super._notBetween(column, valueMin, valueMax);
    }

    @Override
    public DeleteCriteria<E> _contains(EntityGetter<E> column, String sqlValue) {
        return (DeleteCriteria<E>) super._contains(column, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _notContains(EntityGetter<E> column, String sqlValue) {
        return (DeleteCriteria<E>) super._notContains(column, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _startWith(EntityGetter<E> column, String sqlValue) {
        return (DeleteCriteria<E>) super._startWith(column, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _notStartWith(EntityGetter<E> column, String sqlValue) {
        return (DeleteCriteria<E>) super._notStartWith(column, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _endWith(EntityGetter<E> column, String sqlValue) {
        return (DeleteCriteria<E>) super._endWith(column, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _notEndWith(EntityGetter<E> column, String sqlValue) {
        return (DeleteCriteria<E>) super._notEndWith(column, sqlValue);
    }

    public boolean isFlushCache() {
        return flushCache;
    }
}
