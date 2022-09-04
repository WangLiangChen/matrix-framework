package wang.liangchen.matrix.framework.data.criteria;

import wang.liangchen.matrix.framework.data.dao.criteria.EntityGetter;
import wang.liangchen.matrix.framework.data.dao.criteria.Operator;
import wang.liangchen.matrix.framework.data.dao.criteria.SqlValue;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

/**
 * @author Liangchen.Wang 2022-04-16 21:29
 */
public class CriteriaMeta<E extends RootEntity> {
    private final Operator operator;
    private final EntityGetter<E> column;
    private final Object[] sqlValues;

    private CriteriaMeta(Operator operator, EntityGetter<E> column, Object[] sqlValues) {
        this.operator = operator;
        this.column = column;
        this.sqlValues = sqlValues;
    }

    public static <E extends RootEntity> CriteriaMeta<E> getInstance(Operator operator, EntityGetter<E> column, Object... sqlValues) {
        return new CriteriaMeta<>(operator, column, sqlValues);
    }

    public Operator getOperator() {
        return operator;
    }

    public EntityGetter<E> getColumn() {
        return column;
    }

    public Object[] getSqlValues() {
        return sqlValues;
    }
}