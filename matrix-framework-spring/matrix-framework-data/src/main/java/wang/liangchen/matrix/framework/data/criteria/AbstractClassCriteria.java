package wang.liangchen.matrix.framework.data.criteria;

import wang.liangchen.matrix.framework.commons.function.LambdaUtil;
import wang.liangchen.matrix.framework.data.entity.RootEntity;
import wang.liangchen.matrix.framework.data.resolver.EntityGetter;
import wang.liangchen.matrix.framework.data.resolver.EntityMeta;
import wang.liangchen.matrix.framework.data.resolver.EntityResolver;
import wang.liangchen.matrix.framework.data.resolver.FieldMeta;

import java.util.Collection;
import java.util.Map;

/**
 * @author Liangchen.Wang 2023-04-10 19:05
 */
abstract class AbstractClassCriteria<E extends RootEntity> {
    private final ComposedCriteriaResolver composedCriteriaResolver;
    private final Class<E> entityClass;
    private final EntityMeta entityMeta;
    private final Map<String, FieldMeta> fieldMetas;
    private final FieldMeta softDeleteFieldMeta;
    private final FieldMeta versionFieldMeta;

    protected AbstractClassCriteria(Class<E> entityClass, AndOr andOr) {
        this.entityClass = entityClass;
        this.composedCriteriaResolver = ComposedCriteriaResolver.newInstance(andOr);
        this.entityMeta = EntityResolver.INSTANCE.resolveEntity(entityClass);
        this.fieldMetas = this.entityMeta.getFieldMetas();
        this.softDeleteFieldMeta = this.entityMeta.getSoftDeleteFieldMeta();
        this.versionFieldMeta = this.entityMeta.getVersionFieldMeta();
    }

    protected AbstractClassCriteria(Class<E> entityClass) {
        this(entityClass, AndOr.and);
    }

    //--------------------------------start criteria--------------------------------------------------//
    // =====================equals========================
    protected AbstractClassCriteria<E> _equals(EntityGetter<E> fieldGetter, Object sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(fieldMeta.getColumnName(), Operator.EQUALS, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _equalsIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstanceIgnoreCase(fieldMeta.getColumnName(), Operator.EQUALS, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _equals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        FieldMeta valueFieldMeta = resolveEntityGetter(sqlValue);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstanceValuesIsColumnName(fieldMeta.getColumnName(), Operator.EQUALS, valueFieldMeta.getColumnName()));
        return this;
    }

    // =====================notEquals========================

    protected AbstractClassCriteria<E> _notEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(fieldMeta.getColumnName(), Operator.NOTEQUALS, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _notEqualsIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstanceIgnoreCase(fieldMeta.getColumnName(), Operator.NOTEQUALS, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _notEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        FieldMeta valueFieldMeta = resolveEntityGetter(sqlValue);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstanceValuesIsColumnName(fieldMeta.getColumnName(), Operator.NOTEQUALS, valueFieldMeta.getColumnName()));
        return this;
    }

    // =====================in========================
    protected AbstractClassCriteria<E> _in(EntityGetter<E> fieldGetter, Object... sqlValues) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(fieldMeta.getColumnName(), Operator.IN, sqlValues));
        return this;
    }

    protected AbstractClassCriteria<E> _in(EntityGetter<E> fieldGetter, Collection<?> sqlValues) {
        _in(fieldGetter, sqlValues.toArray());
        return this;
    }

    // =====================notIn========================
    protected AbstractClassCriteria<E> _notIn(EntityGetter<E> fieldGetter, Object... sqlValues) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(fieldMeta.getColumnName(), Operator.NOTIN, sqlValues));
        return this;
    }

    protected AbstractClassCriteria<E> _notIn(EntityGetter<E> fieldGetter, Collection<?> sqlValues) {
        _notIn(fieldGetter, sqlValues.toArray());
        return this;
    }

    // =====================greaterThan========================


    protected AbstractClassCriteria<E> _greaterThan(EntityGetter<E> fieldGetter, Object sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(fieldMeta.getColumnName(), Operator.GREATERTHAN, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _greaterThan(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        FieldMeta valueFieldMeta = resolveEntityGetter(sqlValue);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstanceValuesIsColumnName(fieldMeta.getColumnName(), Operator.GREATERTHAN, valueFieldMeta.getColumnName()));
        return this;
    }

    // =====================greaterThanOrEquals========================


    protected AbstractClassCriteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(fieldMeta.getColumnName(), Operator.GREATERTHAN_OR_EQUALS, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        FieldMeta valueFieldMeta = resolveEntityGetter(sqlValue);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstanceValuesIsColumnName(fieldMeta.getColumnName(), Operator.GREATERTHAN_OR_EQUALS, valueFieldMeta.getColumnName()));
        return this;
    }

    // =====================_lessThan========================


    protected AbstractClassCriteria<E> _lessThan(EntityGetter<E> fieldGetter, Object sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(fieldMeta.getColumnName(), Operator.LESSTHAN, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _lessThan(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        FieldMeta valueFieldMeta = resolveEntityGetter(sqlValue);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstanceValuesIsColumnName(fieldMeta.getColumnName(), Operator.LESSTHAN, valueFieldMeta.getColumnName()));
        return this;
    }

    // =====================_lessThanOrEquals========================

    protected AbstractClassCriteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(fieldMeta.getColumnName(), Operator.LESSTHAN_OR_EQUALS, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        FieldMeta valueFieldMeta = resolveEntityGetter(sqlValue);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstanceValuesIsColumnName(fieldMeta.getColumnName(), Operator.LESSTHAN_OR_EQUALS, valueFieldMeta.getColumnName()));
        return this;
    }

    // =====================_isNull========================
    protected AbstractClassCriteria<E> _isNull(EntityGetter<E> fieldGetter) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(fieldMeta.getColumnName(), Operator.ISNULL));
        return this;
    }

    // =====================_isNotNull========================
    protected AbstractClassCriteria<E> _isNotNull(EntityGetter<E> fieldGetter) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(fieldMeta.getColumnName(), Operator.ISNOTNULL));
        return this;
    }

    // =====================_between========================

    protected AbstractClassCriteria<E> _between(EntityGetter<E> fieldGetter, Object valueMin, Object valueMax) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(fieldMeta.getColumnName(), Operator.BETWEEN, valueMin, valueMax));
        return this;
    }

    protected AbstractClassCriteria<E> _between(EntityGetter<E> fieldGetter, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        FieldMeta valueMinFieldMeta = resolveEntityGetter(valueMin);
        FieldMeta valueMaxFieldMeta = resolveEntityGetter(valueMax);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstanceValuesIsColumnName(fieldMeta.getColumnName(), Operator.BETWEEN, valueMinFieldMeta.getColumnName(), valueMaxFieldMeta.getColumnName()));
        return this;
    }


    // =====================_notBetween========================

    protected AbstractClassCriteria<E> _notBetween(EntityGetter<E> fieldGetter, Object valueMin, Object valueMax) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(fieldMeta.getColumnName(), Operator.NOTBETWEEN, valueMin, valueMax));
        return this;
    }

    protected AbstractClassCriteria<E> _notBetween(EntityGetter<E> fieldGetter, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        FieldMeta valueMinFieldMeta = resolveEntityGetter(valueMin);
        FieldMeta valueMaxFieldMeta = resolveEntityGetter(valueMax);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstanceValuesIsColumnName(fieldMeta.getColumnName(), Operator.NOTBETWEEN, valueMinFieldMeta.getColumnName(), valueMaxFieldMeta.getColumnName()));
        return this;
    }

    // =====================_contains========================
    protected AbstractClassCriteria<E> _contains(EntityGetter<E> fieldGetter, String sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(fieldMeta.getColumnName(), Operator.CONTAINS, sqlValue));
        return this;
    }

    // =====================_contains========================
    protected AbstractClassCriteria<E> _containsIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstanceIgnoreCase(fieldMeta.getColumnName(), Operator.CONTAINS, sqlValue));
        return this;
    }

    // =====================_notContains========================
    protected AbstractClassCriteria<E> _notContains(EntityGetter<E> fieldGetter, String sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(fieldMeta.getColumnName(), Operator.NOTCONTAINS, sqlValue));
        return this;
    }

    // =====================_notContains========================
    protected AbstractClassCriteria<E> _notContainsIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstanceIgnoreCase(fieldMeta.getColumnName(), Operator.NOTCONTAINS, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _startWith(EntityGetter<E> fieldGetter, String sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(fieldMeta.getColumnName(), Operator.STARTWITH, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _startWithIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstanceIgnoreCase(fieldMeta.getColumnName(), Operator.STARTWITH, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _notStartWith(EntityGetter<E> fieldGetter, String sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(fieldMeta.getColumnName(), Operator.NOTSTARTWITH, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _notStartWithIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstanceIgnoreCase(fieldMeta.getColumnName(), Operator.NOTSTARTWITH, sqlValue));
        return this;
    }


    protected AbstractClassCriteria<E> _endWith(EntityGetter<E> fieldGetter, String sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(fieldMeta.getColumnName(), Operator.ENDWITH, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _endWithIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstanceIgnoreCase(fieldMeta.getColumnName(), Operator.ENDWITH, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _notEndWith(EntityGetter<E> fieldGetter, String sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstance(fieldMeta.getColumnName(), Operator.NOTENDWITH, sqlValue));
        return this;
    }

    protected AbstractClassCriteria<E> _notEndWithIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        FieldMeta fieldMeta = resolveEntityGetter(fieldGetter);
        this.composedCriteriaResolver.add(SingleCriteriaResolver.newInstanceIgnoreCase(fieldMeta.getColumnName(), Operator.NOTENDWITH, sqlValue));
        return this;
    }

    //--------------------------------start criteria--------------------------------------------------//
    protected FieldMeta resolveEntityGetter(EntityGetter<E> entityGetter) {
        String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(entityGetter);
        return fieldMetas.get(fieldName);
    }

    protected String resolveColumnName(EntityGetter<E> entityGetter) {
        return resolveEntityGetter(entityGetter).getColumnName();
    }

    protected ComposedCriteriaResolver getComposedCriteriaResolver() {
        return composedCriteriaResolver;
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    protected EntityMeta getEntityMeta() {
        return entityMeta;
    }

    protected Map<String, FieldMeta> getFieldMetas() {
        return fieldMetas;
    }

    protected FieldMeta getSoftDeleteFieldMeta() {
        return softDeleteFieldMeta;
    }

    protected FieldMeta getVersionFieldMeta() {
        return versionFieldMeta;
    }
}
