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
    public SubCriteria<E> equals(EntityGetter<E> column, SqlValue sqlValue) {
        return (SubCriteria<E>) super.equals(column, sqlValue);
    }

    @Override
    public SubCriteria<E> notEquals(EntityGetter<E> column, SqlValue sqlValue) {
        return (SubCriteria<E>) super.notEquals(column, sqlValue);
    }

    @Override
    public SubCriteria<E> greaterThan(EntityGetter<E> column, SqlValue sqlValue) {
        return (SubCriteria<E>) super.greaterThan(column, sqlValue);
    }

    @Override
    public SubCriteria<E> greaterThanOrEquals(EntityGetter<E> column, SqlValue sqlValue) {
        return (SubCriteria<E>) super.greaterThanOrEquals(column, sqlValue);
    }

    @Override
    public SubCriteria<E> lessThan(EntityGetter<E> column, SqlValue sqlValue) {
        return (SubCriteria<E>) super.lessThan(column, sqlValue);
    }

    @Override
    public SubCriteria<E> lessThanOrEquals(EntityGetter<E> column, SqlValue sqlValue) {
        return (SubCriteria<E>) super.lessThanOrEquals(column, sqlValue);
    }

    @Override
    public SubCriteria<E> isNull(EntityGetter<E> column) {
        return (SubCriteria<E>) super.isNull(column);
    }

    @Override
    public SubCriteria<E> isNotNull(EntityGetter<E> column) {
        return (SubCriteria<E>) super.isNotNull(column);
    }

    @Override
    public SubCriteria<E> in(EntityGetter<E> column, SqlValue... values) {
        return (SubCriteria<E>) super.in(column, values);
    }

    @Override
    public SubCriteria<E> notIn(EntityGetter<E> column, SqlValue... values) {
        return (SubCriteria<E>) super.notIn(column, values);
    }

    @Override
    public SubCriteria<E> between(EntityGetter<E> column, SqlValue valueMin, SqlValue valueMax) {
        return (SubCriteria<E>) super.between(column, valueMin, valueMax);
    }

    @Override
    public SubCriteria<E> notBetween(EntityGetter<E> column, SqlValue valueMin, SqlValue valueMax) {
        return (SubCriteria<E>) super.notBetween(column, valueMin, valueMax);
    }

    @Override
    public SubCriteria<E> contains(EntityGetter<E> column, String value) {
        return (SubCriteria<E>) super.contains(column, value);
    }

    @Override
    public SubCriteria<E> notContains(EntityGetter<E> column, String value) {
        return (SubCriteria<E>) super.notContains(column, value);
    }

    @Override
    public SubCriteria<E> startWith(EntityGetter<E> column, String value) {
        return (SubCriteria<E>) super.startWith(column, value);
    }

    @Override
    public SubCriteria<E> notStartWith(EntityGetter<E> column, String value) {
        return (SubCriteria<E>) super.notStartWith(column, value);
    }

    @Override
    public SubCriteria<E> endWith(EntityGetter<E> column, String value) {
        return (SubCriteria<E>) super.endWith(column, value);
    }

    @Override
    public SubCriteria<E> notEndWith(EntityGetter<E> column, String value) {
        return (SubCriteria<E>) super.notEndWith(column, value);
    }

    @Override
    public SubCriteria<E> OR(SubCriteria<E> subCriteria) {
        return (SubCriteria<E>) super.OR(subCriteria);
    }

    @Override
    public SubCriteria<E> AND(SubCriteria<E> subCriteria) {
        return (SubCriteria<E>) super.AND(subCriteria);
    }
}
