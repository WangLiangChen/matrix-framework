package wang.liangchen.matrix.framework.data.dao.criteria;


import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.function.LambdaUtil;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.datasource.MultiDataSourceContext;
import wang.liangchen.matrix.framework.data.datasource.dialect.AbstractDialect;
import wang.liangchen.matrix.framework.data.pagination.OrderByDirection;

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
        criteriaParameter.setPageNumber(criteria.getPageNumber());
        criteriaParameter.setPageSize(criteria.getPageSize());
        criteriaParameter.setDistinct(criteria.getDistinct());
    }

    private <E extends RootEntity> void populateResultColumns(Criteria<E> criteria, CriteriaParameter<E> criteriaParameter) {
        Map<String, ColumnMeta> columnMetas = criteria.getTableMeta().getColumnMetas();
        EntityGetter<E>[] resultFields = criteria.getResultFields();
        if (CollectionUtil.INSTANCE.isNotEmpty(resultFields)) {
            Set<String> fieldNames = Arrays.stream(resultFields).map(LambdaUtil.INSTANCE::getReferencedFieldName).collect(Collectors.toSet());
            columnMetas = columnMetas.entrySet().stream().filter(e -> fieldNames.contains(e.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        columnMetas.forEach((fieldName, columnMeta) -> criteriaParameter.addResultColumn(columnMeta.getColumnName()));
    }

    private <E extends RootEntity> void populateOrderBy(Criteria<E> criteria, CriteriaParameter<E> criteriaParameter) {
        Map<String, ColumnMeta> columnMetas = criteria.getTableMeta().getColumnMetas();
        Map<EntityGetter<E>, OrderByDirection> orderByFields = criteria.getOrderBy();
        orderByFields.forEach((k, v) -> {
            String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(k);
            String columnName = columnMetas.get(fieldName).getColumnName();
            criteriaParameter.addOrderBy(columnName, v);
        });
    }


    @SuppressWarnings("unchecked")
    private <E extends RootEntity> void resovle(AbstractCriteria<E> abstractCriteria, StringBuilder sqlBuilder, Map<String, Object> values, AtomicInteger counter) {
        List<CriteriaMeta<E>> CRITERIAMETAS = abstractCriteria.getCRITERIAMETAS();
        if (!CollectionUtil.INSTANCE.isEmpty(CRITERIAMETAS)) {
            String columnName;
            Object[] sqlValues;
            String placeholder = null;
            Operator operator;
            int innerCounter = 0;
            Map<String, ColumnMeta> columnMetas = abstractCriteria.getTableMeta().getColumnMetas();
            for (CriteriaMeta<E> criteriaMeta : CRITERIAMETAS) {
                if (innerCounter++ > 0) {
                    sqlBuilder.append(AND);
                }
                columnName = resoveEntityGetter(criteriaMeta.getColumn(), columnMetas);
                operator = criteriaMeta.getOperator();
                sqlBuilder.append(columnName).append(operator.getOperator());
                sqlValues = criteriaMeta.getSqlValues();
                for (int i = 0; i < sqlValues.length; i++) {
                    Object sqlValue = sqlValues[i];
                    if (sqlValue instanceof EntityGetter) {
                        sqlValues[i] = resoveEntityGetter((EntityGetter<? extends RootEntity>) sqlValue, columnMetas);
                        continue;
                    }
                    if (sqlValue instanceof Class) {
                        sqlValues[i] = String.format("#{entity.%s}", columnName);
                        continue;
                    }
                    placeholder = String.format("%s%d", columnName, counter.getAndIncrement());
                    sqlValues[i] = String.format("#{whereSqlValues.%s}", placeholder);
                    values.put(placeholder, sqlValue);
                }
                switch (operator) {
                    case IN:
                    case NOTIN:
                        sqlBuilder.append(Symbol.OPEN_PAREN.getSymbol());
                        sqlBuilder.append(Arrays.stream(sqlValues).map(String::valueOf).collect(Collectors.joining(Symbol.COMMA.getSymbol())));
                        sqlBuilder.append(Symbol.CLOSE_PAREN.getSymbol());
                        break;
                    case BETWEEN:
                    case NOTBETWEEN:
                        sqlBuilder.append(sqlValues[0]).append(AND).append(sqlValues[1]);
                        break;
                    case ISNULL:
                    case ISNOTNULL:
                        break;
                    case CONTAINS:
                    case NOTCONTAINS:
                        Object object = values.get(placeholder);
                        object = String.format("%%%s%%", object);
                        values.put(placeholder, object);
                        sqlBuilder.append(sqlValues[0]);
                        break;
                    case STARTWITH:
                    case NOTSTARTWITH:
                        object = values.get(placeholder);
                        object = String.format("%s%%", object);
                        values.put(placeholder, object);
                        sqlBuilder.append(sqlValues[0]);
                        break;
                    case ENDWITH:
                    case NOTENDWITH:
                        object = values.get(placeholder);
                        object = String.format("%%%s", object);
                        values.put(placeholder, object);
                        sqlBuilder.append(sqlValues[0]);
                        break;
                    default:
                        sqlBuilder.append(sqlValues[0]);
                        break;
                }
            }
        }
        // 处理ANDS
        List<AbstractCriteria<E>> ANDS = abstractCriteria.getANDS();
        if (CollectionUtil.INSTANCE.isNotEmpty(ANDS)) {
            for (AbstractCriteria<E> builder : ANDS) {
                sqlBuilder.append(AND).append(Symbol.OPEN_PAREN.getSymbol());
                resovle(builder, sqlBuilder, values, counter);
                sqlBuilder.append(Symbol.CLOSE_PAREN.getSymbol());
            }
        }
        // 处理ORS
        List<AbstractCriteria<E>> ORS = abstractCriteria.getORS();
        if (CollectionUtil.INSTANCE.isNotEmpty(ORS)) {
            for (AbstractCriteria<E> innerAbstractCriteria : ORS) {
                sqlBuilder.append(OR).append(Symbol.OPEN_PAREN.getSymbol());
                resovle(innerAbstractCriteria, sqlBuilder, values, counter);
                sqlBuilder.append(Symbol.CLOSE_PAREN.getSymbol());
            }
        }
    }

    private <E extends RootEntity> String resoveEntityGetter(EntityGetter<E> entityGetter, final Map<String, ColumnMeta> columnMetas) {
        String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(entityGetter);
        ColumnMeta columnMeta = columnMetas.get(fieldName);
        return columnMeta.getColumnName();
    }

}
