package wang.liangchen.matrix.framework.data.dao.criteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Liangchen.Wang 2023-04-10 21:29
 */
abstract class OrCriteriaResolver extends AbstractCriteriaResolver {
    private final static Logger logger = LoggerFactory.getLogger(OrCriteriaResolver.class);

    private OrCriteriaResolver() {
        super(AndOr.or);
    }

    protected static OrCriteriaResolver newInstance() {
        return new OrCriteriaResolver() {
        };
    }
}
