package wang.liangchen.matrix.framework.data.dao.criteria;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Liangchen.Wang 2023-04-10 21:29
 */
abstract class ComposedCriteriaResolver extends AbstractCriteriaResolver {
    private final List<AbstractCriteriaResolver> children = new ArrayList<AbstractCriteriaResolver>() {
        @Override
        public boolean add(AbstractCriteriaResolver abstractCriteriaResolver) {
            if (null == abstractCriteriaResolver) {
                return false;
            }
            return super.add(abstractCriteriaResolver);
        }
    };

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

    protected String resolveWhereSql() {
        return resolveWhereSql(this, null);
    }
}
