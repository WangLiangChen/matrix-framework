package wang.liangchen.matrix.framework.data.dao.criteria;


import wang.liangchen.matrix.framework.commons.enumeration.Symbol;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2023-04-10 21:26
 */
abstract class AbstractCriteriaResolver {
    private final static String MERGED_KEY_PATTERN = "%s_%d";
    private final static String WHERE_SQLVALUES_PATTERN = "#{whereSqlValues.%s}";
    private final AndOr andOr;
    private final Map<String, Object> mergedValues = new LinkedHashMap<>();
    private String whereSql;

    protected AbstractCriteriaResolver(AndOr andOr) {
        this.andOr = andOr;
    }

    protected String resolveWhereSql(AbstractCriteriaResolver abstractCriteriaResolver, AbstractCriteriaResolver previousAbstractCriteriaResolver) {
        // 多次同样调用,不再resolve直接返回.非线程安全
        if (null != whereSql) {
            return whereSql;
        }
        whereSql = recursionResolveWhereSql(abstractCriteriaResolver, previousAbstractCriteriaResolver);
        return whereSql;
    }

    private String recursionResolveWhereSql(AbstractCriteriaResolver abstractCriteriaResolver, AbstractCriteriaResolver previousAbstractCriteriaResolver) {
        if (null == abstractCriteriaResolver) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        if (abstractCriteriaResolver instanceof OrCriteriaResolver) {
            concatAndOr(abstractCriteriaResolver, previousAbstractCriteriaResolver, builder);
        }
        if (abstractCriteriaResolver instanceof ComposedCriteriaResolver) {
            ComposedCriteriaResolver composedCriteriaResolver = (ComposedCriteriaResolver) abstractCriteriaResolver;
            List<AbstractCriteriaResolver> items = composedCriteriaResolver.getChildren();
            int size = items.size();
            if (size > 0) {
                concatAndOr(abstractCriteriaResolver, previousAbstractCriteriaResolver, builder);
                builder.append("(");
                for (int i = 0; i < size; i++) {
                    if (i == 0) {
                        builder.append(recursionResolveWhereSql(items.get(i), null));
                    } else {
                        builder.append(recursionResolveWhereSql(items.get(i), items.get(i - 1)));
                    }
                }
                builder.append(")");
            }
        }
        if (abstractCriteriaResolver instanceof SingleCriteriaResolver) {
            concatAndOr(abstractCriteriaResolver, previousAbstractCriteriaResolver, builder);
            SingleCriteriaResolver singleCriteriaResolver = (SingleCriteriaResolver) abstractCriteriaResolver;
            Operator operator = singleCriteriaResolver.getOperator();
            String columnName = singleCriteriaResolver.getColumnName();
            // ignore case
            if (Boolean.TRUE.equals(singleCriteriaResolver.getIgnoreCase())) {
                builder.append("upper(").append(columnName).append(")");
            } else {
                builder.append(columnName);
            }

            builder.append(operator.getOperator());
            Object[] values = singleCriteriaResolver.getValues();
            if (Boolean.TRUE.equals(singleCriteriaResolver.getValueIsColumnName())) {
                // Compatible between
                builder.append(Arrays.stream(values).map(String::valueOf).collect(Collectors.joining(AndOr.and.getSymbol())));
            } else {
                // 先存储值
                String[] placeholders = new String[values.length];
                String mergedKey = null;
                for (int i = 0; i < values.length; i++) {
                    mergedKey = String.format(MERGED_KEY_PATTERN, columnName, mergedValues.size());
                    mergedValues.put(mergedKey, values[i]);
                    placeholders[i] = String.format(WHERE_SQLVALUES_PATTERN, mergedKey);
                }
                switch (operator) {
                    case IN:
                    case NOTIN:
                        builder.append(Symbol.OPEN_PAREN.getSymbol());
                        builder.append(Arrays.stream(placeholders).collect(Collectors.joining(Symbol.COMMA.getSymbol())));
                        builder.append(Symbol.CLOSE_PAREN.getSymbol());
                        break;
                    case BETWEEN:
                    case NOTBETWEEN:
                        builder.append(placeholders[0]).append(AndOr.and.getSymbol()).append(placeholders[1]);
                        break;
                    case ISNULL:
                    case ISNOTNULL:
                        break;
                    case CONTAINS:
                    case NOTCONTAINS:
                        mergedValues.replace(mergedKey, String.format("%%%s%%", values[0]));
                        builder.append(placeholders[0]);
                        break;
                    case STARTWITH:
                    case NOTSTARTWITH:
                        mergedValues.replace(mergedKey, String.format("%s%%", values[0]));
                        builder.append(placeholders[0]);
                        break;
                    case ENDWITH:
                    case NOTENDWITH:
                        mergedValues.replace(mergedKey, String.format("%%%s", values[0]));
                        builder.append(placeholders[0]);
                        break;
                    default:
                        // ignore case
                        if (Boolean.TRUE.equals(singleCriteriaResolver.getIgnoreCase())) {
                            builder.append("upper(").append(placeholders[0]).append(")");
                        } else {
                            builder.append(placeholders[0]);
                        }
                        break;
                }
            }
        }
        return builder.toString();
    }

    public void concatAndOr(AbstractCriteriaResolver abstractCriteriaResolver, AbstractCriteriaResolver previousAbstractCriteriaResolver, StringBuilder builder) {
        if (null != previousAbstractCriteriaResolver && !(previousAbstractCriteriaResolver instanceof OrCriteriaResolver)) {
            builder.append(abstractCriteriaResolver.getAndOr().getSymbol());
        }
    }

    protected AndOr getAndOr() {
        return andOr;
    }

    protected Map<String, Object> getMergedValues() {
        return mergedValues;
    }
}
