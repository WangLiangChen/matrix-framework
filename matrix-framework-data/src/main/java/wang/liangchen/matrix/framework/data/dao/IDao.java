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

    /**
     * <pre>
     * delete by primary key
     * If there is a field using annotation '@ColumnMarkDelete',the row will not be physically deleted
     * </pre>
     *
     * @param entity Entity object to delete
     * @param <E>    Subclass of RootEntity
     * @return Number of rows deleted
     */
    <E extends RootEntity> int delete(E entity);

    /**
     * <pre>
     * delete by query criteria
     * If there is a field using annotation '@ColumnMarkDelete',the row will not be physically deleted
     * </pre>
     *
     * @param subCriteria Constructed query criteria
     * @param <E>         Subclass of RootEntity
     * @return Number of rows deleted
     */
    <E extends RootEntity> int delete(SubCriteria<E> subCriteria);

    /**
     * <pre>
     * update by primary key
     * Use method 'addForceUpdateField' or 'addForceUpdateColumn' to add some forced update columns
     * Can be used to update data to null
     * </pre>
     *
     * @param entity Entity object to update
     * @param <E>    Subclass of RootEntity
     * @return Number of rows updated
     */
    <E extends RootEntity> int update(E entity);

    /**
     * <pre>
     * update by query criteria
     * Use method 'forceUpdate' to add some forced update columns
     * Can be used to update data to null
     * </pre>
     *
     * @param updateCriteria Constructed query and update criteria
     * @param <E>            Subclass of RootEntity
     * @return Number of rows updated
     */
    <E extends RootEntity> int update(UpdateCriteria<E> updateCriteria);


    <E extends RootEntity> E select(Criteria<E> criteria);

    <E extends RootEntity> int count(Criteria<E> criteria);

    <E extends RootEntity> List<E> list(Criteria<E> criteria);

    <E extends RootEntity> PaginationResult<E> pagination(Criteria<E> criteria);
}
