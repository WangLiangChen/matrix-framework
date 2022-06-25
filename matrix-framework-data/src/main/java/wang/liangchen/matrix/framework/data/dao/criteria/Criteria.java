package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.pagination.OrderByDirection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class Criteria<E extends RootEntity> extends AbstractCriteria<E> {
    private EntityGetter<E>[] resultFields;
    private Boolean forUpdate;
    private Integer pageSize;
    private Integer pageNumber;
    private Boolean distinct;
    private Long version;
    private final Map<EntityGetter<E>, OrderByDirection> orderBy = new HashMap<>();

    private Criteria(Class<E> entityClass) {
        super(entityClass);
    }

    public static <E extends RootEntity> Criteria<E> of(Class<E> entityClass) {
        return new Criteria<E>(entityClass) {
        };
    }

    @SafeVarargs
    public final Criteria<E> resultFields(EntityGetter<E>... resultFields) {
        this.resultFields = resultFields;
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

    public Criteria<E> orderBy(EntityGetter<E> column, OrderByDirection orderByDirection) {
        orderBy.put(column, orderByDirection);
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

    public Criteria<E> version(long version) {
        this.version = version;
        return this;
    }

    @Override
    public Criteria<E> _equals(EntityGetter<E> column, SqlValue sqlValue) {
        return (Criteria<E>) super._equals(column, sqlValue);
    }

    @Override
    public Criteria<E> _notEquals(EntityGetter<E> column, SqlValue sqlValue) {
        return (Criteria<E>) super._notEquals(column, sqlValue);
    }

    @Override
    public Criteria<E> _greaterThan(EntityGetter<E> column, SqlValue sqlValue) {
        return (Criteria<E>) super._greaterThan(column, sqlValue);
    }

    @Override
    public Criteria<E> _greaterThanOrEquals(EntityGetter<E> column, SqlValue sqlValue) {
        return (Criteria<E>) super._greaterThanOrEquals(column, sqlValue);
    }

    @Override
    public Criteria<E> _lessThan(EntityGetter<E> column, SqlValue sqlValue) {
        return (Criteria<E>) super._lessThan(column, sqlValue);
    }

    @Override
    public Criteria<E> _lessThanOrEquals(EntityGetter<E> column, SqlValue sqlValue) {
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
    public Criteria<E> _in(EntityGetter<E> column, SqlValue... values) {
        return (Criteria<E>) super._in(column, values);
    }

    @Override
    public Criteria<E> _notIn(EntityGetter<E> column, SqlValue... values) {
        return (Criteria<E>) super._notIn(column, values);
    }

    @Override
    public Criteria<E> _between(EntityGetter<E> column, SqlValue valueMin, SqlValue valueMax) {
        return (Criteria<E>) super._between(column, valueMin, valueMax);
    }

    @Override
    public Criteria<E> _notBetween(EntityGetter<E> column, SqlValue valueMin, SqlValue valueMax) {
        return (Criteria<E>) super._notBetween(column, valueMin, valueMax);
    }

    @Override
    public Criteria<E> _contains(EntityGetter<E> column, String value) {
        return (Criteria<E>) super._contains(column, value);
    }

    @Override
    public Criteria<E> _notContains(EntityGetter<E> column, String value) {
        return (Criteria<E>) super._notContains(column, value);
    }

    @Override
    public Criteria<E> _startWith(EntityGetter<E> column, String value) {
        return (Criteria<E>) super._startWith(column, value);
    }

    @Override
    public Criteria<E> _notStartWith(EntityGetter<E> column, String value) {
        return (Criteria<E>) super._notStartWith(column, value);
    }

    @Override
    public Criteria<E> _endWith(EntityGetter<E> column, String value) {
        return (Criteria<E>) super._endWith(column, value);
    }

    @Override
    public Criteria<E> _notEndWith(EntityGetter<E> column, String value) {
        return (Criteria<E>) super._notEndWith(column, value);
    }

    @Override
    public Criteria<E> _OR(SubCriteria<E> subCriteria) {
        return (Criteria<E>) super._OR(subCriteria);
    }

    @Override
    public Criteria<E> _AND(SubCriteria<E> subCriteria) {
        return (Criteria<E>) super._AND(subCriteria);
    }

    protected EntityGetter<E>[] getResultFields() {
        return resultFields;
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

    protected Map<EntityGetter<E>, OrderByDirection> getOrderBy() {
        return orderBy;
    }


    protected Long getVersion() {
        return version;
    }
}
