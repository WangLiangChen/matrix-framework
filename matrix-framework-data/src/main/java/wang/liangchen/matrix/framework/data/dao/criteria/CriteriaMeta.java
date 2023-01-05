package wang.liangchen.matrix.framework.data.dao.criteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.Collection;

/**
 * @author Liangchen.Wang 2022-04-16 21:29
 */
public class CriteriaMeta<E extends RootEntity> {
    private final static Logger logger = LoggerFactory.getLogger(CriteriaMeta.class);
    private final AbstractCriteria<E> criteria;
    private final Operator operator;
    private final ColumnMeta columnMeta;
    private final Object[] sqlValues;

    private CriteriaMeta(AbstractCriteria<E> criteria, Operator operator, ColumnMeta columnMeta, Object[] sqlValues) {
        this.criteria = criteria;
        this.operator = operator;
        this.columnMeta = columnMeta;
        this.sqlValues = sqlValues;
    }

    public static <E extends RootEntity> CriteriaMeta<E> getInstance(AbstractCriteria<E> criteria, Operator operator, ColumnMeta columnMeta, Object... sqlValues) {
        if (CollectionUtil.INSTANCE.isEmpty(sqlValues)) {
            logger.debug("* The value of {} contains null,so this criteria '{}' is ignored", columnMeta.getFieldName(), operator.getOperator());
            return null;
        }
        for (Object sqlValue : sqlValues) {
            if (null == sqlValue) {
                logger.debug("* The value of '{}' contains null,so this criteria '{}' is ignored", columnMeta.getFieldName(), operator.getOperator());
                return null;
            }
            if (sqlValue instanceof String && ((String) sqlValue).isEmpty()) {
                logger.debug("* The value of '{}' is blank,so this criteria '{}' is ignored", columnMeta.getFieldName(), operator.getOperator());
                return null;
            }
        }
        return new CriteriaMeta<>(criteria, operator, columnMeta, sqlValues);
    }

    public static <E extends RootEntity> CriteriaMeta<E> getInstance(AbstractCriteria<E> criteria, Operator operator, ColumnMeta columnMeta, Collection<?> sqlValues) {
        return getInstance(criteria, operator, columnMeta, sqlValues.toArray());
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
