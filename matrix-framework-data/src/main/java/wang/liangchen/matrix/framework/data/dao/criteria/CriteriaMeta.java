package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

/**
 * @author Liangchen.Wang 2022-04-16 21:29
 */
public class CriteriaMeta<T extends RootEntity> {
    private final Operator operator;
    private final EntityGetter<T> column;
    private final SqlValue[] sqlValues;

    private CriteriaMeta(Operator operator, EntityGetter<T> column, SqlValue[] sqlValues) {
        this.operator = operator;
        this.column = column;
        this.sqlValues = sqlValues;
    }

    public static <T extends RootEntity> CriteriaMeta getInstance(Operator operator, EntityGetter<T> column, SqlValue... sqlValues) {
        return new CriteriaMeta<T>(operator, column, sqlValues);
    }

    public Operator getOperator() {
        return operator;
    }

    public EntityGetter<T> getColumn() {
        return column;
    }

    public SqlValue[] getSqlValues() {
        return sqlValues;
    }
}
