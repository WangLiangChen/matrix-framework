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
        // 从线程上下文设置数据库类型
        AbstractDialect dialect = MultiDataSourceContext.INSTANCE.getDialect();
        criteriaParameter.setDataSourceType(dialect.getDataSourceType());
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
        if (!CollectionUtil.INSTANCE.isEmpty(resultFields)) {
            Set<String> fieldNames = Arrays.stream(resultFields).map(resultField -> LambdaUtil.INSTANCE.getReferencedFieldName(resultField)).collect(Collectors.toSet());
            columnMetas = columnMetas.entrySet().stream().filter(e -> fieldNames.contains(e.getKey())).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        }
        columnMetas.forEach((fieldName, columnMeta) -> {
            String columnName = columnMeta.getColumnName();
            // 列名 as 成 实体属性名
            if (Objects.equals(columnName, fieldName)) {
                criteriaParameter.addResultColumn(columnName);
            } else {
                criteriaParameter.addResultColumn(String.format("%s as %s", columnName, fieldName));
            }
        });
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
            SqlValue[] sqlValues;
            List<String> placeholders;
            String placeholder;
            Operator operator;
            int innerCounter = 0;
            Map<String, ColumnMeta> columnMetas = abstractCriteria.getTableMeta().getColumnMetas();
            for (CriteriaMeta<E> criteriaMeta : CRITERIAMETAS) {
                columnName = columnName(criteriaMeta.getColumn(), columnMetas);
                sqlValues = criteriaMeta.getSqlValues();
                placeholders = new ArrayList<>();
                for (SqlValue sqlValue : sqlValues) {
                    Object value = sqlValue.getValue();
                    if (value instanceof EntityGetter) {
                        placeholders.add(columnName((EntityGetter<E>) value, columnMetas));
                        continue;
                    }
                    placeholder = String.format("%s%d", columnName, counter.getAndIncrement());
                    values.put(placeholder, value);
                    placeholders.add(String.format("#{whereSqlValues.%s}", placeholder));
                }
                if (innerCounter++ > 0) {
                    sqlBuilder.append(AND);
                }
                operator = criteriaMeta.getOperator();
                switch (operator) {
                    case IN:
                    case NOTIN:
                        sqlBuilder.append(columnName).append(operator.getOperator()).append(Symbol.OPEN_PAREN.getSymbol());
                        sqlBuilder.append(placeholders.stream().collect(Collectors.joining(Symbol.COMMA.getSymbol())));
                        sqlBuilder.append(Symbol.CLOSE_PAREN.getSymbol());
                        break;
                    case BETWEEN:
                    case NOTBETWEEN:
                        sqlBuilder.append(columnName).append(operator.getOperator()).append(placeholders.get(0)).append(AND).append(placeholders.get(1));
                        break;
                    case ISNULL:
                    case ISNOTNULL:
                        sqlBuilder.append(columnName).append(operator.getOperator());
                        break;
                    case CONTAINS:
                    case NOTCONTAINS:
                        sqlBuilder.append(columnName).append(operator.getOperator()).append("'%").append(sqlValues[0].getValue()).append("%'");
                        break;
                    case STARTWITH:
                    case NOTSTARTWITH:
                        sqlBuilder.append(columnName).append(operator.getOperator()).append("'").append(sqlValues[0].getValue()).append("%'");
                        break;
                    case ENDWITH:
                    case NOTENDWITH:
                        sqlBuilder.append(columnName).append(operator.getOperator()).append("'%").append(sqlValues[0].getValue()).append("'");
                        break;
                    default:
                        sqlBuilder.append(columnName).append(operator.getOperator()).append(placeholders.get(0));
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

    private <E extends RootEntity> String columnName(EntityGetter<E> column, final Map<String, ColumnMeta> columnMetas) {

        String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(column);
        ColumnMeta columnMeta = columnMetas.get(fieldName);
        return columnMeta.getColumnName();
    }

}
