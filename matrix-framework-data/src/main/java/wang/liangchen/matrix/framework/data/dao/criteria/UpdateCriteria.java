package wang.liangchen.matrix.framework.data.dao.criteria;


import wang.liangchen.matrix.framework.commons.function.LambdaUtil;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class UpdateCriteria<E extends RootEntity> extends AbstractCriteria<E> {
    private final Map<String, Object> forceUpdateColumns = new HashMap<>();
    private VersionMeta versionMeta;
    /**
     * 默认刷新缓存
     */
    private boolean flushCache = true;

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

    public UpdateCriteria<E> disableFlushCache() {
        this.flushCache = false;
        return this;
    }

    public UpdateCriteria<E> nonNullUpdate(E entity) {
        UpdateCriteria<E> newCriteria = new UpdateCriteria<E>(entity) {
        };
        // populate new criteria
        newCriteria.getComposedCriteriaResolver().add(this.getComposedCriteriaResolver());
        newCriteria.forceUpdateColumns.putAll(this.forceUpdateColumns);
        newCriteria.flushCache = this.flushCache;
        return newCriteria;
    }

    public UpdateCriteria<E> forceUpdate(EntityGetter<E> fieldGetter, Object value) {
        String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(fieldGetter);
        ColumnMeta columnMeta = this.getColumnMetas().get(fieldName);
        forceUpdateColumns.put(columnMeta.getColumnName(), value);
        return this;
    }

    public UpdateCriteria<E> forceUpdate(String columnName, Object value) {
        forceUpdateColumns.put(columnName, value);
        return this;
    }

    public UpdateCriteria<E> optimisticLock(EntityGetter<E> fieldGetter, Integer oldValue) {
        return optimisticLock(fieldGetter, oldValue, null);
    }

    public UpdateCriteria<E> optimisticLock(EntityGetter<E> fieldGetter, Object oldValue, Object newValue) {
        String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(fieldGetter);
        ColumnMeta columnMeta = this.getColumnMetas().get(fieldName);
        this.versionMeta = VersionMeta.newInstance(columnMeta.getColumnName(), oldValue, newValue);
        return this;
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
    public UpdateCriteria<E> _greaterThan(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (UpdateCriteria<E>) super._greaterThan(fieldGetter, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _greaterThan(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (UpdateCriteria<E>) super._greaterThan(fieldGetter, sqlValue);
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
    public UpdateCriteria<E> _lessThan(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (UpdateCriteria<E>) super._lessThan(fieldGetter, sqlValue);
    }

    @Override
    public UpdateCriteria<E> _lessThan(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (UpdateCriteria<E>) super._lessThan(fieldGetter, sqlValue);
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

    @Override
    protected UpdateCriteria<E> _or() {
        return (UpdateCriteria<E>) super._or();
    }

    @Override
    protected UpdateCriteria<E> _or(Consumer<SubCriteria<E>> consumer) {
        return (UpdateCriteria<E>) super._or(consumer);
    }

    @Override
    protected UpdateCriteria<E> _and(Consumer<SubCriteria<E>> consumer) {
        return (UpdateCriteria<E>) super._and(consumer);
    }

    protected Map<String, Object> getForceUpdateColumns() {
        return forceUpdateColumns;
    }

    public boolean isFlushCache() {
        return flushCache;
    }
    protected VersionMeta getVersionMeta() {
        return versionMeta;
    }
}
