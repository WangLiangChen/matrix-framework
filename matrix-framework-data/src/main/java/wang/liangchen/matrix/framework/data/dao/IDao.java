package wang.liangchen.matrix.framework.data.dao;

import wang.liangchen.matrix.framework.data.dao.criteria.Criteria;
import wang.liangchen.matrix.framework.data.dao.criteria.SubCriteria;
import wang.liangchen.matrix.framework.data.dao.criteria.UpdateCriteria;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.pagination.PaginationResult;

import java.util.Collection;
import java.util.List;

/**
 * @author Liangchen.Wang 2022-06-15 7:56
 */
public interface IDao {

    <E extends RootEntity> int insert(E entity);

    <E extends RootEntity> int insert(Collection<E> entities);

    <E extends RootEntity> int delete(E entity);

    <E extends RootEntity> int delete(SubCriteria<E> subCriteria);

    <E extends RootEntity> int update(E entity);

    <E extends RootEntity> int update(UpdateCriteria<E> updateCriteria);

    <E extends RootEntity> int count(Criteria<E> criteria);

    <E extends RootEntity> List<E> list(Criteria<E> criteria);

    <E extends RootEntity> PaginationResult<E> pagination(Criteria<E> criteria);
}
