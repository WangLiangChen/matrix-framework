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

/**
 * @author LiangChen.Wang 2024/11/6 18:22
 */
public enum CriteriaResolver {
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
            if (!deleteCriteria.isHardDelete()) {
                FieldMeta softDeleteFieldMeta = deleteCriteria.getSoftDeleteFieldMeta();
                criteriaParameter.setSoftDeleteColumnValue(softDeleteFieldMeta.getSoftDeleteValue());
            }
            return criteriaParameter;
        }
        if (abstractCriteria instanceof UpdateCriteria<E> updateCriteria) {
            return criteriaParameter;
        }

        if (abstractCriteria instanceof Criteria<E> criteria) {
            populateResultColumns(criteria, criteriaParameter);
            populateOrderBy(criteria, criteriaParameter);
            populatePagination(criteria, criteriaParameter);
            return criteriaParameter;
        }
        return criteriaParameter;
    }


    private <E extends RootEntity> void populatePagination(Criteria<E> criteria, CriteriaParameter<E> criteriaParameter) {
        criteriaParameter.setForUpdate(criteria.getForUpdate());
        Pagination pagination = criteriaParameter.getPagination();
        pagination.setPageNumber(criteria.getPageNumber());
        pagination.setPageSize(criteria.getPageSize());
        criteriaParameter.setDistinct(criteria.getDistinct());
    }

    private <E extends RootEntity> void populateResultColumns(Criteria<E> criteria, CriteriaParameter<E> criteriaParameter) {
        List<String> selectColumns = criteria.getSelectColumns();
        // 非空则使用设置的返回列
        if (CollectionUtil.INSTANCE.isNotEmpty(selectColumns)) {
            criteriaParameter.addSelectColumns(selectColumns);
            return;
        }
        // 为空则使用所有列
        Map<String, FieldMeta> fieldMetas = criteria.getFieldMetas();
        fieldMetas.forEach((fieldName, fieldMeta) -> criteriaParameter.addSelectColumn(fieldMeta.getColumnName()));
    }

    private <E extends RootEntity> void populateOrderBy(Criteria<E> criteria, CriteriaParameter<E> criteriaParameter) {
        List<OrderBy> orderBys = criteria.getOrderBys();
        if (CollectionUtil.INSTANCE.isNotEmpty(orderBys)) {
            criteriaParameter.getPagination().addOrderBys(orderBys);
        }
    }

}
