package wang.liangchen.matrix.framework.data.criteria;

import wang.liangchen.matrix.framework.commons.CollectionUtil;
import wang.liangchen.matrix.framework.data.context.DataSourceContext;
import wang.liangchen.matrix.framework.data.datasource.dialect.AbstractDialect;
import wang.liangchen.matrix.framework.data.entity.RootEntity;
import wang.liangchen.matrix.framework.data.pagination.OrderBy;
import wang.liangchen.matrix.framework.data.pagination.Pagination;
import wang.liangchen.matrix.framework.data.resolver.FieldMeta;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author LiangChen.Wang 2024/11/6 18:22
 */
enum CriteriaResolver {
    INSTANCE;

    public <E extends RootEntity> CriteriaParameter<E> resolve(AbstractCriteria<E> abstractCriteria) {
        CriteriaParameter<E> criteriaParameter = abstractCriteria.getCriteriaParameter();

        // 从线程上下文设置数据库类型
        AbstractDialect dialect = DataSourceContext.INSTANCE.getDialect();
        if (null != dialect) {
            criteriaParameter.setDriverClassName(dialect.getDriverClassName());
        }

        ComposedCriteriaResolver composedCriteriaResolver = abstractCriteria.getComposedCriteriaResolver();
        String whereSql = composedCriteriaResolver.resolveWhereSql();
        criteriaParameter.setWhereSql(whereSql);
        criteriaParameter.setWhereSqlValues(composedCriteriaResolver.getMergedValues());

        if (abstractCriteria instanceof DeleteCriteria<E> deleteCriteria) {
            SoftDeleteColumnMeta deleteMeta = deleteCriteria.getDeleteMeta();
            if (null != deleteMeta && null != deleteMeta.getValue() && null != deleteMeta.getColumnName()) {
                criteriaParameter.setDeleteMeta(deleteMeta);
            }
            VersionColumnMeta versionMeta = deleteCriteria.getVersionMeta();
            if (null != versionMeta && null != versionMeta.getExpectedValue() && null != versionMeta.getValue() && null != versionMeta.getColumnName()) {
                criteriaParameter.setVersionMeta(versionMeta);
            }
        }
        if (abstractCriteria instanceof UpdateCriteria<E> updateCriteria) {
            criteriaParameter.setVersionMeta(updateCriteria.getVersionMeta());
            populateMandatoryUpdate(updateCriteria, criteriaParameter);
        }

        if (abstractCriteria instanceof Criteria<E> criteria) {
            populateResultColumns(criteria, criteriaParameter);
            populateOrderBy(criteria, criteriaParameter);
            populatePagination(criteria, criteriaParameter);
        }

        return criteriaParameter;
    }


    private <E extends RootEntity> void populateMandatoryUpdate(UpdateCriteria<E> updateCriteria, CriteriaParameter<E> criteriaParameter) {
        E entity = criteriaParameter.getEntity();
        Map<String, Object> mandatoryUpdatedColumns = updateCriteria.getMandatoryUpdatedColumns();
        if (CollectionUtil.INSTANCE.isEmpty(mandatoryUpdatedColumns)) {
            return;
        }
        mandatoryUpdatedColumns.forEach(entity::addMandatoryUpdatedColumns);
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
        Map<String, FieldMeta> fieldMetas = criteria.getFieldMetas();
        fieldMetas.forEach((fieldName, fieldMeta) -> criteriaParameter.addResultColumn(fieldMeta.getColumnName()));
    }

    private <E extends RootEntity> void populateOrderBy(Criteria<E> criteria, CriteriaParameter<E> criteriaParameter) {
        List<OrderBy> orderBys = criteria.getOrderBys();
        if (CollectionUtil.INSTANCE.isNotEmpty(orderBys)) {
            criteriaParameter.getPagination().addOrderBys(orderBys);
        }
    }

}
