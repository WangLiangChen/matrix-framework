package liangchen.wang.matrix.framework.data.dao;


import liangchen.wang.matrix.framework.data.pagination.PaginationResult;

import java.util.List;
import java.util.Optional;

/**
 * @author LiangChen.Wang
 */
public interface ICommandDao<E extends RootEntity, Q extends RootQuery> {

    boolean insert(E entity);

    int insertBatch(List<E> entities);

    int deleteByQuery(Q query);

    int updateByQuery(E entity, Q query);

}
