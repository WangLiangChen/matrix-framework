package wang.liangchen.matrix.framework.data.repository;


import jakarta.inject.Inject;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.data.criteria.Criteria;
import wang.liangchen.matrix.framework.data.criteria.DeleteCriteria;
import wang.liangchen.matrix.framework.data.criteria.UpdateCriteria;
import wang.liangchen.matrix.framework.data.entity.RootEntity;
import wang.liangchen.matrix.framework.data.pagination.PaginationResult;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * @author Liangchen.Wang 2021-10-19 18:35
 */
public abstract class AbstractParameterizedRepository<E extends RootEntity> extends AbstractRepository {

    private final Class<E> entityClass;
    @Inject
    private StandaloneRepository standaloneRepository;
    private final static String EXCEPTION = "Type must be ParameterizedType '<E extends RootEntity>'";

    @SuppressWarnings({"unchecked"})
    public AbstractParameterizedRepository() {
        Type thisType = getClass().getGenericSuperclass();
        if (!(thisType instanceof ParameterizedType)) {
            throw new MatrixWarnException(EXCEPTION);
        }
        Type[] argTypes = ((ParameterizedType) thisType).getActualTypeArguments();
        if (argTypes.length < 1) {
            throw new MatrixWarnException(EXCEPTION);
        }
        entityClass = (Class<E>) argTypes[0];
    }

    @Override
    public <E extends RootEntity> int insert(E entity) {
        return standaloneRepository.insert(entity);
    }

    @Override
    public <E extends RootEntity> int insert(Collection<E> entities) {
        return standaloneRepository.insert(entities);
    }

    @Override
    public <E extends RootEntity> int insert(Collection<E> entities, int batchSize) {
        return standaloneRepository.insert(entities, batchSize);
    }

    @Override
    public <E extends RootEntity> int delete(E entity) {
        return standaloneRepository.delete(entity);
    }

    @Override
    public <E extends RootEntity> int delete(DeleteCriteria<E> deleteCriteria) {
        return standaloneRepository.delete(deleteCriteria);
    }

    @Override
    public <E extends RootEntity> int update(E entity) {
        return standaloneRepository.update(entity);
    }

    @Override
    public <E extends RootEntity> int update(UpdateCriteria<E> updateCriteria) {
        return standaloneRepository.update(updateCriteria);
    }

    @Override
    public <E extends RootEntity> E select(Criteria<E> criteria) {
        return standaloneRepository.select(criteria);
    }

    @Override
    public <E extends RootEntity> int count(Criteria<E> criteria) {
        return standaloneRepository.count(criteria);
    }

    @Override
    public <E extends RootEntity> boolean exists(Criteria<E> criteria) {
        return standaloneRepository.exists(criteria);
    }

    @Override
    public <E extends RootEntity> List<E> list(Criteria<E> criteria) {
        return standaloneRepository.list(criteria);
    }

    @Override
    public <E extends RootEntity> PaginationResult<E> pagination(Criteria<E> criteria) {
        return standaloneRepository.pagination(criteria);
    }

}
