package wang.liangchen.matrix.framework.data.dao.criteria;


import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.datasource.MultiDataSourceContext;
import wang.liangchen.matrix.framework.data.datasource.dialect.AbstractDialect;
import wang.liangchen.matrix.framework.data.pagination.OrderBy;
import wang.liangchen.matrix.framework.data.pagination.Pagination;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public enum CriteriaResolver {
    /**
     * instance
     */
    INSTANCE;

    public <E extends RootEntity> CriteriaParameter<E> resolve(AbstractCriteria<E> abstractCriteria) {
        CriteriaParameter<E> criteriaParameter = new CriteriaParameter<>();
        criteriaParameter.setEntity(abstractCriteria.getEntity());
        criteriaParameter.setEntityClass(abstractCriteria.getEntityClass());

        // 从线程上下文设置数据库类型
        AbstractDialect dialect = MultiDataSourceContext.INSTANCE.getDialect();
        if (null != dialect) {
            criteriaParameter.setDataSourceType(dialect.getDataSourceType());
        }
        criteriaParameter.setTableMeta(abstractCriteria.getTableMeta());

        ComposedCriteriaResolver composedCriteriaResolver = abstractCriteria.getComposedCriteriaResolver();
        String whereSql = composedCriteriaResolver.resolveWhereSql();
        criteriaParameter.setWhereSql(whereSql);
        criteriaParameter.setWhereSqlValues(composedCriteriaResolver.getMergedValues());

        if (abstractCriteria instanceof DeleteCriteria<E> deleteCriteria) {
            DeleteMeta deleteMeta = deleteCriteria.getMarkDeleteMeta();
            if (null != deleteMeta && null != deleteMeta.getDeleteValue() && null != deleteMeta.getDeleteColumnName()) {
                criteriaParameter.setMarkDeleteMeta(deleteMeta);
            }
            VersionMeta versionMeta = deleteCriteria.getVersionMeta();
            if (null != versionMeta && null != versionMeta.getVersionOldValue() && null != versionMeta.getVersionNewValue() && null != versionMeta.getVersionColumnName()) {
                criteriaParameter.setVersionMeta(versionMeta);
            }
        }
        if (abstractCriteria instanceof UpdateCriteria<E> updateCriteria) {
            criteriaParameter.setVersionMeta(updateCriteria.getVersionMeta());
            populateForceUpdate(updateCriteria, criteriaParameter);
        }

        if (abstractCriteria instanceof Criteria<E> criteria) {
            populateResultColumns(criteria, criteriaParameter);
            populateOrderBy(criteria, criteriaParameter);
            populatePagination(criteria, criteriaParameter);
        }

        return criteriaParameter;
    }


    private <E extends RootEntity> void populateForceUpdate(UpdateCriteria<E> updateCriteria, CriteriaParameter<E> criteriaParameter) {
        E entity = updateCriteria.getEntity();
        criteriaParameter.setEntity(entity);
        Map<String, Object> forceUpdateColumns = updateCriteria.getForceUpdateColumns();
        if (CollectionUtil.INSTANCE.isEmpty(forceUpdateColumns)) {
            return;
        }
        forceUpdateColumns.forEach(entity::addForceUpdateColumn);
    }

    private <E extends RootEntity> void populatePagination(Criteria<E> criteria, CriteriaParameter<E> criteriaParameter) {
        criteriaParameter.setForUpdate(criteria.getForUpdate());
        Pagination pagination = criteriaParameter.getPagination();
        pagination.setPageNumber(criteria.getPageNumber());
        pagination.setPageSize(criteria.getPageSize());
        criteriaParameter.setDistinct(criteria.getDistinct());
    }

    private <E extends RootEntity> void populateResultColumns(Criteria<E> criteria, CriteriaParameter<E> criteriaParameter) {
        Set<String> resultColumns = criteria.getResultColumns();
        // 非空则使用设置的返回列
        if (CollectionUtil.INSTANCE.isNotEmpty(resultColumns)) {
            criteriaParameter.addResultColumns(resultColumns);
            return;
        }
        // 为空则使用所有列
        Map<String, ColumnMeta> columnMetas = criteria.getTableMeta().getColumnMetas();
        columnMetas.forEach((fieldName, columnMeta) -> criteriaParameter.addResultColumn(columnMeta.getColumnName()));
    }

    private <E extends RootEntity> void populateOrderBy(Criteria<E> criteria, CriteriaParameter<E> criteriaParameter) {
        List<OrderBy> orderBys = criteria.getOrderBys();
        if (CollectionUtil.INSTANCE.isNotEmpty(orderBys)) {
            criteriaParameter.getPagination().addOrderBys(orderBys);
        }
    }

}
