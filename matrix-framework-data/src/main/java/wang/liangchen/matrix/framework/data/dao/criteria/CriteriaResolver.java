package wang.liangchen.matrix.framework.data.dao.criteria;


import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.function.LambdaUtil;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.datasource.MultiDataSourceContext;
import wang.liangchen.matrix.framework.data.datasource.dialect.AbstractDialect;
import wang.liangchen.matrix.framework.data.pagination.OrderBy;
import wang.liangchen.matrix.framework.data.pagination.Pagination;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public enum CriteriaResolver {
    /**
     * instance
     */
    INSTANCE;
    private final static String AND = " and ";
    private final static String OR = " or ";

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

        StringBuilder sqlBuilder = new StringBuilder();
        Map<String, Object> values = new HashMap<>();
        resovle(abstractCriteria, sqlBuilder, values, new AtomicInteger(0));
        criteriaParameter.setWhereSql(sqlBuilder.toString());
        criteriaParameter.setWhereSqlValues(values);

        if (abstractCriteria instanceof Criteria) {
            Criteria<E> criteria = (Criteria<E>) abstractCriteria;
            populateResultColumns(criteria, criteriaParameter);
            populateOrderBy(criteria, criteriaParameter);
            populatePagination(criteria, criteriaParameter);
        }

        if (abstractCriteria instanceof UpdateCriteria) {
            UpdateCriteria<E> updateCriteria = (UpdateCriteria<E>) abstractCriteria;
            populateForceUpdate(updateCriteria, criteriaParameter);
        }
        return criteriaParameter;
    }

    private <E extends RootEntity> void populateForceUpdate(UpdateCriteria<E> updateCriteria, CriteriaParameter<E> criteriaParameter) {
        E entity = updateCriteria.getEntity();
        criteriaParameter.setEntity(entity);
        Map<EntityGetter<E>, Object> forceUpdateColumns = updateCriteria.getForceUpdateFields();
        if (CollectionUtil.INSTANCE.isEmpty(forceUpdateColumns)) {
            return;
        }
        Map<String, ColumnMeta> columnMetas = updateCriteria.getTableMeta().getColumnMetas();
        forceUpdateColumns.forEach((column, value) -> {
            String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(column);
            String columnName = columnMetas.get(fieldName).getColumnName();
            entity.addForceUpdateColumn(columnName, value);
        });
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


    @SuppressWarnings("unchecked")
    private <E extends RootEntity> void resovle(AbstractCriteria<E> abstractCriteria, StringBuilder sqlBuilder, Map<String, Object> values, AtomicInteger counter) {
        List<CriteriaMeta<E>> CRITERIAMETAS = abstractCriteria.getCRITERIAMETAS();
        if (!CollectionUtil.INSTANCE.isEmpty(CRITERIAMETAS)) {
            ColumnMeta columnMeta;
            String columnName;
            String[] placeholders;
            Object[] sqlValues;
            String placeholder = null;
            Operator operator;
            for (CriteriaMeta<E> criteriaMeta : CRITERIAMETAS) {
                columnMeta = criteriaMeta.getColumnMeta();
                columnName = columnMeta.getColumnName();
                sqlValues = criteriaMeta.getSqlValues();
                placeholders = new String[sqlValues.length];
                for (int i = 0; i < sqlValues.length; i++) {
                    placeholder = String.format("%s%d", columnName, counter.getAndIncrement());
                    placeholders[i] = String.format("#{whereSqlValues.%s}", placeholder);
                    values.put(placeholder, sqlValues[i]);
                }
                operator = criteriaMeta.getOperator();
                sqlBuilder.append(AND).append(columnName).append(operator.getOperator());
                switch (operator) {
                    case IN:
                    case NOTIN:
                        sqlBuilder.append(Symbol.OPEN_PAREN.getSymbol());
                        sqlBuilder.append(Arrays.stream(placeholders).map(String::valueOf).collect(Collectors.joining(Symbol.COMMA.getSymbol())));
                        sqlBuilder.append(Symbol.CLOSE_PAREN.getSymbol());
                        break;
                    case BETWEEN:
                    case NOTBETWEEN:
                        sqlBuilder.append(placeholders[0]).append(AND).append(placeholders[1]);
                        break;
                    case ISNULL:
                    case ISNOTNULL:
                        break;
                    case CONTAINS:
                    case NOTCONTAINS:
                        Object object = values.get(placeholder);
                        object = String.format("%%%s%%", object);
                        values.put(placeholder, object);
                        sqlBuilder.append(placeholders[0]);
                        break;
                    case STARTWITH:
                    case NOTSTARTWITH:
                        object = values.get(placeholder);
                        object = String.format("%s%%", object);
                        values.put(placeholder, object);
                        sqlBuilder.append(placeholders[0]);
                        break;
                    case ENDWITH:
                    case NOTENDWITH:
                        object = values.get(placeholder);
                        object = String.format("%%%s", object);
                        values.put(placeholder, object);
                        sqlBuilder.append(placeholders[0]);
                        break;
                    default:
                        sqlBuilder.append(placeholders[0]);
                        break;
                }
            }
        }
    }
}
