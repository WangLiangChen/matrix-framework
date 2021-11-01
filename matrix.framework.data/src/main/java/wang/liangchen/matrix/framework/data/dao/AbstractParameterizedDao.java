package wang.liangchen.matrix.framework.data.dao;


import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.pagination.PaginationResult;
import wang.liangchen.matrix.framework.data.query.RootQuery;

import javax.inject.Inject;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Liangchen.Wang 2021-10-19 18:35
 */
public abstract class AbstractParameterizedDao<E extends RootEntity, Q extends RootQuery> extends AbstractDao {
    private final Class<E> entityClass;
    //private final Class<Q> queryClass;
    @Inject
    private StandaloneDao standaloneDao;
    private final static String EXCEPTION = "Type must be ParameterizedType '<E extends RootEntity, Q extends RootQuery>'";

    @SuppressWarnings({"unchecked"})
    public AbstractParameterizedDao() {
        Type thisType = getClass().getGenericSuperclass();
        if (!(thisType instanceof ParameterizedType)) {
            throw new MatrixInfoException(EXCEPTION);
        }
        Type[] argTypes = ((ParameterizedType) thisType).getActualTypeArguments();
        if (argTypes.length < 2) {
            throw new MatrixInfoException(EXCEPTION);
        }
        entityClass = (Class<E>) argTypes[0];
        //queryClass = (Class<Q>) argTypes[1];
    }

    public int insert(RootEntity entity) {
        return standaloneDao.insert(entity);
    }

    public int insertBatch(List<? extends RootEntity> entities) {
        return standaloneDao.insertBatch(entities);
    }

    public int deleteByQuery(RootQuery query) {
        return standaloneDao.deleteByQuery(query);
    }

    public int delete(RootEntity entity) {
        return standaloneDao.delete(entity);
    }

    public int updateByQuery(RootEntity entity, RootQuery query) {
        return standaloneDao.updateByQuery(entity, query);
    }

    public int update(RootEntity entity) {
        return standaloneDao.update(entity);
    }

    public List<E> list(RootQuery query, String... columns) {
        return standaloneDao.list(entityClass, query, columns);
    }

    public int count(RootQuery query) {
        return standaloneDao.count(query);
    }

    public PaginationResult<E> pagination(RootQuery query, String... columns) {
        return standaloneDao.pagination(entityClass, query, columns);
    }
}
