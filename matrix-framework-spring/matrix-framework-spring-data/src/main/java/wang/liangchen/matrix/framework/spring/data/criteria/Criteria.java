package wang.liangchen.matrix.framework.spring.data.criteria;


import wang.liangchen.matrix.framework.commons.function.LambdaUtil;
import wang.liangchen.matrix.framework.commons.object.EnhancedList;
import wang.liangchen.matrix.framework.spring.data.entity.RootEntity;
import wang.liangchen.matrix.framework.spring.data.pagination.OrderBy;
import wang.liangchen.matrix.framework.spring.data.pagination.OrderByDirection;
import wang.liangchen.matrix.framework.spring.data.pagination.Pagination;
import wang.liangchen.matrix.framework.spring.data.resolver.EntityGetter;
import wang.liangchen.matrix.framework.spring.data.resolver.FieldMeta;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class Criteria<E extends RootEntity> extends AbstractCriteria<E> {
    private final List<String> selectColumns = new EnhancedList<>();
    private final List<OrderBy> orderBys = new ArrayList<>();
    private Integer pageSize;
    private Integer pageNumber;
    private Boolean distinct;
    private Long version;
    private boolean useCache = true;
    private Boolean forUpdate;

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

    public final Criteria<E> selectColumns(Collection<String> selectColumns) {
        this.selectColumns.addAll(selectColumns);
        return this;
    }

    public final Criteria<E> selectColumns(String... selectColumns) {
        return selectColumns(Arrays.asList(selectColumns));
    }

    public final Criteria<E> selectFields(Collection<EntityGetter<E>> selectFields) {
        List<String> selectColumns = selectFields.stream().map(this::resolveColumnName).collect(Collectors.toList());
        return selectColumns(selectColumns);
    }

    @SafeVarargs
    public final Criteria<E> selectFields(EntityGetter<E>... selectFields) {
        return selectFields(Arrays.asList(selectFields));
    }

    public Criteria<E> orderBy(Collection<OrderBy> orderBys) {
        this.orderBys.addAll(orderBys);
        return this;
    }

    public Criteria<E> orderBy(String columnName, OrderByDirection orderByDirection) {
        this.orderBys.add(new OrderBy(columnName, orderByDirection));
        return this;

    }

    public Criteria<E> orderBy(EntityGetter<E> fieldGetter, OrderByDirection orderByDirection) {
        Map<String, FieldMeta> fieldMetas = this.getFieldMetas();
        String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(fieldGetter);
        String columnName = fieldMetas.get(fieldName).getColumnName();
        return orderBy(columnName, orderByDirection);
    }

    public Criteria<E> pageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Criteria<E> pageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public Criteria<E> distinct() {
        this.distinct = true;
        return this;
    }

    public Criteria<E> version(long version) {
        this.version = version;
        return this;
    }

    public Criteria<E> disableCache() {
        this.useCache = false;
        return this;
    }

    public Criteria<E> forUpdate() {
        this.forUpdate = true;
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
        return orderBy(paginationOrderBys);
    }


    //--------------------------------start criteria--------------------------------------------------//

    @Override
    public Criteria<E> _equals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (Criteria<E>) super._equals(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _equalsIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._equalsIgnoreCase(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _equals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (Criteria<E>) super._equals(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _notEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (Criteria<E>) super._notEquals(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _notEqualsIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._notEqualsIgnoreCase(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _notEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (Criteria<E>) super._notEquals(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _in(EntityGetter<E> fieldGetter, Object... sqlValues) {
        return (Criteria<E>) super._in(fieldGetter, sqlValues);
    }

    @Override
    public Criteria<E> _in(EntityGetter<E> fieldGetter, Collection<?> sqlValues) {
        return (Criteria<E>) super._in(fieldGetter, sqlValues);
    }

    @Override
    public Criteria<E> _notIn(EntityGetter<E> fieldGetter, Object... sqlValues) {
        return (Criteria<E>) super._notIn(fieldGetter, sqlValues);
    }

    @Override
    public Criteria<E> _notIn(EntityGetter<E> fieldGetter, Collection<?> sqlValues) {
        return (Criteria<E>) super._notIn(fieldGetter, sqlValues);
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
    public Criteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter, Object sqlValue) {
        return (Criteria<E>) super._greaterThanOrEquals(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter, EntityGetter<E> sqlValue) {
        return (Criteria<E>) super._greaterThanOrEquals(fieldGetter, sqlValue);
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
    public Criteria<E> _between(EntityGetter<E> fieldGetter, Object valueMin, Object valueMax) {
        return (Criteria<E>) super._between(fieldGetter, valueMin, valueMax);
    }

    @Override
    public Criteria<E> _between(EntityGetter<E> fieldGetter, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (Criteria<E>) super._between(fieldGetter, valueMin, valueMax);
    }

    @Override
    public Criteria<E> _notBetween(EntityGetter<E> fieldGetter, Object valueMin, Object valueMax) {
        return (Criteria<E>) super._notBetween(fieldGetter, valueMin, valueMax);
    }

    @Override
    public Criteria<E> _notBetween(EntityGetter<E> fieldGetter, EntityGetter<E> valueMin, EntityGetter<E> valueMax) {
        return (Criteria<E>) super._notBetween(fieldGetter, valueMin, valueMax);
    }

    @Override
    public Criteria<E> _contains(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._contains(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _containsIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._containsIgnoreCase(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _notContains(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._notContains(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _notContainsIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._notContainsIgnoreCase(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _startWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._startWith(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _startWithIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._startWithIgnoreCase(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _notStartWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._notStartWith(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _notStartWithIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._notStartWithIgnoreCase(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _endWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._endWith(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _endWithIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._endWithIgnoreCase(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _notEndWith(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._notEndWith(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _notEndWithIgnoreCase(EntityGetter<E> fieldGetter, String sqlValue) {
        return (Criteria<E>) super._notEndWithIgnoreCase(fieldGetter, sqlValue);
    }

    @Override
    public Criteria<E> _equals(EntityGetter<E> fieldGetter) {
        return (Criteria<E>) super._equals(fieldGetter);
    }

    @Override
    public Criteria<E> _notEquals(EntityGetter<E> fieldGetter) {
        return (Criteria<E>) super._notEquals(fieldGetter);
    }

    @Override
    public Criteria<E> _greaterThan(EntityGetter<E> fieldGetter) {
        return (Criteria<E>) super._greaterThan(fieldGetter);
    }

    @Override
    public Criteria<E> _greaterThanOrEquals(EntityGetter<E> fieldGetter) {
        return (Criteria<E>) super._greaterThanOrEquals(fieldGetter);
    }

    @Override
    public Criteria<E> _lessThan(EntityGetter<E> fieldGetter) {
        return (Criteria<E>) super._lessThan(fieldGetter);
    }

    @Override
    public Criteria<E> _lessThanOrEquals(EntityGetter<E> fieldGetter) {
        return (Criteria<E>) super._lessThanOrEquals(fieldGetter);
    }

    //--------------------------------end criteria--------------------------------------------------//


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

    protected List<String> getSelectColumns() {
        return selectColumns;
    }

    protected List<OrderBy> getOrderBys() {
        return orderBys;
    }

    protected Integer getPageSize() {
        return pageSize;
    }

    protected Integer getPageNumber() {
        return pageNumber;
    }

    protected Boolean getDistinct() {
        return distinct;
    }

    protected Long getVersion() {
        return version;
    }

    public boolean isUseCache() {
        return useCache;
    }

    protected Boolean getForUpdate() {
        return forUpdate;
    }
}
