package wang.liangchen.matrix.framework.data.commons.domain.dictionary;

import jakarta.inject.Inject;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.data.dao.criteria.Criteria;
import wang.liangchen.matrix.framework.data.dao.criteria.DeleteCriteria;
import wang.liangchen.matrix.framework.data.pagination.PaginationResult;

import java.util.List;

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
        DictionaryKey.populateKey(entity);
        this.standaloneDao.insert(entity);
    }

    public void remove(DictionaryKey dictionaryKey) {
        DeleteCriteria<Dictionary> deleteCriteria = DeleteCriteria.of(Dictionary.class)
                ._equals(Dictionary::getDictionaryKey, dictionaryKey.toKeyString());
        this.standaloneDao.delete(deleteCriteria);
    }

    public PaginationResult<Dictionary> pagination(String dictionaryGroup, int pageNumber, int pageSize) {
        Criteria<Dictionary> criteria = Criteria.of(Dictionary.class)
                .pageNumber(pageNumber).pageSize(pageSize)
                ._equals(Dictionary::getDictionaryGroup, dictionaryGroup);
        return this.standaloneDao.pagination(criteria);
    }

    public List<Dictionary> pagination(String dictionaryGroup) {
        Criteria<Dictionary> criteria = Criteria.of(Dictionary.class)
                ._equals(Dictionary::getDictionaryGroup, dictionaryGroup);
        return this.standaloneDao.list(criteria);
    }

    public void update(Dictionary entity) {
        this.standaloneDao.update(entity);
    }
}
