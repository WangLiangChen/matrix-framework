package wang.liangchen.matrix.framework.spring.data.repository;

import wang.liangchen.matrix.framework.spring.data.criteria.Criteria;
import wang.liangchen.matrix.framework.spring.data.criteria.DeleteCriteria;
import wang.liangchen.matrix.framework.spring.data.criteria.UpdateCriteria;
import wang.liangchen.matrix.framework.spring.data.entity.RootEntity;
import wang.liangchen.matrix.framework.spring.data.pagination.PaginationResult;

import java.util.Collection;
import java.util.List;

/**
 * @author Liangchen.Wang 2022-06-15 7:56
 */
public interface IRepository {
    <E extends RootEntity> int insert(E entity);

    <E extends RootEntity> int insert(Collection<E> entities);

    <E extends RootEntity> int insert(Collection<E> entities, int batchSize);


    <E extends RootEntity> int delete(E entity);


    <E extends RootEntity> int delete(DeleteCriteria<E> deleteCriteria);


    <E extends RootEntity> int update(E entity);

    <E extends RootEntity> int update(UpdateCriteria<E> updateCriteria);


    <E extends RootEntity> E select(Criteria<E> criteria);

    <E extends RootEntity> int count(Criteria<E> criteria);

    <E extends RootEntity> boolean exists(Criteria<E> criteria);

    <E extends RootEntity> List<E> list(Criteria<E> criteria);

    <E extends RootEntity> PaginationResult<E> pagination(Criteria<E> criteria);
}
