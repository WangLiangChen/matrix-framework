package wang.liangchen.matrix.framework.spring.data.criteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

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
        // the values is column name when valueIsColumnName is true
        this.valueIsColumnName = valueIsColumnName;
        this.ignoreCase = ignoreCase;
        if (Operator.ISNULL == operator || Operator.ISNOTNULL == operator) {
            this.values = new Object[0];
            return;
        }
        this.values = Arrays.stream(values).filter(Objects::nonNull).toArray();
        if (CollectionUtil.INSTANCE.isEmpty(values)) {
            throw new MatrixErrorException("The values of column " + columnName + " is empty.");
        }
    }


    protected static SingleCriteriaResolver newInstance(String columnName, Operator operator, Object... sqlValues) {
        return new SingleCriteriaResolver(columnName, operator, false, false, sqlValues) {
        };
    }

    protected static SingleCriteriaResolver newInstance(String columnName, Operator operator, Boolean valueIsColumnName, Boolean ignoreCase, String... sqlValues) {
        Object[] objectValues = Arrays.copyOf(sqlValues, sqlValues.length);
        return new SingleCriteriaResolver(columnName, operator, valueIsColumnName, ignoreCase, objectValues) {
        };
    }

    protected static SingleCriteriaResolver newInstanceIgnoreCase(String columnName, Operator operator, String... sqlValues) {
        return newInstance(columnName, operator, false, true, sqlValues);
    }

    protected static SingleCriteriaResolver newInstanceValuesIsColumnName(String columnName, Operator operator, String... sqlValues) {
        return newInstance(columnName, operator, true, false, sqlValues);
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
