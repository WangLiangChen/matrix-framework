package wang.liangchen.matrix.framework.data.dao.criteria;


import wang.liangchen.matrix.framework.commons.function.LambdaUtil;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class DeleteCriteria<E extends RootEntity> extends AbstractCriteria<E> {
    private boolean flushCache = true;
    private String deleteColumnName;
    private Object deleteValue;

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

    public DeleteCriteria<E> markDelete(EntityGetter<E> fieldGetter, Object sqlValue) {
        String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(fieldGetter);
        ColumnMeta columnMeta = this.getColumnMetas().get(fieldName);
        this.deleteColumnName = columnMeta.getColumnName();
        this.deleteValue = sqlValue;
        return this;
    }

    @Override
    public DeleteCriteria<E> ignoreStringBlank() {
        return (DeleteCriteria<E>) super.ignoreStringBlank();
    }


    @Override
    public DeleteCriteria<E> _equals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (DeleteCriteria<E>) super._equals(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _equals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (DeleteCriteria<E>) super._equals(fieldGetter, sqlValue);
    }


    @Override
    public DeleteCriteria<E> _notEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (DeleteCriteria<E>) super._notEquals(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _notEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (DeleteCriteria<E>) super._notEquals(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _in(EntityGetter<E> fieldGetter, Object... values) {
        return (DeleteCriteria<E>) super._in(fieldGetter, values);
    }

    @Override
    public DeleteCriteria<E> _in(EntityGetter<E> fieldGetter, Collection<?> values) {
        return (DeleteCriteria<E>) super._in(fieldGetter, values);
    }

    @Override
    public DeleteCriteria<E> _notIn(EntityGetter<E> fieldGetter, Object... values) {
        return (DeleteCriteria<E>) super._notIn(fieldGetter, values);
    }

    @Override
    public DeleteCriteria<E> _notIn(EntityGetter<E> fieldGetter, Collection<?> values) {
        return (DeleteCriteria<E>) super._notIn(fieldGetter, values);
    }


    @Override
    public DeleteCriteria<E> _greaterThan(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (DeleteCriteria<E>) super._greaterThan(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _greaterThan(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (DeleteCriteria<E>) super._greaterThan(fieldGetter, sqlValue);
    }


    @Override
    public DeleteCriteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (DeleteCriteria<E>) super._greaterThanOrEquals(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (DeleteCriteria<E>) super._greaterThanOrEquals(fieldGetter, sqlValue);
    }


    @Override
    public DeleteCriteria<E> _lessThan(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (DeleteCriteria<E>) super._lessThan(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _lessThan(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (DeleteCriteria<E>) super._lessThan(fieldGetter, sqlValue);
    }


    @Override
    public DeleteCriteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (DeleteCriteria<E>) super._lessThanOrEquals(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (DeleteCriteria<E>) super._lessThanOrEquals(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _isNull(EntityGetter<E> fieldGetter) {
        return (DeleteCriteria<E>) super._isNull(fieldGetter);
    }

    @Override
    public DeleteCriteria<E> _isNotNull(EntityGetter<E> fieldGetter) {
        return (DeleteCriteria<E>) super._isNotNull(fieldGetter);
    }

    @Override
    public DeleteCriteria<E> _between(EntityGetter<E> fieldGetter, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (DeleteCriteria<E>) super._between(fieldGetter, valueMin, valueMax);
    }

    @Override
    public DeleteCriteria<E> _between(EntityGetter<E> fieldGetter, Object valueMin, Object valueMax) {
        return (DeleteCriteria<E>) super._between(fieldGetter, valueMin, valueMax);
    }

    @Override
    public DeleteCriteria<E> _notBetween(EntityGetter<E> fieldGetter, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (DeleteCriteria<E>) super._notBetween(fieldGetter, valueMin, valueMax);
    }

    @Override
    public DeleteCriteria<E> _notBetween(EntityGetter<E> fieldGetter, Object valueMin, Object valueMax) {
        return (DeleteCriteria<E>) super._notBetween(fieldGetter, valueMin, valueMax);
    }

    @Override
    public DeleteCriteria<E> _contains(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._contains(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _notContains(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._notContains(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _startWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._startWith(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _notStartWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._notStartWith(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _endWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._endWith(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _notEndWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._notEndWith(fieldGetter, sqlValue);
    }

    @Override
    protected DeleteCriteria<E> _or(Consumer<SubCriteria<E>> consumer) {
        return (DeleteCriteria<E>) super._or(consumer);
    }

    @Override
    protected DeleteCriteria<E> _and(Consumer<SubCriteria<E>> consumer) {
        return (DeleteCriteria<E>) super._and(consumer);
    }

    public boolean isFlushCache() {
        return flushCache;
    }

    protected String getDeleteColumnName() {
        return deleteColumnName;
    }

    protected Object getDeleteValue() {
        return deleteValue;
    }
}
