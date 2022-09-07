package wang.liangchen.matrix.framework.data.dao.criteria;


import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.Collection;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class SubCriteria<E extends RootEntity> extends AbstractCriteria<E> {
    private SubCriteria(E entity) {
        super(entity);
    }

    private SubCriteria(Class<E> entityClass) {
        super(entityClass);
    }

    public static <E extends RootEntity> SubCriteria<E> of(E entity) {
        return new SubCriteria<E>(entity) {
        };
    }

    public static <E extends RootEntity> SubCriteria<E> of(Class<E> entityClass) {
        return new SubCriteria<E>(entityClass) {
        };
    }

    @Override
    public SubCriteria<E> _equals(EntityGetter<E> column) {
        return (SubCriteria<E>) super._equals(column);
    }

    @Override
    public SubCriteria<E> _equals(EntityGetter<E> column, Object sqlValue) {
        return (SubCriteria<E>) super._equals(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _equals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (SubCriteria<E>) super._equals(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _notEquals(EntityGetter<E> column) {
        return (SubCriteria<E>) super._notEquals(column);
    }

    @Override
    public SubCriteria<E> _notEquals(EntityGetter<E> column, Object sqlValue) {
        return (SubCriteria<E>) super._notEquals(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _notEquals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (SubCriteria<E>) super._notEquals(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _in(EntityGetter<E> column, Object... values) {
        return (SubCriteria<E>) super._in(column, values);
    }

    @Override
    public SubCriteria<E> _in(EntityGetter<E> column, Collection<?> values) {
        return (SubCriteria<E>) super._in(column, values);
    }

    @Override
    public SubCriteria<E> _notIn(EntityGetter<E> column, Object... values) {
        return (SubCriteria<E>) super._notIn(column, values);
    }

    @Override
    public SubCriteria<E> _notIn(EntityGetter<E> column, Collection<?> values) {
        return (SubCriteria<E>) super._notIn(column, values);
    }

    @Override
    public SubCriteria<E> _greaterThan(EntityGetter<E> column) {
        return (SubCriteria<E>) super._greaterThan(column);
    }

    @Override
    public SubCriteria<E> _greaterThan(EntityGetter<E> column, Object sqlValue) {
        return (SubCriteria<E>) super._greaterThan(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _greaterThan(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (SubCriteria<E>) super._greaterThan(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _greaterThanOrEquals(EntityGetter<E> column) {
        return (SubCriteria<E>) super._greaterThanOrEquals(column);
    }

    @Override
    public SubCriteria<E> _greaterThanOrEquals(EntityGetter<E> column, Object sqlValue) {
        return (SubCriteria<E>) super._greaterThanOrEquals(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _greaterThanOrEquals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (SubCriteria<E>) super._greaterThanOrEquals(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _lessThan(EntityGetter<E> column) {
        return (SubCriteria<E>) super._lessThan(column);
    }

    @Override
    public SubCriteria<E> _lessThan(EntityGetter<E> column, Object sqlValue) {
        return (SubCriteria<E>) super._lessThan(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _lessThan(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (SubCriteria<E>) super._lessThan(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _lessThanOrEquals(EntityGetter<E> column) {
        return (SubCriteria<E>) super._lessThanOrEquals(column);
    }

    @Override
    public SubCriteria<E> _lessThanOrEquals(EntityGetter<E> column, Object sqlValue) {
        return (SubCriteria<E>) super._lessThanOrEquals(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _lessThanOrEquals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (SubCriteria<E>) super._lessThanOrEquals(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _isNull(EntityGetter<E> column) {
        return (SubCriteria<E>) super._isNull(column);
    }

    @Override
    public SubCriteria<E> _isNotNull(EntityGetter<E> column) {
        return (SubCriteria<E>) super._isNotNull(column);
    }

    @Override
    public SubCriteria<E> _between(EntityGetter<E> column, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (SubCriteria<E>) super._between(column, valueMin, valueMax);
    }

    @Override
    public SubCriteria<E> _between(EntityGetter<E> column, Object valueMin, Object valueMax) {
        return (SubCriteria<E>) super._between(column, valueMin, valueMax);
    }

    @Override
    public SubCriteria<E> _notBetween(EntityGetter<E> column, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (SubCriteria<E>) super._notBetween(column, valueMin, valueMax);
    }

    @Override
    public SubCriteria<E> _notBetween(EntityGetter<E> column, Object valueMin, Object valueMax) {
        return (SubCriteria<E>) super._notBetween(column, valueMin, valueMax);
    }

    @Override
    public SubCriteria<E> _contains(EntityGetter<E> column, String sqlValue) {
        return (SubCriteria<E>) super._contains(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _notContains(EntityGetter<E> column, String sqlValue) {
        return (SubCriteria<E>) super._notContains(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _startWith(EntityGetter<E> column, String sqlValue) {
        return (SubCriteria<E>) super._startWith(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _notStartWith(EntityGetter<E> column, String sqlValue) {
        return (SubCriteria<E>) super._notStartWith(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _endWith(EntityGetter<E> column, String sqlValue) {
        return (SubCriteria<E>) super._endWith(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _notEndWith(EntityGetter<E> column, String sqlValue) {
        return (SubCriteria<E>) super._notEndWith(column, sqlValue);
    }
}
