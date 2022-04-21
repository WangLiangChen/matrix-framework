package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
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

    protected AbstractCriteria<E> equals(EntityGetter<E> column, SqlValue sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.EQUALS, column, sqlValue));
        return this;
    }

    protected AbstractCriteria<E> OR(SubCriteria<E> subCriteria) {
        ORS.add(subCriteria);
        return this;
    }

    protected AbstractCriteria<E> AND(SubCriteria<E> subCriteria) {
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
