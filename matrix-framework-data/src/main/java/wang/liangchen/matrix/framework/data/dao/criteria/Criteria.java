package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.pagination.OrderByDirection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class Criteria<T extends RootEntity> extends AbstractCriteria<T> {
    private EntityGetter<T>[] resultFields;
    private Boolean forUpdate;
    private Integer pageSize;
    private Integer pageNumber;
    private Boolean distinct;
    private Long version;
    private Map<EntityGetter<T>, OrderByDirection> orderBy = new HashMap<>();

    private Criteria(Class<T> entityClass) {
        super(entityClass);
    }

    public static <T extends RootEntity> Criteria<T> of(Class<T> entityClass) {
        return new Criteria<T>(entityClass) {
        };
    }

    public Criteria<T> resultFields(EntityGetter<T>... resultFields) {
        this.resultFields = resultFields;
        return this;
    }

    public Criteria<T> distinct() {
        this.distinct = true;
        return this;
    }

    public Criteria<T> forUpdate() {
        this.forUpdate = true;
        return this;
    }

    public Criteria<T> orderBy(EntityGetter<T> column, OrderByDirection orderByDirection) {
        orderBy.put(column, orderByDirection);
        return this;
    }

    public Criteria<T> pageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Criteria<T> pageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public Criteria<T> version(long version) {
        this.version = version;
        return this;
    }

    @Override
    public Criteria<T> equals(EntityGetter<T> column, SqlValue sqlValue) {
        return (Criteria<T>) super.equals(column, sqlValue);
    }

    @Override
    public Criteria<T> OR(SubCriteria<T> subCriteria) {
        return (Criteria<T>) super.OR(subCriteria);
    }

    @Override
    public Criteria<T> AND(SubCriteria<T> subCriteria) {
        return (Criteria<T>) super.AND(subCriteria);
    }

    protected EntityGetter<T>[] getResultFields() {
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

    protected Map<EntityGetter<T>, OrderByDirection> getOrderBy() {
        return orderBy;
    }


    protected Long getVersion() {
        return version;
    }
}
