package wang.liangchen.matrix.framework.data.dao.criteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.exception.MatrixRuntimeException;

import java.util.Arrays;

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
        if (Operator.ISNULL == operator || Operator.ISNOTNULL == operator) {
            this.values = new Object[0];
            this.valueIsColumnName = Boolean.FALSE;
            this.ignoreCase = Boolean.FALSE;
            return;
        }
        if (CollectionUtil.INSTANCE.isEmpty(values)) {
            throw new MatrixRuntimeException("The values can't be empty");
        }
        for (Object value : values) {
            if (null == value) {
                throw new MatrixRuntimeException("The value in the values can't be null");
            }
        }

        this.values = values;
        this.valueIsColumnName = valueIsColumnName;
        this.ignoreCase = ignoreCase;
    }


    protected static SingleCriteriaResolver newInstance(String columnName, Operator operator, Boolean valueIsColumnName, Object... sqlValues) {
        return new SingleCriteriaResolver(columnName, operator, valueIsColumnName, Boolean.FALSE, sqlValues) {
        };
    }

    protected static SingleCriteriaResolver newInstance(String columnName, Operator operator, Object... sqlValues) {
        return new SingleCriteriaResolver(columnName, operator, Boolean.FALSE, Boolean.FALSE, sqlValues) {
        };
    }

    protected static SingleCriteriaResolver newInstance(String columnName, Operator operator, Boolean valueIsColumnName, String... sqlValues) {
        Object[] objects = Arrays.copyOf(sqlValues,sqlValues.length);
        return new SingleCriteriaResolver(columnName, operator, valueIsColumnName, Boolean.TRUE, objects) {
        };
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
