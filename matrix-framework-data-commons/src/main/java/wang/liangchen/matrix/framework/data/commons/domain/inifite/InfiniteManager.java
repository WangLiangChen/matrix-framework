package wang.liangchen.matrix.framework.data.commons.domain.inifite;

import jakarta.inject.Inject;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;

/**
 * @author Liangchen.Wang 2023-03-28 11:45
 */
public class InfiniteManager {
    private final StandaloneDao standaloneDao;

    @Inject
    public InfiniteManager(StandaloneDao standaloneDao) {
        this.standaloneDao = standaloneDao;
    }
}
