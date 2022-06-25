package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 * _equals         _notEquals
 * _greaterThan    _greaterThanOrEquals
 * _lessThan       _lessThanOrEquals
 * _in             _notIn
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

    private final Class<E> entityClass;
    private final TableMeta tableMeta;

    protected AbstractCriteria(Class<E> entityClass) {
        this.entityClass = entityClass;
        tableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
    }

    protected AbstractCriteria<E> _equals(EntityGetter<E> column, SqlValue sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.EQUALS, column, sqlValue));
        return this;
    }

    protected AbstractCriteria<E> _notEquals(EntityGetter<E> column, SqlValue sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.NOTEQUALS, column, sqlValue));
        return this;
    }

    protected AbstractCriteria<E> _greaterThan(EntityGetter<E> column, SqlValue sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.GREATERTHAN, column, sqlValue));
        return this;
    }

    protected AbstractCriteria<E> _greaterThanOrEquals(EntityGetter<E> column, SqlValue sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.GREATERTHAN_OR_EQUALS, column, sqlValue));
        return this;
    }

    protected AbstractCriteria<E> _lessThan(EntityGetter<E> column, SqlValue sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.LESSTHAN, column, sqlValue));
        return this;
    }

    protected AbstractCriteria<E> _lessThanOrEquals(EntityGetter<E> column, SqlValue sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.LESSTHAN_OR_EQUALS, column, sqlValue));
        return this;
    }

    protected AbstractCriteria<E> _isNull(EntityGetter<E> column) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.ISNULL, column));
        return this;
    }

    protected AbstractCriteria<E> _isNotNull(EntityGetter<E> column) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.ISNOTNULL, column));
        return this;
    }

    protected AbstractCriteria<E> _in(EntityGetter<E> column, SqlValue... values) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.IN, column, values));
        return this;
    }

    protected AbstractCriteria<E> _notIn(EntityGetter<E> column, SqlValue... values) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.NOTIN, column, values));
        return this;
    }

    protected AbstractCriteria<E> _between(EntityGetter<E> column, SqlValue valueMin, SqlValue valueMax) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.BETWEEN, column, valueMin, valueMax));
        return this;
    }

    protected AbstractCriteria<E> _notBetween(EntityGetter<E> column, SqlValue valueMin, SqlValue valueMax) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.NOTBETWEEN, column, valueMin, valueMax));
        return this;
    }

    protected AbstractCriteria<E> _contains(EntityGetter<E> column, String value) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.CONTAINS, column, SqlValue.of(value)));
        return this;
    }

    protected AbstractCriteria<E> _notContains(EntityGetter<E> column, String value) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.NOTCONTAINS, column, SqlValue.of(value)));
        return this;
    }

    protected AbstractCriteria<E> _startWith(EntityGetter<E> column, String value) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.STARTWITH, column, SqlValue.of(value)));
        return this;
    }

    protected AbstractCriteria<E> _notStartWith(EntityGetter<E> column, String value) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.NOTSTARTWITH, column, SqlValue.of(value)));
        return this;
    }

    protected AbstractCriteria<E> _endWith(EntityGetter<E> column, String value) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.ENDWITH, column, SqlValue.of(value)));
        return this;
    }

    protected AbstractCriteria<E> _notEndWith(EntityGetter<E> column, String value) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.NOTENDWITH, column, SqlValue.of(value)));
        return this;
    }

    protected AbstractCriteria<E> _OR(SubCriteria<E> subCriteria) {
        ORS.add(subCriteria);
        return this;
    }

    protected AbstractCriteria<E> _AND(SubCriteria<E> subCriteria) {
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

    protected Class<E> getEntityClass() {
        return entityClass;
    }

    protected TableMeta getTableMeta() {
        return tableMeta;
    }
}
