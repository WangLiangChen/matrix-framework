package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.commons.function.LambdaUtil;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.Collection;
import java.util.Map;

/**
 * @author Liangchen.Wang 2023-04-10 19:05
 */
abstract class AbstractClassCriteria<E extends RootEntity> {
    private final ComposedCriteriaResolver composedCriteriaResolver;
    private final Class<E> entityClass;
    private final TableMeta tableMeta;
    private final Map<String, ColumnMeta> columnMetas;

    protected AbstractClassCriteria(Class<E> entityClass, AndOr andOr) {
        this.entityClass = entityClass;
        this.composedCriteriaResolver = ComposedCriteriaResolver.newInstance(andOr);
        this.tableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
        this.columnMetas = this.tableMeta.getColumnMetas();
    }

    protected AbstractClassCriteria(Class<E> entityClass) {
        this(entityClass, AndOr.and);
    }

    // =====================equals========================
    protected AbstractClassCriteria<E> _equals(EntityGetter<E> fieldGetter, Object sqlValue) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.EQUALS, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _equals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        ColumnMeta valueColumnMeta = resolveEntityGetter(sqlValue);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.EQUALS, Boolean.TRUE, valueColumnMeta.getColumnName()));
        return this;
    }

    // =====================notEquals========================

    protected AbstractClassCriteria<E> _notEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.NOTEQUALS, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _notEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        ColumnMeta valueColumnMeta = resolveEntityGetter(sqlValue);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.NOTEQUALS, Boolean.TRUE, valueColumnMeta.getColumnName()));
        return this;
    }

    // =====================in========================
    protected AbstractClassCriteria<E> _in(EntityGetter<E> fieldGetter, Object... sqlValues) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.IN, sqlValues));
        return this;
    }

    protected AbstractClassCriteria<E> _in(EntityGetter<E> fieldGetter, Collection<?> values) {
        _in(fieldGetter, values.toArray());
        return this;
    }

    // =====================notIn========================
    protected AbstractClassCriteria<E> _notIn(EntityGetter<E> fieldGetter, Object... sqlValues) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.NOTIN, sqlValues));
        return this;
    }

    protected AbstractClassCriteria<E> _notIn(EntityGetter<E> fieldGetter, Collection<?> values) {
        _notIn(fieldGetter, values.toArray());
        return this;
    }

    // =====================greaterThan========================


    protected AbstractClassCriteria<E> _greaterThan(EntityGetter<E> fieldGetter, Object sqlValue) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.GREATERTHAN, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _greaterThan(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        ColumnMeta valueColumnMeta = resolveEntityGetter(sqlValue);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.GREATERTHAN, Boolean.TRUE, valueColumnMeta.getColumnName()));
        return this;
    }

    // =====================greaterThanOrEauals========================


    protected AbstractClassCriteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.GREATERTHAN_OR_EQUALS, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        ColumnMeta valueColumnMeta = resolveEntityGetter(sqlValue);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.GREATERTHAN_OR_EQUALS, Boolean.TRUE, valueColumnMeta.getColumnName()));
        return this;
    }

    // =====================_lessThan========================


    protected AbstractClassCriteria<E> _lessThan(EntityGetter<E> fieldGetter, Object sqlValue) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.LESSTHAN, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _lessThan(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        ColumnMeta valueColumnMeta = resolveEntityGetter(sqlValue);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.LESSTHAN, Boolean.TRUE, valueColumnMeta.getColumnName()));
        return this;
    }

    // =====================_lessThanOrEquals========================

    protected AbstractClassCriteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.LESSTHAN_OR_EQUALS, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        ColumnMeta valueColumnMeta = resolveEntityGetter(sqlValue);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.LESSTHAN_OR_EQUALS, Boolean.TRUE, valueColumnMeta.getColumnName()));
        return this;
    }

    // =====================_isNull========================
    protected AbstractClassCriteria<E> _isNull(EntityGetter<E> fieldGetter) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.ISNULL));
        return this;
    }

    // =====================_isNotNull========================
    protected AbstractClassCriteria<E> _isNotNull(EntityGetter<E> fieldGetter) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.ISNOTNULL));
        return this;
    }

    // =====================_between========================

    protected AbstractClassCriteria<E> _between(EntityGetter<E> fieldGetter, Object valueMin, Object valueMax) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.BETWEEN, valueMin, valueMax));
        return this;
    }

    protected AbstractClassCriteria<E> _between(EntityGetter<E> fieldGetter, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        ColumnMeta valueMinColumnMeta = resolveEntityGetter(valueMin);
        ColumnMeta valueMaxColumnMeta = resolveEntityGetter(valueMax);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.BETWEEN, Boolean.TRUE, valueMinColumnMeta.getColumnName(), valueMaxColumnMeta.getColumnName()));
        return this;
    }


    // =====================_notBetween========================

    protected AbstractClassCriteria<E> _notBetween(EntityGetter<E> fieldGetter, Object valueMin, Object valueMax) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.NOTBETWEEN, valueMin, valueMax));
        return this;
    }

    protected AbstractClassCriteria<E> _notBetween(EntityGetter<E> fieldGetter, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        ColumnMeta valueMinColumnMeta = resolveEntityGetter(valueMin);
        ColumnMeta valueMaxColumnMeta = resolveEntityGetter(valueMax);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.NOTBETWEEN, Boolean.TRUE, valueMinColumnMeta.getColumnName(), valueMaxColumnMeta.getColumnName()));
        return this;
    }

    // =====================_contains========================
    protected AbstractClassCriteria<E> _contains(EntityGetter<E> fieldGetter, String sqlValue) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.CONTAINS, sqlValue));
        return this;
    }

    // =====================_notContains========================
    protected AbstractClassCriteria<E> _notContains(EntityGetter<E> fieldGetter, String sqlValue) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.NOTCONTAINS, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _startWith(EntityGetter<E> fieldGetter, String sqlValue) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.STARTWITH, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _notStartWith(EntityGetter<E> fieldGetter, String sqlValue) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.NOTSTARTWITH, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _endWith(EntityGetter<E> fieldGetter, String sqlValue) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.ENDWITH, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _notEndWith(EntityGetter<E> fieldGetter, String sqlValue) {
        ColumnMeta columnMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(columnMeta.getColumnName(), Operator.NOTENDWITH, sqlValue));
        return this;
    }

    private <E extends RootEntity> ColumnMeta resolveEntityGetter(EntityGetter<E> entityGetter) {
        String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(entityGetter);
        return columnMetas.get(fieldName);
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    protected TableMeta getTableMeta() {
        return tableMeta;
    }

    protected Map<String, ColumnMeta> getColumnMetas() {
        return columnMetas;
    }

    protected ComposedCriteriaResolver getComposedCriteriaResolver() {
        return composedCriteriaResolver;
    }
}
