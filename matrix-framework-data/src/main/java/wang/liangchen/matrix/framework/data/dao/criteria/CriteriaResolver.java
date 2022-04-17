package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.function.LambdaUtil;
import wang.liangchen.matrix.framework.data.dao.table.ColumnMeta;
import wang.liangchen.matrix.framework.data.pagination.OrderByDirection;
import wang.liangchen.matrix.framework.data.query.Operator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    public CriteriaParameter resolve(Criteria criteria) {
        CriteriaParameter criteriaParameter = new CriteriaParameter();
        criteriaParameter.setTableMeta(criteria.getTableMeta());

        StringBuilder sqlBuilder = new StringBuilder();
        Map<String, Object> values = new HashMap<>();
        resovle(criteria, sqlBuilder, values, new AtomicInteger(0));
        criteriaParameter.setWhereSql(sqlBuilder.toString());
        criteriaParameter.setWhereSqlValues(values);
        populateResultColumns(criteria, criteriaParameter);
        return criteriaParameter;
    }

    private void populateResultColumns(Criteria criteria, CriteriaParameter criteriaParameter) {
        EntityGetter[] resultFields = criteria.getResultFields();
        if (CollectionUtil.INSTANCE.isEmpty(resultFields)) {
            criteriaParameter.addResultColumn(Symbol.STAR.getSymbol());
            return;
        }
        Map<String, ColumnMeta> columnMetas = criteria.getTableMeta().getColumnMetas();
        for (EntityGetter resultField : resultFields) {
            String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(resultField);
            ColumnMeta columnMeta = columnMetas.get(fieldName);
            String columnName = columnMeta.getColumnName();
            criteriaParameter.addResultColumn(columnName);
        }
    }

    private void populateOrderBy(Criteria criteria, CriteriaParameter criteriaParameter) {
        Map<String, ColumnMeta> columnMetas = criteria.getTableMeta().getColumnMetas();
        Map<EntityGetter, OrderByDirection> orderByFields = criteria.getOrderBy();
        orderByFields.forEach((k, v) -> {
            String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(k);
            String columnName = columnMetas.get(fieldName).getColumnName();
            criteriaParameter.addOrderBy(columnName, v);
        });
    }


    private void resovle(AbstractCriteria abstractCriteria, StringBuilder sqlBuilder, Map<String, Object> values, AtomicInteger counter) {
        List<CriteriaMeta> CRITERIAMETAS = abstractCriteria.getCRITERIAMETAS();
        if (!CollectionUtil.INSTANCE.isEmpty(CRITERIAMETAS)) {
            String columnName;
            SqlValue[] sqlValues;
            List<String> placeholders;
            String placeholder;
            Operator operator;
            int innerCounter = 0;
            Map<String, ColumnMeta> columnMetas = abstractCriteria.getTableMeta().getColumnMetas();
            for (CriteriaMeta criteriaMeta : CRITERIAMETAS) {
                columnName = columnName(criteriaMeta.getColumn(), columnMetas);
                sqlValues = criteriaMeta.getSqlValues();
                placeholders = new ArrayList<>();
                for (SqlValue sqlValue : sqlValues) {
                    Object value = sqlValue.getValue();
                    if (value instanceof EntityGetter) {
                        placeholders.add(columnName((EntityGetter) value, columnMetas));
                        continue;
                    }
                    placeholder = String.format("%s%d", columnName, counter.getAndIncrement());
                    values.put(placeholder, sqlValue.getValue());
                    placeholders.add(String.format("#{whereSqlValues.%s}", placeholder));
                }

                if (innerCounter++ > 0) {
                    sqlBuilder.append(AND);
                }
                operator = criteriaMeta.getOperator();
                switch (operator) {
                    case EQUALS:
                        sqlBuilder.append(columnName).append(operator.getOperator()).append(placeholders.get(0));
                        break;
                }
            }
        }
        // 处理ANDS
        List<AbstractCriteria> ANDS = abstractCriteria.getANDS();
        if (CollectionUtil.INSTANCE.isNotEmpty(ANDS)) {
            for (AbstractCriteria builder : ANDS) {
                sqlBuilder.append(AND).append(Symbol.OPEN_PAREN.getSymbol());
                resovle(builder, sqlBuilder, values, counter);
                sqlBuilder.append(Symbol.CLOSE_PAREN.getSymbol());
            }
        }
        // 处理ORS
        List<AbstractCriteria> ORS = abstractCriteria.getORS();
        if (CollectionUtil.INSTANCE.isNotEmpty(ORS)) {
            for (AbstractCriteria builder : ORS) {
                sqlBuilder.append(OR).append(Symbol.OPEN_PAREN.getSymbol());
                resovle(builder, sqlBuilder, values, counter);
                sqlBuilder.append(Symbol.CLOSE_PAREN.getSymbol());
            }
        }
    }

    private String columnName(EntityGetter column, final Map<String, ColumnMeta> columnMetas) {
        String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(column);
        ColumnMeta columnMeta = columnMetas.get(fieldName);
        return columnMeta.getColumnName();
    }

}
