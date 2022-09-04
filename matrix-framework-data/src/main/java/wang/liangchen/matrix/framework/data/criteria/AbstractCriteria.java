package wang.liangchen.matrix.framework.data.criteria;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.data.dao.criteria.EntityGetter;
import wang.liangchen.matrix.framework.data.dao.criteria.Operator;
import wang.liangchen.matrix.framework.data.dao.criteria.TableMeta;
import wang.liangchen.matrix.framework.data.dao.criteria.TableMetas;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.ArrayList;
import java.util.List;

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
public abstract class AbstractCriteria<E extends RootEntity> {
    private final List<CriteriaMeta<E>> CRITERIAMETAS = new ArrayList<>();
    private final List<AbstractCriteria<E>> ORS = new ArrayList<>();
    private final List<AbstractCriteria<E>> ANDS = new ArrayList<>();

    private final E entity;
    private final Class<E> entityClass;
    private final TableMeta tableMeta;


    @SuppressWarnings("unchecked")
    protected AbstractCriteria(E entity) {
        this.entity = entity;
        this.entityClass = (Class<E>) entity.getClass();
        tableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
    }

    protected AbstractCriteria(Class<E> entityClass) {
        this.entity = null;
        this.entityClass = entityClass;
        tableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
    }

    // =====================equals========================
    public AbstractCriteria<E> _equals(EntityGetter<E> column) {
        Assert.INSTANCE.notNull(entity, "Unsupported call,entity can't be null");
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.EQUALS, column, entityClass));
        return this;
    }

    public AbstractCriteria<E> _equals(EntityGetter<E> column, Object sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.EQUALS, column, sqlValue));
        return this;
    }

    public AbstractCriteria<E> _equals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.EQUALS, column, sqlValue));
        return this;
    }

    // =====================notEquals========================
    public AbstractCriteria<E> _notEquals(EntityGetter<E> column) {
        Assert.INSTANCE.notNull(entity, "Unsupported call,entity can't be null");
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.NOTEQUALS, column, entityClass));
        return this;
    }

    public AbstractCriteria<E> _notEquals(EntityGetter<E> column, Object sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.NOTEQUALS, column, sqlValue));
        return this;
    }

    public AbstractCriteria<E> _notEquals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.NOTEQUALS, column, sqlValue));
        return this;
    }

    // =====================in========================
    public AbstractCriteria<E> _in(EntityGetter<E> column, Object... values) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.IN, column, values));
        return this;
    }

    // =====================notIn========================
    public AbstractCriteria<E> _notIn(EntityGetter<E> column, Object... values) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.NOTIN, column, values));
        return this;
    }

    // =====================greaterThan========================
    public AbstractCriteria<E> _greaterThan(EntityGetter<E> column) {
        Assert.INSTANCE.notNull(entity, "Unsupported call,entity can't be null");
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.GREATERTHAN, column, entityClass));
        return this;
    }

    public AbstractCriteria<E> _greaterThan(EntityGetter<E> column, Object sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.GREATERTHAN, column, sqlValue));
        return this;
    }

    public AbstractCriteria<E> _greaterThan(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.GREATERTHAN, column, sqlValue));
        return this;
    }

    // =====================greaterThanOrEauals========================
    public AbstractCriteria<E> _greaterThanOrEquals(EntityGetter<E> column) {
        Assert.INSTANCE.notNull(entity, "Unsupported call,entity can't be null");
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.GREATERTHAN_OR_EQUALS, column, entityClass));
        return this;
    }

    public AbstractCriteria<E> _greaterThanOrEquals(EntityGetter<E> column, Object sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.GREATERTHAN_OR_EQUALS, column, sqlValue));
        return this;
    }

    public AbstractCriteria<E> _greaterThanOrEquals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.GREATERTHAN_OR_EQUALS, column, sqlValue));
        return this;
    }

    // =====================_lessThan========================
    public AbstractCriteria<E> _lessThan(EntityGetter<E> column) {
        Assert.INSTANCE.notNull(entity, "Unsupported call,entity can't be null");
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.LESSTHAN, column, entityClass));
        return this;
    }

    public AbstractCriteria<E> _lessThan(EntityGetter<E> column, Object sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.LESSTHAN, column, sqlValue));
        return this;
    }

    public AbstractCriteria<E> _lessThan(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.LESSTHAN, column, sqlValue));
        return this;
    }

    // =====================-========================
    public AbstractCriteria<E> _lessThanOrEquals(EntityGetter<E> column) {
        Assert.INSTANCE.notNull(entity, "Unsupported call,entity can't be null");
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.LESSTHAN_OR_EQUALS, column, entityClass));
        return this;
    }

    public AbstractCriteria<E> _lessThanOrEquals(EntityGetter<E> column, Object sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.LESSTHAN_OR_EQUALS, column, sqlValue));
        return this;
    }

    public AbstractCriteria<E> _lessThanOrEquals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.LESSTHAN_OR_EQUALS, column, sqlValue));
        return this;
    }

    // =====================_isNull========================
    public AbstractCriteria<E> _isNull(EntityGetter<E> column) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.ISNULL, column));
        return this;
    }

    // =====================_isNotNull========================
    public AbstractCriteria<E> _isNotNull(EntityGetter<E> column) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.ISNOTNULL, column));
        return this;
    }

    // =====================_between========================
    public AbstractCriteria<E> _between(EntityGetter<E> column, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.BETWEEN, column, valueMin, valueMax));
        return this;
    }

    public AbstractCriteria<E> _between(EntityGetter<E> column, Object valueMin, Object valueMax) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.BETWEEN, column, valueMin, valueMax));
        return this;
    }


    // =====================_notBetween========================
    public AbstractCriteria<E> _notBetween(EntityGetter<E> column, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.NOTBETWEEN, column, valueMin, valueMax));
        return this;
    }

    public AbstractCriteria<E> _notBetween(EntityGetter<E> column, Object valueMin, Object valueMax) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.NOTBETWEEN, column, valueMin, valueMax));
        return this;
    }

    // =====================_contains========================
    public AbstractCriteria<E> _contains(EntityGetter<E> column, String sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.CONTAINS, column, sqlValue));
        return this;
    }

    // =====================_notContains========================
    public AbstractCriteria<E> _notContains(EntityGetter<E> column, String sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.NOTCONTAINS, column, sqlValue));
        return this;
    }

    public AbstractCriteria<E> _startWith(EntityGetter<E> column, String sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.STARTWITH, column, sqlValue));
        return this;
    }

    public AbstractCriteria<E> _notStartWith(EntityGetter<E> column, String sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.NOTSTARTWITH, column, sqlValue));
        return this;
    }

    public AbstractCriteria<E> _endWith(EntityGetter<E> column, String sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.ENDWITH, column, sqlValue));
        return this;
    }

    public AbstractCriteria<E> _notEndWith(EntityGetter<E> column, String sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.NOTENDWITH, column, sqlValue));
        return this;
    }

    public AbstractCriteria<E> _OR(SubCriteria<E> subCriteria) {
        ORS.add(subCriteria);
        return this;
    }

    public AbstractCriteria<E> _AND(SubCriteria<E> subCriteria) {
        ANDS.add(subCriteria);
        return this;
    }


    protected List<CriteriaMeta<E>> getCRITERIAMETAS() {
        return CRITERIAMETAS;
    }

    protected List<AbstractCriteria<E>> getORS() {
        return ORS;
    }

    protected List<AbstractCriteria<E>> getANDS() {
        return ANDS;
    }

    public E getEntity() {
        return entity;
    }

    protected Class<E> getEntityClass() {
        return entityClass;
    }

    protected TableMeta getTableMeta() {
        return tableMeta;
    }
}
