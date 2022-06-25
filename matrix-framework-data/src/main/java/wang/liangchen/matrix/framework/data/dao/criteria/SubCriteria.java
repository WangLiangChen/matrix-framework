package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class SubCriteria<E extends RootEntity> extends AbstractCriteria<E> {
    private SubCriteria(Class<E> entityClass) {
        super(entityClass);
    }

    public static <E extends RootEntity> SubCriteria<E> of(Class<E> entityClass) {
        return new SubCriteria<E>(entityClass) {
        };
    }

    @Override
    public SubCriteria<E> _equals(EntityGetter<E> column, SqlValue sqlValue) {
        return (SubCriteria<E>) super._equals(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _notEquals(EntityGetter<E> column, SqlValue sqlValue) {
        return (SubCriteria<E>) super._notEquals(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _greaterThan(EntityGetter<E> column, SqlValue sqlValue) {
        return (SubCriteria<E>) super._greaterThan(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _greaterThanOrEquals(EntityGetter<E> column, SqlValue sqlValue) {
        return (SubCriteria<E>) super._greaterThanOrEquals(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _lessThan(EntityGetter<E> column, SqlValue sqlValue) {
        return (SubCriteria<E>) super._lessThan(column, sqlValue);
    }

    @Override
    public SubCriteria<E> _lessThanOrEquals(EntityGetter<E> column, SqlValue sqlValue) {
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
    public SubCriteria<E> _in(EntityGetter<E> column, SqlValue... values) {
        return (SubCriteria<E>) super._in(column, values);
    }

    @Override
    public SubCriteria<E> _notIn(EntityGetter<E> column, SqlValue... values) {
        return (SubCriteria<E>) super._notIn(column, values);
    }

    @Override
    public SubCriteria<E> _between(EntityGetter<E> column, SqlValue valueMin, SqlValue valueMax) {
        return (SubCriteria<E>) super._between(column, valueMin, valueMax);
    }

    @Override
    public SubCriteria<E> _notBetween(EntityGetter<E> column, SqlValue valueMin, SqlValue valueMax) {
        return (SubCriteria<E>) super._notBetween(column, valueMin, valueMax);
    }

    @Override
    public SubCriteria<E> _contains(EntityGetter<E> column, String value) {
        return (SubCriteria<E>) super._contains(column, value);
    }

    @Override
    public SubCriteria<E> _notContains(EntityGetter<E> column, String value) {
        return (SubCriteria<E>) super._notContains(column, value);
    }

    @Override
    public SubCriteria<E> _startWith(EntityGetter<E> column, String value) {
        return (SubCriteria<E>) super._startWith(column, value);
    }

    @Override
    public SubCriteria<E> _notStartWith(EntityGetter<E> column, String value) {
        return (SubCriteria<E>) super._notStartWith(column, value);
    }

    @Override
    public SubCriteria<E> _endWith(EntityGetter<E> column, String value) {
        return (SubCriteria<E>) super._endWith(column, value);
    }

    @Override
    public SubCriteria<E> _notEndWith(EntityGetter<E> column, String value) {
        return (SubCriteria<E>) super._notEndWith(column, value);
    }

    @Override
    public SubCriteria<E> _OR(SubCriteria<E> subCriteria) {
        return (SubCriteria<E>) super._OR(subCriteria);
    }

    @Override
    public SubCriteria<E> _AND(SubCriteria<E> subCriteria) {
        return (SubCriteria<E>) super._AND(subCriteria);
    }
}
