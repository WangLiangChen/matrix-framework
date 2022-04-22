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
    public Criteria<E> equals(EntityGetter<E> column, SqlValue sqlValue) {
        return (Criteria<E>) super.equals(column, sqlValue);
    }

    @Override
    public Criteria<E> notEquals(EntityGetter<E> column, SqlValue sqlValue) {
        return (Criteria<E>) super.notEquals(column, sqlValue);
    }

    @Override
    public Criteria<E> greaterThan(EntityGetter<E> column, SqlValue sqlValue) {
        return (Criteria<E>) super.greaterThan(column, sqlValue);
    }

    @Override
    public Criteria<E> greaterThanOrEquals(EntityGetter<E> column, SqlValue sqlValue) {
        return (Criteria<E>) super.greaterThanOrEquals(column, sqlValue);
    }

    @Override
    public Criteria<E> lessThan(EntityGetter<E> column, SqlValue sqlValue) {
        return (Criteria<E>) super.lessThan(column, sqlValue);
    }

    @Override
    public Criteria<E> lessThanOrEquals(EntityGetter<E> column, SqlValue sqlValue) {
        return (Criteria<E>) super.lessThanOrEquals(column, sqlValue);
    }

    @Override
    public Criteria<E> isNull(EntityGetter<E> column) {
        return (Criteria<E>) super.isNull(column);
    }

    @Override
    public Criteria<E> isNotNull(EntityGetter<E> column) {
        return (Criteria<E>) super.isNotNull(column);
    }

    @Override
    public Criteria<E> in(EntityGetter<E> column, SqlValue... values) {
        return (Criteria<E>) super.in(column, values);
    }

    @Override
    public Criteria<E> notIn(EntityGetter<E> column, SqlValue... values) {
        return (Criteria<E>) super.notIn(column, values);
    }

    @Override
    public Criteria<E> between(EntityGetter<E> column, SqlValue valueMin, SqlValue valueMax) {
        return (Criteria<E>) super.between(column, valueMin, valueMax);
    }

    @Override
    public Criteria<E> notBetween(EntityGetter<E> column, SqlValue valueMin, SqlValue valueMax) {
        return (Criteria<E>) super.notBetween(column, valueMin, valueMax);
    }

    @Override
    public Criteria<E> contains(EntityGetter<E> column, String value) {
        return (Criteria<E>) super.contains(column, value);
    }

    @Override
    public Criteria<E> notContains(EntityGetter<E> column, String value) {
        return (Criteria<E>) super.notContains(column, value);
    }

    @Override
    public Criteria<E> startWith(EntityGetter<E> column, String value) {
        return (Criteria<E>) super.startWith(column, value);
    }

    @Override
    public Criteria<E> notStartWith(EntityGetter<E> column, String value) {
        return (Criteria<E>) super.notStartWith(column, value);
    }

    @Override
    public Criteria<E> endWith(EntityGetter<E> column, String value) {
        return (Criteria<E>) super.endWith(column, value);
    }

    @Override
    public Criteria<E> notEndWith(EntityGetter<E> column, String value) {
        return (Criteria<E>) super.notEndWith(column, value);
    }

    @Override
    public Criteria<E> OR(SubCriteria<E> subCriteria) {
        return (Criteria<E>) (Criteria<E>) super.OR(subCriteria);
    }

    @Override
    public Criteria<E> AND(SubCriteria<E> subCriteria) {
        return (Criteria<E>) super.AND(subCriteria);
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
