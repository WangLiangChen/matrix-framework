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
    public Criteria<E> OR(SubCriteria<E> subCriteria) {
        return (Criteria<E>) super.OR(subCriteria);
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
