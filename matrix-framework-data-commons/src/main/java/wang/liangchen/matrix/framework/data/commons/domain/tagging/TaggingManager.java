package wang.liangchen.matrix.framework.data.commons.domain.tagging;

import wang.liangchen.matrix.framework.data.dao.StandaloneDao;

import javax.inject.Inject;

/**
 * @author Liangchen.Wang 2023-03-27 10:42
 */
public class TaggingManager {
    private final StandaloneDao standaloneDao;

    @Inject
    public TaggingManager(StandaloneDao standaloneDao) {
        this.standaloneDao = standaloneDao;
    }

    public void tagging(Tagging entity) {
        this.standaloneDao.insert(entity);
    }
}
