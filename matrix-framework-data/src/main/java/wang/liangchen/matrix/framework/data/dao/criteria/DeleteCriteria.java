package wang.liangchen.matrix.framework.data.dao.criteria;


import wang.liangchen.matrix.framework.commons.function.LambdaUtil;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class DeleteCriteria<E extends RootEntity> extends AbstractCriteria<E> {
    /**
     * 默认刷新缓存
     */
    private boolean flushCache = true;
    private DeleteMeta deleteMeta;
    private VersionMeta versionMeta;

    private DeleteCriteria(Class<E> entityClass) {
        super(entityClass);
    }

    public static <E extends RootEntity> DeleteCriteria<E> of(Class<E> entityClass) {
        return new DeleteCriteria<E>(entityClass) {
        };
    }

    public DeleteCriteria<E> disableFlushCache() {
        this.flushCache = false;
        return this;
    }

    public boolean isFlushCache() {
        return flushCache;
    }

    public DeleteCriteria<E> markDelete(EntityGetter<E> fieldGetter, Object sqlValue) {
        String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(fieldGetter);
        ColumnMeta columnMeta = this.getColumnMetas().get(fieldName);
        this.deleteMeta = DeleteMeta.newInstance(columnMeta.getColumnName(), sqlValue);
        return this;
    }

    public DeleteCriteria<E> optimisticLock(EntityGetter<E> fieldGetter, Integer oldValue) {
        return optimisticLock(fieldGetter, oldValue, null);
    }

    public DeleteCriteria<E> optimisticLock(EntityGetter<E> fieldGetter, Object oldValue, Object newValue) {
        String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(fieldGetter);
        ColumnMeta columnMeta = this.getColumnMetas().get(fieldName);
        this.versionMeta = VersionMeta.newInstance(columnMeta.getColumnName(), oldValue, newValue);
        return this;
    }

    //--------------------------------start criteria--------------------------------------------------//

    @Override
    public DeleteCriteria<E> _equals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (DeleteCriteria<E>) super._equals(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _equalsIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._equalsIgnoreCase(fieldGetter, sqlValue);
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
    public DeleteCriteria<E> _notEqualsIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._notEqualsIgnoreCase(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _notEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (DeleteCriteria<E>) super._notEquals(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _in(EntityGetter<E> fieldGetter, Object... sqlValues) {
        return (DeleteCriteria<E>) super._in(fieldGetter, sqlValues);
    }

    @Override
    public DeleteCriteria<E> _in(EntityGetter<E> fieldGetter, Collection<?> sqlValues) {
        return (DeleteCriteria<E>) super._in(fieldGetter, sqlValues);
    }

    @Override
    public DeleteCriteria<E> _notIn(EntityGetter<E> fieldGetter, Object... sqlValues) {
        return (DeleteCriteria<E>) super._notIn(fieldGetter, sqlValues);
    }

    @Override
    public DeleteCriteria<E> _notIn(EntityGetter<E> fieldGetter, Collection<?> sqlValues) {
        return (DeleteCriteria<E>) super._notIn(fieldGetter, sqlValues);
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
    public DeleteCriteria<E> _between(EntityGetter<E> fieldGetter, Object valueMin, Object valueMax) {
        return (DeleteCriteria<E>) super._between(fieldGetter, valueMin, valueMax);
    }

    @Override
    public DeleteCriteria<E> _between(EntityGetter<E> fieldGetter, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (DeleteCriteria<E>) super._between(fieldGetter, valueMin, valueMax);
    }

    @Override
    public DeleteCriteria<E> _notBetween(EntityGetter<E> fieldGetter, Object valueMin, Object valueMax) {
        return (DeleteCriteria<E>) super._notBetween(fieldGetter, valueMin, valueMax);
    }

    @Override
    public DeleteCriteria<E> _notBetween(EntityGetter<E> fieldGetter, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (DeleteCriteria<E>) super._notBetween(fieldGetter, valueMin, valueMax);
    }

    @Override
    public DeleteCriteria<E> _contains(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._contains(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _containsIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._containsIgnoreCase(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _notContains(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._notContains(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _notContainsIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._notContainsIgnoreCase(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _startWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._startWith(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _startWithIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._startWithIgnoreCase(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _notStartWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._notStartWith(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _notStartWithIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._notStartWithIgnoreCase(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _endWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._endWith(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _endWithIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._endWithIgnoreCase(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _notEndWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._notEndWith(fieldGetter, sqlValue);
    }

    @Override
    public DeleteCriteria<E> _notEndWithIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        return (DeleteCriteria<E>) super._notEndWithIgnoreCase(fieldGetter, sqlValue);
    }

    //--------------------------------end criteria--------------------------------------------------//

    @Override
    protected DeleteCriteria<E> _or() {
        return (DeleteCriteria<E>) super._or();
    }

    @Override
    protected DeleteCriteria<E> _or(Consumer<SubCriteria<E>> consumer) {
        return (DeleteCriteria<E>) super._or(consumer);
    }

    @Override
    protected DeleteCriteria<E> _and(Consumer<SubCriteria<E>> consumer) {
        return (DeleteCriteria<E>) super._and(consumer);
    }


    protected DeleteMeta getMarkDeleteMeta() {
        return deleteMeta;
    }

    protected VersionMeta getVersionMeta() {
        return versionMeta;
    }
}
