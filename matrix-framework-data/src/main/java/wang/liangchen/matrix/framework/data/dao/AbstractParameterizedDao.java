package wang.liangchen.matrix.framework.data.dao;


import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.data.dao.criteria.Criteria;
import wang.liangchen.matrix.framework.data.dao.criteria.SubCriteria;
import wang.liangchen.matrix.framework.data.dao.criteria.UpdateCriteria;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.pagination.PaginationResult;

import javax.inject.Inject;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Liangchen.Wang 2021-10-19 18:35
 */
public abstract class AbstractParameterizedDao<E extends RootEntity> extends AbstractDao {
    private final Class<E> entityClass;
    @Inject
    private StandaloneDao standaloneDao;
    private final static String EXCEPTION = "Type must be ParameterizedType '<E extends RootEntity>'";

    @SuppressWarnings({"unchecked"})
    public AbstractParameterizedDao() {
        Type thisType = getClass().getGenericSuperclass();
        if (!(thisType instanceof ParameterizedType)) {
            throw new MatrixInfoException(EXCEPTION);
        }
        Type[] argTypes = ((ParameterizedType) thisType).getActualTypeArguments();
        if (argTypes.length < 1) {
            throw new MatrixInfoException(EXCEPTION);
        }
        entityClass = (Class<E>) argTypes[0];
    }

    public int insert(E entity) {
        return standaloneDao.insert(entity);
    }

    public int insert(List<E> entities) {
        return standaloneDao.insert(entities);
    }

    public int delete(E entity) {
        return standaloneDao.delete(entity);
    }

    public int delete(SubCriteria<E> subCriteria) {
        return standaloneDao.delete(subCriteria);
    }

    public int update(E entity) {
        return standaloneDao.update(entity);
    }

    public int update(UpdateCriteria<E> updateCriteria) {
        return standaloneDao.update(updateCriteria);
    }


    public List<E> list(Criteria<E> criteria) {
        return standaloneDao.list(criteria);

    }

    public int count(Criteria<E> criteria) {
        return standaloneDao.count(criteria);
    }

    public PaginationResult<E> pagination(Criteria<E> criteria) {
        return standaloneDao.pagination(criteria);
    }
}
