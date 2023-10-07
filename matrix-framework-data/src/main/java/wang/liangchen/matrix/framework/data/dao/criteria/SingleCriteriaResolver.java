package wang.liangchen.matrix.framework.data.dao.criteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Liangchen.Wang 2023-04-10 21:29
 */
abstract class SingleCriteriaResolver extends AbstractCriteriaResolver {
    private final static Logger logger = LoggerFactory.getLogger(SingleCriteriaResolver.class);
    private final String columnName;
    private final Operator operator;
    private final Object[] values;
    private final Boolean valueIsColumnName;
    private final Boolean ignoreCase;

    private SingleCriteriaResolver(String columnName, Operator operator, Boolean valueIsColumnName, Boolean ignoreCase, Object... values) {
        super(AndOr.and);
        this.columnName = columnName;
        this.operator = operator;

        this.values = values;
        this.valueIsColumnName = valueIsColumnName;
        this.ignoreCase = ignoreCase;
    }

    protected static SingleCriteriaResolver newInstance(String columnName, Operator operator, Boolean valueIsColumnName, Boolean ignoreCase, Object... sqlValues) {
        // 无需sqlValues
        if (Operator.ISNULL == operator || Operator.ISNOTNULL == operator) {
            return new SingleCriteriaResolver(columnName, operator, Boolean.FALSE, Boolean.FALSE, sqlValues) {
            };
        }
        if (CollectionUtil.INSTANCE.isEmpty(sqlValues)) {
            logger.info("The sqlValues can't be empty,ignored");
            return null;
        }
        // 剔除null,并再次判空
        sqlValues = Arrays.stream(sqlValues).filter(Objects::nonNull).toArray();
        if (CollectionUtil.INSTANCE.isEmpty(sqlValues)) {
            logger.info("The Null values are filtered and The sqlValues can't be empty,ignored");
            return null;
        }
        return new SingleCriteriaResolver(columnName, operator, valueIsColumnName, ignoreCase, sqlValues) {
        };
    }

    protected static SingleCriteriaResolver newInstance(String columnName, Operator operator, Boolean valueIsColumnName, Object... sqlValues) {
        return newInstance(columnName, operator, valueIsColumnName, Boolean.FALSE, sqlValues);
    }

    protected static SingleCriteriaResolver newInstance(String columnName, Operator operator, Object... sqlValues) {
        return newInstance(columnName, operator, Boolean.FALSE, Boolean.FALSE, sqlValues);
    }

    protected static SingleCriteriaResolver newInstance(String columnName, Operator operator, Boolean valueIsColumnName, String... sqlValues) {
        Object[] objects = Arrays.copyOf(sqlValues, sqlValues.length);
        return newInstance(columnName, operator, valueIsColumnName, Boolean.TRUE, objects);
    }

    protected static SingleCriteriaResolver newInstance(String columnName, Operator operator, String... sqlValues) {
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

    public Boolean getIgnoreCase() {
        return ignoreCase;
    }
}
