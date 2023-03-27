package wang.liangchen.matrix.framework.data.commons.domain.tagging;

import jakarta.inject.Inject;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.data.dao.criteria.Criteria;
import wang.liangchen.matrix.framework.data.dao.criteria.DeleteCriteria;

import java.util.List;

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

    public void remove(String taggingGroup, String taggingCode, String tableName, String dataId) {
        DeleteCriteria<Tagging> deleteCriteria = DeleteCriteria.of(Tagging.class)
                ._equals(Tagging::getTaggingGroup, taggingGroup)
                ._equals(Tagging::getTaggingCode, taggingCode)
                ._equals(Tagging::getTableName, tableName)
                ._equals(Tagging::getDataId, dataId);
        this.standaloneDao.delete(deleteCriteria);
    }

    public List<Tagging> list(String taggingGroup, String tableName, String dataId) {
        Criteria<Tagging> criteria = Criteria.of(Tagging.class)
                ._equals(Tagging::getTaggingGroup, taggingGroup)
                ._equals(Tagging::getTableName, tableName)
                ._equals(Tagging::getDataId, dataId);
        return this.standaloneDao.list(criteria);
    }
}
