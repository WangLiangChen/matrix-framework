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
import java.util.Collection;
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

    @Override
    public <E extends RootEntity> int insert(E entity) {
        return standaloneDao.insert(entity);
    }

    @Override
    public <E extends RootEntity> int insert(Collection<E> entities) {
        return standaloneDao.insert(entities);
    }

    @Override
    public <E extends RootEntity> int delete(E entity) {
        return standaloneDao.delete(entity);
    }

    @Override
    public <E extends RootEntity> int delete(SubCriteria<E> subCriteria) {
        return standaloneDao.delete(subCriteria);
    }

    @Override
    public <E extends RootEntity> int update(E entity) {
        return standaloneDao.update(entity);
    }

    @Override
    public <E extends RootEntity> int update(UpdateCriteria<E> updateCriteria) {
        return standaloneDao.update(updateCriteria);
    }

    @Override
    public <E extends RootEntity> E select(Criteria<E> criteria) {
        return standaloneDao.select(criteria);
    }

    @Override
    public <E extends RootEntity> int count(Criteria<E> criteria) {
        return standaloneDao.count(criteria);
    }

    @Override
    public <E extends RootEntity> List<E> list(Criteria<E> criteria) {
        return standaloneDao.list(criteria);
    }

    @Override
    public <E extends RootEntity> PaginationResult<E> pagination(Criteria<E> criteria) {
        return standaloneDao.pagination(criteria);
    }
}
