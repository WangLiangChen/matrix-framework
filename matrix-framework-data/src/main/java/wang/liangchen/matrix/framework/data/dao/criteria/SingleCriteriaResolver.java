package wang.liangchen.matrix.framework.data.dao.criteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;

/**
 * @author Liangchen.Wang 2023-04-10 21:29
 */
abstract class SingleCriteriaResolver extends AbstractCriteriaResolver {
    private final static Logger logger = LoggerFactory.getLogger(SingleCriteriaResolver.class);
    private final String columnName;
    private final Operator operator;
    private final Object[] values;
    private final Boolean valueIsColumnName;

    private SingleCriteriaResolver(String columnName, Operator operator, Boolean valueIsColumnName, Object... values) {
        super(AndOr.and);
        this.columnName = columnName;
        this.operator = operator;
        this.values = values;
        this.valueIsColumnName = valueIsColumnName;
    }

    protected static SingleCriteriaResolver newInstance(String columnName, Operator operator, Boolean valueIsColumnName, Object... sqlValues) {
        if (Operator.ISNULL == operator || Operator.ISNOTNULL == operator) {
            return new SingleCriteriaResolver(columnName, operator, Boolean.FALSE) {
            };
        }
        if (CollectionUtil.INSTANCE.isEmpty(sqlValues)) {
            logger.debug("* The value of {} contains null,so this criteria '{}' is ignored", columnName, operator.getOperator());
            return null;
        }
        for (Object sqlValue : sqlValues) {
            if (null == sqlValue) {
                logger.debug("* The value of '{}' contains null,so this criteria '{}' is ignored", columnName, operator.getOperator());
                return null;
            }
            if (sqlValue instanceof String && ((String) sqlValue).isEmpty()) {
                logger.debug("* The value of '{}' is blank,so this criteria '{}' is ignored", columnName, operator.getOperator());
                return null;
            }
        }
        return new SingleCriteriaResolver(columnName, operator, valueIsColumnName, sqlValues) {
        };
    }

    protected static SingleCriteriaResolver newInstance(String columnName, Operator operator, Object... sqlValues) {
        return newInstance(columnName, operator, Boolean.FALSE, sqlValues);
    }


    protected String getColumnName() {
        return columnName;
    }

    protected Operator getOperator() {
        return operator;
    }

    protected Object[] getValues() {
        return values;
    }

    protected Boolean getValueIsColumnName() {
        return valueIsColumnName;
    }
}
