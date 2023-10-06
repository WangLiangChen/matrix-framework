package wang.liangchen.matrix.framework.data.dao.criteria;


import wang.liangchen.matrix.framework.commons.function.LambdaUtil;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.pagination.OrderBy;
import wang.liangchen.matrix.framework.data.pagination.OrderByDirection;
import wang.liangchen.matrix.framework.data.pagination.Pagination;

import java.util.*;
import java.util.function.Consumer;

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
        this.resultColumns = null == this.resultColumns ? new HashSet<>() : this.resultColumns;
        Map<String, ColumnMeta> columnMetas = this.getColumnMetas();
        for (EntityGetter<E> resultField : resultFields) {
            String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(resultField);
            String columnName = columnMetas.get(fieldName).getColumnName();
            this.resultColumns.add(columnName);
        }
        return this;
    }

    public final Criteria<E> resultColumns(String... resultColumns) {
        this.resultColumns = null == this.resultColumns ? new HashSet<>() : this.resultColumns;
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

    public Criteria<E> orderBy(EntityGetter<E> fieldGetter, OrderByDirection orderByDirection) {
        this.orderBys = null == this.orderBys ? new ArrayList<>() : this.orderBys;
        Map<String, ColumnMeta> columnMetas = this.getColumnMetas();
        String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(fieldGetter);
        String columnName = columnMetas.get(fieldName).getColumnName();
        this.orderBys.add(OrderBy.newInstance(columnName, orderByDirection));
        return this;
    }

    public Criteria<E> orderBy(List<OrderBy> orderBys) {
        this.orderBys = null == this.orderBys ? new ArrayList<>() : this.orderBys;
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
        if (null == pagination) {
            return this;
        }
        this.pageNumber = null == this.pageNumber ? pagination.getPageNumber() : this.pageNumber;
        this.pageSize = null == this.pageSize ? pagination.getPageSize() : this.pageSize;
        List<OrderBy> paginationOrderBys = pagination.getOrderBys();
        if (null == paginationOrderBys) {
            return this;
        }
        this.orderBys = null == this.orderBys ? new ArrayList<>() : this.orderBys;
        this.orderBys.addAll(paginationOrderBys);
        return this;
    }

    public Criteria<E> version(long version) {
        this.version = version;
        return this;
    }

    @Override
    public Criteria<E> _equals(EntityGetter<E> fieldGetter) {
        return (Criteria<E>) super._equals(fieldGetter);
    }

    @Override
    public Criteria<E> _equals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (Criteria<E>) super._equals(fieldGetter, sqlValue);
    }

    @Override
    protected Criteria<E> _equalsIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>)super._equalsIgnoreCase(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _equals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (Criteria<E>) super._equals(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _notEquals(EntityGetter<E> fieldGetter) {
        return (Criteria<E>) super._notEquals(fieldGetter);
    }

    @Override
    public Criteria<E> _notEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (Criteria<E>) super._notEquals(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _notEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (Criteria<E>) super._notEquals(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _in(EntityGetter<E> fieldGetter, Object... values) {
        return (Criteria<E>) super._in(fieldGetter, values);
    }

    @Override
    public Criteria<E> _in(EntityGetter<E> fieldGetter, Collection<?> values) {
        return (Criteria<E>) super._in(fieldGetter, values);
    }

    @Override
    public Criteria<E> _notIn(EntityGetter<E> fieldGetter, Object... values) {
        return (Criteria<E>) super._notIn(fieldGetter, values);
    }

    @Override
    public Criteria<E> _notIn(EntityGetter<E> fieldGetter, Collection<?> values) {
        return (Criteria<E>) super._notIn(fieldGetter, values);
    }

    @Override
    public Criteria<E> _greaterThan(EntityGetter<E> fieldGetter) {
        return (Criteria<E>) super._greaterThan(fieldGetter);
    }

    @Override
    public Criteria<E> _greaterThan(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (Criteria<E>) super._greaterThan(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _greaterThan(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (Criteria<E>) super._greaterThan(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter) {
        return (Criteria<E>) super._greaterThanOrEquals(fieldGetter);
    }

    @Override
    public Criteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (Criteria<E>) super._greaterThanOrEquals(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (Criteria<E>) super._greaterThanOrEquals(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _lessThan(EntityGetter<E> fieldGetter) {
        return (Criteria<E>) super._lessThan(fieldGetter);
    }

    @Override
    public Criteria<E> _lessThan(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (Criteria<E>) super._lessThan(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _lessThan(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (Criteria<E>) super._lessThan(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter) {
        return (Criteria<E>) super._lessThanOrEquals(fieldGetter);
    }

    @Override
    public Criteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (Criteria<E>) super._lessThanOrEquals(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (Criteria<E>) super._lessThanOrEquals(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _isNull(EntityGetter<E> fieldGetter) {
        return (Criteria<E>) super._isNull(fieldGetter);
    }

    @Override
    public Criteria<E> _isNotNull(EntityGetter<E> fieldGetter) {
        return (Criteria<E>) super._isNotNull(fieldGetter);
    }

    @Override
    public Criteria<E> _between(EntityGetter<E> fieldGetter, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (Criteria<E>) super._between(fieldGetter, valueMin, valueMax);
    }

    @Override
    public Criteria<E> _between(EntityGetter<E> fieldGetter, Object valueMin, Object valueMax) {
        return (Criteria<E>) super._between(fieldGetter, valueMin, valueMax);
    }

    @Override
    public Criteria<E> _notBetween(EntityGetter<E> fieldGetter, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (Criteria<E>) super._notBetween(fieldGetter, valueMin, valueMax);
    }

    @Override
    public Criteria<E> _notBetween(EntityGetter<E> fieldGetter, Object valueMin, Object valueMax) {
        return (Criteria<E>) super._notBetween(fieldGetter, valueMin, valueMax);
    }

    @Override
    public Criteria<E> _contains(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._contains(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _notContains(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._notContains(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _startWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._startWith(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _notStartWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._notStartWith(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _endWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._endWith(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _notEndWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._notEndWith(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _or(Consumer<SubCriteria<E>> consumer) {
        return (Criteria<E>) super._or(consumer);
    }

    @Override
    public Criteria<E> _or() {
        return (Criteria<E>) super._or();
    }

    @Override
    public Criteria<E> _and(Consumer<SubCriteria<E>> consumer) {
        return (Criteria<E>) super._and(consumer);
    }

    protected Set<String> getResultColumns() {
        return resultColumns;
    }

    protected Boolean getDistinct() {
        return distinct;
    }

    protected Boolean getForUpdate() {
        return forUpdate;
    }

    protected Integer getPageSize() {
        return pageSize;
    }

    protected Integer getPageNumber() {
        return pageNumber;
    }

    protected List<OrderBy> getOrderBys() {
        return orderBys;
    }

    protected Long getVersion() {
        return version;
    }

    public boolean isUseCache() {
        return useCache;
    }
}
