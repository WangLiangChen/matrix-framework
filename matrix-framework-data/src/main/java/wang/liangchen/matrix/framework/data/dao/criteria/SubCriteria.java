package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author Liangchen.Wang 2023-04-10 18:24
 */
public abstract class SubCriteria<E extends RootEntity> extends AbstractObjectCriteria<E> {

    private SubCriteria(E entity, AndOr andOr) {
        super(entity, andOr);
    }

    private SubCriteria(Class<E> entityClass, AndOr andOr) {
        super(entityClass, andOr);
    }

    protected static <E extends RootEntity> SubCriteria<E> newInstance(E entity, AndOr andOr) {
        return new SubCriteria<E>(entity, andOr) {
        };
    }

    protected static <E extends RootEntity> SubCriteria<E> newInstance(Class<E> entityClass, AndOr andOr) {
        return new SubCriteria<E>(entityClass, andOr) {
        };
    }

    @Override
    public SubCriteria<E> _equals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (SubCriteria<E>) super._equals(fieldGetter, sqlValue);
    }

    @Override
    public SubCriteria<E> _equals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (SubCriteria<E>) super._equals(fieldGetter, sqlValue);
    }

    @Override
    public SubCriteria<E> _notEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (SubCriteria<E>) super._notEquals(fieldGetter, sqlValue);
    }

    @Override
    public SubCriteria<E> _notEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (SubCriteria<E>) super._notEquals(fieldGetter, sqlValue);
    }

    @Override
    public SubCriteria<E> _in(EntityGetter<E> fieldGetter, Object... values) {
        return (SubCriteria<E>) super._in(fieldGetter, values);
    }

    @Override
    public SubCriteria<E> _in(EntityGetter<E> fieldGetter, Collection<?> values) {
        return (SubCriteria<E>) super._in(fieldGetter, values);
    }

    @Override
    public SubCriteria<E> _notIn(EntityGetter<E> fieldGetter, Object... values) {
        return (SubCriteria<E>) super._notIn(fieldGetter, values);
    }

    @Override
    public SubCriteria<E> _notIn(EntityGetter<E> fieldGetter, Collection<?> values) {
        return (SubCriteria<E>) super._notIn(fieldGetter, values);
    }

    @Override
    public SubCriteria<E> _greaterThan(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (SubCriteria<E>) super._greaterThan(fieldGetter, sqlValue);
    }

    @Override
    public SubCriteria<E> _greaterThan(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (SubCriteria<E>) super._greaterThan(fieldGetter, sqlValue);
    }

    @Override
    public SubCriteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (SubCriteria<E>) super._greaterThanOrEquals(fieldGetter, sqlValue);
    }

    @Override
    public SubCriteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (SubCriteria<E>) super._greaterThanOrEquals(fieldGetter, sqlValue);
    }

    @Override
    public SubCriteria<E> _lessThan(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (SubCriteria<E>) super._lessThan(fieldGetter, sqlValue);
    }

    @Override
    public SubCriteria<E> _lessThan(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (SubCriteria<E>) super._lessThan(fieldGetter, sqlValue);
    }

    @Override
    public SubCriteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (SubCriteria<E>) super._lessThanOrEquals(fieldGetter, sqlValue);
    }

    @Override
    public SubCriteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (SubCriteria<E>) super._lessThanOrEquals(fieldGetter, sqlValue);
    }

    @Override
    public SubCriteria<E> _isNull(EntityGetter<E> fieldGetter) {
        return (SubCriteria<E>) super._isNull(fieldGetter);
    }

    @Override
    public SubCriteria<E> _isNotNull(EntityGetter<E> fieldGetter) {
        return (SubCriteria<E>) super._isNotNull(fieldGetter);
    }

    @Override
    public SubCriteria<E> _between(EntityGetter<E> fieldGetter, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (SubCriteria<E>) super._between(fieldGetter, valueMin, valueMax);
    }

    @Override
    public SubCriteria<E> _between(EntityGetter<E> fieldGetter, Object valueMin, Object valueMax) {
        return (SubCriteria<E>) super._between(fieldGetter, valueMin, valueMax);
    }

    @Override
    public SubCriteria<E> _notBetween(EntityGetter<E> fieldGetter, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (SubCriteria<E>) super._notBetween(fieldGetter, valueMin, valueMax);
    }

    @Override
    public SubCriteria<E> _notBetween(EntityGetter<E> fieldGetter, Object valueMin, Object valueMax) {
        return (SubCriteria<E>) super._notBetween(fieldGetter, valueMin, valueMax);
    }

    @Override
    public SubCriteria<E> _contains(EntityGetter<E> fieldGetter, String sqlValue) {
        return (SubCriteria<E>) super._contains(fieldGetter, sqlValue);
    }

    @Override
    public SubCriteria<E> _notContains(EntityGetter<E> fieldGetter, String sqlValue) {
        return (SubCriteria<E>) super._notContains(fieldGetter, sqlValue);
    }

    @Override
    public SubCriteria<E> _startWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (SubCriteria<E>) super._startWith(fieldGetter, sqlValue);
    }

    @Override
    public SubCriteria<E> _notStartWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (SubCriteria<E>) super._notStartWith(fieldGetter, sqlValue);
    }

    @Override
    public SubCriteria<E> _endWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (SubCriteria<E>) super._endWith(fieldGetter, sqlValue);
    }

    @Override
    public SubCriteria<E> _notEndWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (SubCriteria<E>) super._notEndWith(fieldGetter, sqlValue);
    }

    @Override
    protected SubCriteria<E> _equals(EntityGetter<E> fieldGetter) {
        return (SubCriteria<E>) super._equals(fieldGetter);
    }

    @Override
    protected SubCriteria<E> _notEquals(EntityGetter<E> fieldGetter) {
        return (SubCriteria<E>) super._notEquals(fieldGetter);
    }

    @Override
    protected SubCriteria<E> _greaterThan(EntityGetter<E> fieldGetter) {
        return (SubCriteria<E>) super._greaterThan(fieldGetter);
    }

    @Override
    protected SubCriteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter) {
        return (SubCriteria<E>) super._greaterThanOrEquals(fieldGetter);
    }

    @Override
    protected SubCriteria<E> _lessThan(EntityGetter<E> fieldGetter) {
        return (SubCriteria<E>) super._lessThan(fieldGetter);
    }

    @Override
    protected SubCriteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter) {
        return (SubCriteria<E>) super._lessThanOrEquals(fieldGetter);
    }

    @Override
    public SubCriteria<E> _or() {
        return (SubCriteria<E>) super._or();
    }

    @Override
    public SubCriteria<E> _or(Consumer<SubCriteria<E>> consumer) {
        return (SubCriteria<E>) super._or(consumer);
    }

    @Override
    public SubCriteria<E> _and(Consumer<SubCriteria<E>> consumer) {
        return (SubCriteria<E>) super._and(consumer);
    }
}
