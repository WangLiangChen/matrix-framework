package wang.liangchen.matrix.framework.data.dao.criteria;


import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.function.LambdaUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.pagination.OrderBy;
import wang.liangchen.matrix.framework.data.pagination.OrderByDirection;
import wang.liangchen.matrix.framework.data.pagination.Pagination;

import java.util.*;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class Criteria<E extends RootEntity> extends AbstractCriteria<E> {
    private Set<String> resultColumns;
    private Boolean forUpdate;
    private Integer pageSize;
    private Integer pageNumber;
    private Boolean distinct;
    private Long version;
    private List<OrderBy> orderBys;
    private boolean useCache = true;

    private Criteria(E entity) {
        super(entity);
    }

    private Criteria(Class<E> entityClass) {
        super(entityClass);
    }

    public static <E extends RootEntity> Criteria<E> of(E entity) {
        return new Criteria<E>(entity) {
        };
    }

    public static <E extends RootEntity> Criteria<E> of(Class<E> entityClass) {
        return new Criteria<E>(entityClass) {
        };
    }

    @SafeVarargs
    public final Criteria<E> resultFields(EntityGetter<E>... resultFields) {
        if (null == this.resultColumns) {
            this.resultColumns = new HashSet<>();
        }
        Map<String, ColumnMeta> columnMetas = getTableMeta().getColumnMetas();
        for (EntityGetter<E> resultField : resultFields) {
            String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(resultField);
            String columnName = columnMetas.get(fieldName).getColumnName();
            this.resultColumns.add(columnName);
        }
        return this;
    }

    public final Criteria<E> resultColumns(String... resultColumns) {
        if (null == this.resultColumns) {
            this.resultColumns = new HashSet<>();
        }
        this.resultColumns.addAll(Arrays.asList(resultColumns));
        return this;
    }

    public Criteria<E> distinct() {
        this.distinct = true;
        return this;
    }

    public Criteria<E> forUpdate() {
        this.forUpdate = true;
        return this;
    }

    public Criteria<E> disableCache() {
        this.useCache = false;
        return this;
    }

    public Criteria<E> orderBy(EntityGetter<E> column, OrderByDirection orderByDirection) {
        if (null == this.orderBys) {
            this.orderBys = new ArrayList<>();
        }
        Map<String, ColumnMeta> columnMetas = getTableMeta().getColumnMetas();
        String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(column);
        String columnName = columnMetas.get(fieldName).getColumnName();
        this.orderBys.add(OrderBy.newInstance(columnName, orderByDirection));
        return this;
    }

    public Criteria<E> orderBy(List<OrderBy> orderBys) {
        if (null == this.orderBys) {
            this.orderBys = new ArrayList<>();
        }
        this.orderBys.addAll(orderBys);
        return this;
    }

    public Criteria<E> pageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Criteria<E> pageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public Criteria<E> pagination(Pagination pagination) {
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN, "'pagination' may not be null");
        this.pageNumber = null == this.pageNumber ? pagination.getPageNumber() : this.pageNumber;
        this.pageSize = null == this.pageSize ? pagination.getPageSize() : this.pageSize;
        if (null == orderBys) {
            this.orderBys = new ArrayList<>();
            this.orderBys.addAll(pagination.getOrderBys());
        }
        return this;
    }

    public Criteria<E> version(long version) {
        this.version = version;
        return this;
    }

    @Override
    public Criteria<E> _equals(EntityGetter<E> column) {
        return (Criteria<E>) super._equals(column);
    }

    @Override
    public Criteria<E> _equals(EntityGetter<E> column, Object sqlValue) {
        return (Criteria<E>) super._equals(column, sqlValue);
    }

    @Override
    public Criteria<E> _equals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (Criteria<E>) super._equals(column, sqlValue);
    }

    @Override
    public Criteria<E> _notEquals(EntityGetter<E> column) {
        return (Criteria<E>) super._notEquals(column);
    }

    @Override
    public Criteria<E> _notEquals(EntityGetter<E> column, Object sqlValue) {
        return (Criteria<E>) super._notEquals(column, sqlValue);
    }

    @Override
    public Criteria<E> _notEquals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (Criteria<E>) super._notEquals(column, sqlValue);
    }

    @Override
    public Criteria<E> _in(EntityGetter<E> column, Object... values) {
        return (Criteria<E>) super._in(column, values);
    }

    @Override
    public Criteria<E> _in(EntityGetter<E> column, Collection<?> values) {
        return (Criteria<E>) super._in(column, values);
    }

    @Override
    public Criteria<E> _notIn(EntityGetter<E> column, Object... values) {
        return (Criteria<E>) super._notIn(column, values);
    }

    @Override
    public Criteria<E> _notIn(EntityGetter<E> column, Collection<?> values) {
        return (Criteria<E>) super._notIn(column, values);
    }

    @Override
    public Criteria<E> _greaterThan(EntityGetter<E> column) {
        return (Criteria<E>) super._greaterThan(column);
    }

    @Override
    public Criteria<E> _greaterThan(EntityGetter<E> column, Object sqlValue) {
        return (Criteria<E>) super._greaterThan(column, sqlValue);
    }

    @Override
    public Criteria<E> _greaterThan(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (Criteria<E>) super._greaterThan(column, sqlValue);
    }

    @Override
    public Criteria<E> _greaterThanOrEquals(EntityGetter<E> column) {
        return (Criteria<E>) super._greaterThanOrEquals(column);
    }

    @Override
    public Criteria<E> _greaterThanOrEquals(EntityGetter<E> column, Object sqlValue) {
        return (Criteria<E>) super._greaterThanOrEquals(column, sqlValue);
    }

    @Override
    public Criteria<E> _greaterThanOrEquals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (Criteria<E>) super._greaterThanOrEquals(column, sqlValue);
    }

    @Override
    public Criteria<E> _lessThan(EntityGetter<E> column) {
        return (Criteria<E>) super._lessThan(column);
    }

    @Override
    public Criteria<E> _lessThan(EntityGetter<E> column, Object sqlValue) {
        return (Criteria<E>) super._lessThan(column, sqlValue);
    }

    @Override
    public Criteria<E> _lessThan(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (Criteria<E>) super._lessThan(column, sqlValue);
    }

    @Override
    public Criteria<E> _lessThanOrEquals(EntityGetter<E> column) {
        return (Criteria<E>) super._lessThanOrEquals(column);
    }

    @Override
    public Criteria<E> _lessThanOrEquals(EntityGetter<E> column, Object sqlValue) {
        return (Criteria<E>) super._lessThanOrEquals(column, sqlValue);
    }

    @Override
    public Criteria<E> _lessThanOrEquals(EntityGetter<E> column, EntityGetter<E> sqlValue) {
        return (Criteria<E>) super._lessThanOrEquals(column, sqlValue);
    }

    @Override
    public Criteria<E> _isNull(EntityGetter<E> column) {
        return (Criteria<E>) super._isNull(column);
    }

    @Override
    public Criteria<E> _isNotNull(EntityGetter<E> column) {
        return (Criteria<E>) super._isNotNull(column);
    }

    @Override
    public Criteria<E> _between(EntityGetter<E> column, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (Criteria<E>) super._between(column, valueMin, valueMax);
    }

    @Override
    public Criteria<E> _between(EntityGetter<E> column, Object valueMin, Object valueMax) {
        return (Criteria<E>) super._between(column, valueMin, valueMax);
    }

    @Override
    public Criteria<E> _notBetween(EntityGetter<E> column, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (Criteria<E>) super._notBetween(column, valueMin, valueMax);
    }

    @Override
    public Criteria<E> _notBetween(EntityGetter<E> column, Object valueMin, Object valueMax) {
        return (Criteria<E>) super._notBetween(column, valueMin, valueMax);
    }

    @Override
    public Criteria<E> _contains(EntityGetter<E> column, String sqlValue) {
        return (Criteria<E>) super._contains(column, sqlValue);
    }

    @Override
    public Criteria<E> _notContains(EntityGetter<E> column, String sqlValue) {
        return (Criteria<E>) super._notContains(column, sqlValue);
    }

    @Override
    public Criteria<E> _startWith(EntityGetter<E> column, String sqlValue) {
        return (Criteria<E>) super._startWith(column, sqlValue);
    }

    @Override
    public Criteria<E> _notStartWith(EntityGetter<E> column, String sqlValue) {
        return (Criteria<E>) super._notStartWith(column, sqlValue);
    }

    @Override
    public Criteria<E> _endWith(EntityGetter<E> column, String sqlValue) {
        return (Criteria<E>) super._endWith(column, sqlValue);
    }

    @Override
    public Criteria<E> _notEndWith(EntityGetter<E> column, String sqlValue) {
        return (Criteria<E>) super._notEndWith(column, sqlValue);
    }

    @Override
    public Criteria<E> _OR(SubCriteria<E> subCriteria) {
        return (Criteria<E>) super._OR(subCriteria);
    }

    @Override
    public Criteria<E> _AND(SubCriteria<E> subCriteria) {
        return (Criteria<E>) super._AND(subCriteria);
    }

    public Set<String> getResultColumns() {
        return resultColumns;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Boolean getForUpdate() {
        return forUpdate;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public List<OrderBy> getOrderBys() {
        return orderBys;
    }

    public Long getVersion() {
        return version;
    }

    public boolean isUseCache() {
        return useCache;
    }
}
