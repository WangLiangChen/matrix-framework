package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.function.LambdaUtil;
import wang.liangchen.matrix.framework.commons.object.BeanUtil;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.data.dao.table.ColumnMeta;
import wang.liangchen.matrix.framework.data.dao.table.TableMeta;
import wang.liangchen.matrix.framework.data.dao.table.TableMetas;
import wang.liangchen.matrix.framework.data.query.AndOr;
import wang.liangchen.matrix.framework.data.query.Operator;
import wang.liangchen.matrix.framework.data.util.SqlBuilder;

import java.lang.invoke.SerializedLambda;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class CriteriaBuilder<T> {
    private final List<CriteriaMeta> criteriaMetas = new ArrayList<>();
    private final List<CriteriaBuilder<T>> ORS = new ArrayList<>();
    private final static String AND = " and ";
    private final static String OR = " or ";
    private int valueCounter = 0;

    public static <T> CriteriaBuilder<T> newInstance() {
        return new CriteriaBuilder<T>() {
        };
    }

    public CriteriaBuilder<T> equals(EntityGetter<T> column, SqlValue sqlValue) {
        criteriaMetas.add(CriteriaMeta.getInstance(Operator.EQUALS, column, sqlValue));
        return this;
    }

    public CriteriaBuilder<T> OR(CriteriaBuilder<T> criteriaBuilder) {
        ORS.add(criteriaBuilder);
        return this;
    }

    public WhereSql whereSql() {
        if (criteriaMetas.isEmpty()) {
            return WhereSql.newInstance(Symbol.BLANK.getSymbol(), Collections.EMPTY_MAP);
        }
        CriteriaMeta criteriaMeta0 = criteriaMetas.get(0);
        EntityGetter column = criteriaMeta0.getColumn();
        SerializedLambda serializedLambda = LambdaUtil.INSTANCE.serializedLambda(column);
        String implClass = serializedLambda.getImplClass();
        implClass = StringUtil.INSTANCE.Path2Package(implClass);
        Class<?> entityClass = ClassUtil.INSTANCE.forName(implClass);
        TableMeta tableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
        Map<String, ColumnMeta> columnMetas = tableMeta.getColumnMetas();
        return buildWhereSql(columnMetas);
    }

    private WhereSql buildWhereSql(Map<String, ColumnMeta> columnMetas) {
        String columnName;
        Operator operator;
        SqlValue[] sqlValues;
        List<String> placeholders;
        String placeholder;
        Map<String, Object> values = new HashMap<>();
        StringBuilder sqlBuilder = new StringBuilder();

        for (CriteriaMeta criteriaMeta : criteriaMetas) {
            columnName = columnName(criteriaMeta.getColumn(), columnMetas);
            sqlValues = criteriaMeta.getSqlValues();
            placeholders = new ArrayList<>();
            for (SqlValue sqlValue : sqlValues) {
                Object value = sqlValue.getValue();
                if (value instanceof EntityGetter) {
                    placeholders.add(columnName((EntityGetter) value, columnMetas));
                    continue;
                }
                placeholder = String.format("%s%d", columnName, valueCounter++);
                values.put(placeholder, sqlValue.getValue());
                placeholders.add(String.format("#{%s}", placeholder));
            }
            operator = criteriaMeta.getOperator();
            sqlBuilder.append(AND);
            switch (operator) {
                case EQUALS:
                    sqlBuilder.append(columnName).append(operator.getOperator()).append(placeholders.get(0));
                    break;
            }
        }
        return WhereSql.newInstance(sqlBuilder.toString(), values);
    }

    private String columnName(EntityGetter column, final Map<String, ColumnMeta> columnMetas) {
        String fieldName = LambdaUtil.INSTANCE.getReferencedFieldName(column);
        ColumnMeta columnMeta = columnMetas.get(fieldName);
        return columnMeta.getColumnName();
    }


}
