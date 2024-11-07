package wang.liangchen.matrix.framework.data.criteria;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2023-04-10 21:29
 */
abstract class ComposedCriteriaResolver extends AbstractCriteriaResolver {
    private final List<AbstractCriteriaResolver> children = new ArrayList<>() {
        @Override
        public boolean add(AbstractCriteriaResolver criteriaResolver) {
            if (null == criteriaResolver) {
                return false;
            }
            return super.add(criteriaResolver);
        }
    };
    private final static String MERGED_KEY_PATTERN = "%s_%d";
    private final static String WHERE_SQLVALUES_PATTERN = "#{whereSqlValues.%s}";
    private final Map<String, Object> mergedValues = new LinkedHashMap<>();
    private String whereSql;

    private ComposedCriteriaResolver(AndOr andOr) {
        super(andOr);
    }

    protected static ComposedCriteriaResolver newInstance(AndOr andOr) {
        return new ComposedCriteriaResolver(andOr) {
        };
    }

    protected static ComposedCriteriaResolver newInstance() {
        return new ComposedCriteriaResolver(AndOr.and) {
        };
    }

    protected void add(OrCriteriaResolver orCriteriaResolver) {
        this.children.add(orCriteriaResolver);
    }

    protected void add(SingleCriteriaResolver singleCriteriaResolver) {
        this.children.add(singleCriteriaResolver);
    }

    protected void add(ComposedCriteriaResolver composedCriteriaResolver) {
        this.children.add(composedCriteriaResolver);
    }

    protected List<AbstractCriteriaResolver> getChildren() {
        return children;
    }


    protected String resolveWhereSql(AbstractCriteriaResolver abstractCriteriaResolver) {
        // 多次同样调用,不再resolve直接返回.非线程安全
        if (null != whereSql) {
            return whereSql;
        }
        whereSql = recursionResolveWhereSql(abstractCriteriaResolver, null);
        return whereSql;
    }

    private String recursionResolveWhereSql(AbstractCriteriaResolver abstractCriteriaResolver, AbstractCriteriaResolver previousAbstractCriteriaResolver) {
        if (null == abstractCriteriaResolver) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        if (abstractCriteriaResolver instanceof OrCriteriaResolver) {
            concatAndOr(abstractCriteriaResolver, previousAbstractCriteriaResolver, builder);
            return builder.toString();
        }

        if (abstractCriteriaResolver instanceof ComposedCriteriaResolver) {
            ComposedCriteriaResolver composedCriteriaResolver = (ComposedCriteriaResolver) abstractCriteriaResolver;
            List<AbstractCriteriaResolver> items = composedCriteriaResolver.getChildren();
            // filter null
            items = items.stream().filter(Objects::nonNull).collect(Collectors.toList());

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
            return builder.toString();
        }
        // single criteria
        concatAndOr(abstractCriteriaResolver, previousAbstractCriteriaResolver, builder);
        SingleCriteriaResolver singleCriteriaResolver = (SingleCriteriaResolver) abstractCriteriaResolver;
        Operator operator = singleCriteriaResolver.getOperator();
        String columnName = singleCriteriaResolver.getColumnName();
        Boolean ignoreCase = singleCriteriaResolver.getIgnoreCase();
        // ignore case,use upper on column
        if (Boolean.TRUE.equals(ignoreCase)) {
            builder.append("upper(".concat(columnName).concat(")"));
        } else {
            builder.append(columnName);
        }

        builder.append(operator.getOperator());
        Object[] values = singleCriteriaResolver.getValues();
        if (Boolean.TRUE.equals(singleCriteriaResolver.getValueIsColumnName())) {
            // Compatible between
            builder.append(Arrays.stream(values).map(String::valueOf).collect(Collectors.joining(AndOr.and.getSymbol())));
            return builder.toString();
        }
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
                // ignore case
                handleIgnoreCase(builder, placeholders[0], ignoreCase);
                break;
            case STARTWITH:
            case NOTSTARTWITH:
                mergedValues.replace(mergedKey, String.format("%s%%", values[0]));
                // ignore case
                handleIgnoreCase(builder, placeholders[0], ignoreCase);
                break;
            case ENDWITH:
            case NOTENDWITH:
                mergedValues.replace(mergedKey, String.format("%%%s", values[0]));
                // ignore case
                handleIgnoreCase(builder, placeholders[0], ignoreCase);
                break;
            default:
                // ignore case
                handleIgnoreCase(builder, placeholders[0], ignoreCase);
                break;
        }

        return builder.toString();
    }


    private void handleIgnoreCase(StringBuilder builder, String placeholder, Boolean ignoreCase) {
        if (Boolean.TRUE.equals(ignoreCase)) {
            builder.append("upper(").append(placeholder).append(")");
        } else {
            builder.append(placeholder);
        }
    }

    public void concatAndOr(AbstractCriteriaResolver abstractCriteriaResolver, AbstractCriteriaResolver previousAbstractCriteriaResolver, StringBuilder builder) {
        if (null != previousAbstractCriteriaResolver && !(previousAbstractCriteriaResolver instanceof OrCriteriaResolver)) {
            builder.append(abstractCriteriaResolver.getAndOr().getSymbol());
        }
    }
}
