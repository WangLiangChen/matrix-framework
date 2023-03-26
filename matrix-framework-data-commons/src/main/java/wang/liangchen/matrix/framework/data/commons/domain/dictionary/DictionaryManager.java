package wang.liangchen.matrix.framework.data.commons.domain.dictionary;

import jakarta.inject.Inject;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;

/**
 * @author Liangchen.Wang 2023-03-26 11:59
 */
public class DictionaryManager {
    private final StandaloneDao standaloneDao;

    @Inject
    public DictionaryManager(StandaloneDao standaloneDao) {
        this.standaloneDao = standaloneDao;
    }

    public void add(Dictionary entity) {
        this.standaloneDao.insert(entity);
    }
}
