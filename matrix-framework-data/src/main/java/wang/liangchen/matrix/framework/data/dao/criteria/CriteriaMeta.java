package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.Collection;

/**
 * @author Liangchen.Wang 2022-04-16 21:29
 */
public class CriteriaMeta<E extends RootEntity> {
    private final Operator operator;
    private final ColumnMeta columnMeta;
    private final Object[] sqlValues;

    private CriteriaMeta(Operator operator, ColumnMeta columnMeta, Object[] sqlValues) {
        this.operator = operator;
        this.columnMeta = columnMeta;
        this.sqlValues = sqlValues;
    }

    public static <E extends RootEntity> CriteriaMeta<E> getInstance(Operator operator, ColumnMeta columnMeta, Object... sqlValues) {
        if (Operator.IN == operator || Operator.NOTIN == operator) {
            ValidationUtil.INSTANCE.notEmpty(sqlValues, "values must not be empty.field: {}", columnMeta.getFieldName());
        }
        for (Object sqlValue : sqlValues) {
            ValidationUtil.INSTANCE.notNull(sqlValue, "value must not be null.field: {}", columnMeta.getFieldName());
        }
        return new CriteriaMeta<>(operator, columnMeta, sqlValues);
    }

    public static <E extends RootEntity> CriteriaMeta<E> getInstance(Operator operator, ColumnMeta columnMeta, Collection<?> sqlValues) {
        return new CriteriaMeta<>(operator, columnMeta, sqlValues.toArray());
    }

    public Operator getOperator() {
        return operator;
    }

    public ColumnMeta getColumnMeta() {
        return columnMeta;
    }

    public Object[] getSqlValues() {
        return sqlValues;
    }

}
