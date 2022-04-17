package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.function.LambdaUtil;
import wang.liangchen.matrix.framework.data.dao.table.ColumnMeta;
import wang.liangchen.matrix.framework.data.query.Operator;

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

    public WhereSql resolve(Criteria criteria) {
        StringBuilder sqlBuilder = new StringBuilder();
        Map<String, Object> values = new HashMap<>();
        resovle(criteria, sqlBuilder, values, new AtomicInteger(0));
        String resultColumns = resolveResultColumns(criteria);
        return WhereSql.newInstance(criteria.getTableMeta(), sqlBuilder.toString(), values, resultColumns);
    }

    private String resolveResultColumns(Criteria criteria) {
        EntityGetter[] resultColumns = criteria.getResultColumns();
        if (CollectionUtil.INSTANCE.isEmpty(resultColumns)) {
            return Symbol.STAR.getSymbol();
        }
        Map<String, ColumnMeta> columnMetas = criteria.getTableMeta().getColumnMetas();
        return Arrays.stream(resultColumns).map(e -> {
            String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(e);
            ColumnMeta columnMeta = columnMetas.get(fieldName);
            return columnMeta.getColumnName();
        }).collect(Collectors.joining(Symbol.COMMA.getSymbol()));
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
                    placeholders.add(String.format("#{%s}", placeholder));
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
