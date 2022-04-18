package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.dao.table.TableMeta;
import wang.liangchen.matrix.framework.data.dao.table.TableMetas;
import wang.liangchen.matrix.framework.data.query.Operator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class AbstractCriteria<T extends RootEntity> {
    private final List<CriteriaMeta> CRITERIAMETAS = new ArrayList<>();
    private final List<AbstractCriteria<T>> ORS = new ArrayList<>();
    private final List<AbstractCriteria<T>> ANDS = new ArrayList<>();

    private final Class<T> entityClass;
    private final TableMeta tableMeta;

    protected AbstractCriteria(Class<T> entityClass) {
        this.entityClass = entityClass;
        tableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
    }

    protected AbstractCriteria<T> equals(EntityGetter<T> column, SqlValue sqlValue) {
        CRITERIAMETAS.add(CriteriaMeta.getInstance(Operator.EQUALS, column, sqlValue));
        return this;
    }

    protected AbstractCriteria<T> OR(SubCriteria<T> subCriteria) {
        ORS.add(subCriteria);
        return this;
    }

    protected AbstractCriteria<T> AND(SubCriteria<T> subCriteria) {
        ANDS.add(subCriteria);
        return this;
    }

    protected List<CriteriaMeta> getCRITERIAMETAS() {
        return CRITERIAMETAS;
    }

    protected List<AbstractCriteria<T>> getORS() {
        return ORS;
    }

    protected List<AbstractCriteria<T>> getANDS() {
        return ANDS;
    }

    protected Class<T> getEntityClass() {
        return entityClass;
    }

    protected TableMeta getTableMeta() {
        return tableMeta;
    }
}
