package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.function.LambdaUtil;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 * _equals         _notEquals
 * _in             _notIn
 * _greaterThan    _greaterThanOrEquals
 * _lessThan       _lessThanOrEquals
 * _isNull         _isNotNull
 * _between        _notBetween
 * _startWith      _notStartWith
 * _endWith        _notEndWith
 * _contains       _notContains
 */
abstract class AbstractCriteria<E extends RootEntity> {
    private final List<CriteriaMeta<E>> CRITERIAMETAS = new ArrayList<CriteriaMeta<E>>() {
        @Override
        public boolean add(CriteriaMeta<E> criteriaMeta) {
            if (null == criteriaMeta) {
                return false;
            }
            return super.add(criteriaMeta);
        }
    };

    private final E entity;
    private final Class<E> entityClass;
    private final TableMeta tableMeta;
    private final Map<String, ColumnMeta> columnMetas;
    private boolean ignoreStringBlank = false;


    @SuppressWarnings("unchecked")
    protected AbstractCriteria(E entity) {
        this.entity = ValidationUtil.INSTANCE.notNull(entity);
        this.entityClass = (Class<E>) entity.getClass();
        tableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
        this.columnMetas = tableMeta.getColumnMetas();
    }

    protected AbstractCriteria(Class<E> entityClass) {
        this.entity = null;
        this.entityClass = entityClass;
        tableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
        this.columnMetas = tableMeta.getColumnMetas();
    }

    protected AbstractCriteria<E> ignoreStringBlank() {
        this.ignoreStringBlank = true;
        return this;
    }

    // =====================equals========================
    protected AbstractCriteria<E> _equals(EntityGetter<E> fieldGetter) {
        addCriteriaMeta(Operator.EQUALS, fieldGetter);
        return this;
    }

    protected AbstractCriteria<E> _equals(EntityGetter<E> fieldGetter, Object sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.EQUALS, resoveEntityGetter(fieldGetter), sqlValue));
        return this;
    }

    protected AbstractCriteria<E> _equals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.EQUALS, resoveEntityGetter(fieldGetter), sqlValue));
        return this;
    }

    // =====================notEquals========================
    protected AbstractCriteria<E> _notEquals(EntityGetter<E> fieldGetter) {
        addCriteriaMeta(Operator.NOTEQUALS, fieldGetter);
        return this;
    }

    protected AbstractCriteria<E> _notEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.NOTEQUALS, resoveEntityGetter(fieldGetter), sqlValue));
        return this;
    }

    protected AbstractCriteria<E> _notEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.NOTEQUALS, resoveEntityGetter(fieldGetter), sqlValue));
        return this;
    }

    // =====================in========================
    protected AbstractCriteria<E> _in(EntityGetter<E> fieldGetter, Object... values) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.IN, resoveEntityGetter(fieldGetter), values));
        return this;
    }

    protected AbstractCriteria<E> _in(EntityGetter<E> fieldGetter, Collection<?> values) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.IN, resoveEntityGetter(fieldGetter), values));
        return this;
    }

    // =====================notIn========================
    protected AbstractCriteria<E> _notIn(EntityGetter<E> fieldGetter, Object... values) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.NOTIN, resoveEntityGetter(fieldGetter), values));
        return this;
    }

    protected AbstractCriteria<E> _notIn(EntityGetter<E> fieldGetter, Collection<?> values) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.NOTIN, resoveEntityGetter(fieldGetter), values));
        return this;
    }

    // =====================greaterThan========================
    protected AbstractCriteria<E> _greaterThan(EntityGetter<E> fieldGetter) {
        addCriteriaMeta(Operator.GREATERTHAN, fieldGetter);
        return this;
    }

    protected AbstractCriteria<E> _greaterThan(EntityGetter<E> fieldGetter, Object sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.GREATERTHAN, resoveEntityGetter(fieldGetter), sqlValue));
        return this;
    }

    protected AbstractCriteria<E> _greaterThan(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.GREATERTHAN, resoveEntityGetter(fieldGetter), sqlValue));
        return this;
    }

    // =====================greaterThanOrEauals========================
    protected AbstractCriteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter) {
        addCriteriaMeta(Operator.GREATERTHAN_OR_EQUALS, fieldGetter);
        return this;
    }

    protected AbstractCriteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.GREATERTHAN_OR_EQUALS, resoveEntityGetter(fieldGetter), sqlValue));
        return this;
    }

    protected AbstractCriteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.GREATERTHAN_OR_EQUALS, resoveEntityGetter(fieldGetter), sqlValue));
        return this;
    }

    // =====================_lessThan========================
    protected AbstractCriteria<E> _lessThan(EntityGetter<E> fieldGetter) {
        addCriteriaMeta(Operator.LESSTHAN, fieldGetter);
        return this;
    }

    protected AbstractCriteria<E> _lessThan(EntityGetter<E> fieldGetter, Object sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.LESSTHAN, resoveEntityGetter(fieldGetter), sqlValue));
        return this;
    }

    protected AbstractCriteria<E> _lessThan(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.LESSTHAN, resoveEntityGetter(fieldGetter), sqlValue));
        return this;
    }

    // =====================-========================
    protected AbstractCriteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter) {
        addCriteriaMeta(Operator.LESSTHAN_OR_EQUALS, fieldGetter);
        return this;
    }

    protected AbstractCriteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.LESSTHAN_OR_EQUALS, resoveEntityGetter(fieldGetter), sqlValue));
        return this;
    }

    protected AbstractCriteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.LESSTHAN_OR_EQUALS, resoveEntityGetter(fieldGetter), sqlValue));
        return this;
    }

    // =====================_isNull========================
    protected AbstractCriteria<E> _isNull(EntityGetter<E> fieldGetter) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.ISNULL, resoveEntityGetter(fieldGetter)));
        return this;
    }

    // =====================_isNotNull========================
    protected AbstractCriteria<E> _isNotNull(EntityGetter<E> fieldGetter) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.ISNOTNULL, resoveEntityGetter(fieldGetter)));
        return this;
    }

    // =====================_between========================
    protected AbstractCriteria<E> _between(EntityGetter<E> fieldGetter, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.BETWEEN, resoveEntityGetter(fieldGetter), valueMin, valueMax));
        return this;
    }

    protected AbstractCriteria<E> _between(EntityGetter<E> fieldGetter, Object valueMin, Object valueMax) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.BETWEEN, resoveEntityGetter(fieldGetter), valueMin, valueMax));
        return this;
    }


    // =====================_notBetween========================
    protected AbstractCriteria<E> _notBetween(EntityGetter<E> fieldGetter, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.NOTBETWEEN, resoveEntityGetter(fieldGetter), valueMin, valueMax));
        return this;
    }

    protected AbstractCriteria<E> _notBetween(EntityGetter<E> fieldGetter, Object valueMin, Object valueMax) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.NOTBETWEEN, resoveEntityGetter(fieldGetter), valueMin, valueMax));
        return this;
    }

    // =====================_contains========================
    protected AbstractCriteria<E> _contains(EntityGetter<E> fieldGetter, String sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.CONTAINS, resoveEntityGetter(fieldGetter), sqlValue));
        return this;
    }

    // =====================_notContains========================
    protected AbstractCriteria<E> _notContains(EntityGetter<E> fieldGetter, String sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.NOTCONTAINS, resoveEntityGetter(fieldGetter), sqlValue));
        return this;
    }

    protected AbstractCriteria<E> _startWith(EntityGetter<E> fieldGetter, String sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.STARTWITH, resoveEntityGetter(fieldGetter), sqlValue));
        return this;
    }

    protected AbstractCriteria<E> _notStartWith(EntityGetter<E> fieldGetter, String sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.NOTSTARTWITH, resoveEntityGetter(fieldGetter), sqlValue));
        return this;
    }

    protected AbstractCriteria<E> _endWith(EntityGetter<E> fieldGetter, String sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.ENDWITH, resoveEntityGetter(fieldGetter), sqlValue));
        return this;
    }

    protected AbstractCriteria<E> _notEndWith(EntityGetter<E> fieldGetter, String sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, Operator.NOTENDWITH, resoveEntityGetter(fieldGetter), sqlValue));
        return this;
    }

    protected List<CriteriaMeta<E>> getCRITERIAMETAS() {
        return CRITERIAMETAS;
    }


    public E getEntity() {
        return entity;
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

    private <E extends RootEntity> ColumnMeta resoveEntityGetter(EntityGetter<E> entityGetter) {
        String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(entityGetter);
        return columnMetas.get(fieldName);
    }

    private void addCriteriaMeta(Operator operator, EntityGetter<E> fieldGetter) {
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN, entity, "Unsupported call,entity must not be null");
        String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(fieldGetter);
        String getterName = StringUtil.INSTANCE.getGetter(fieldName);
        Object fieldValue = ClassUtil.INSTANCE.invokeGetter(entity, getterName);
        ColumnMeta columnMeta = columnMetas.get(fieldName);
        CRITERIAMETAS.add(CriteriaMeta.getInstance(this, operator, columnMeta, fieldValue));
    }
}
