package liangchen.wang.matrix.framework.data.core;


import liangchen.wang.matrix.framework.data.pagination.PaginationResult;

import java.util.List;
import java.util.Optional;

/**
 * @author LiangChen.Wang
 */
public interface IDao<E extends RootEntity, Q extends RootQuery> {

    boolean insert(E entity);

    int insertBatch(List<E> entitys);

    int deleteByQuery(Q query);

    int updateByQuery(E entity, Q query);

    boolean exist(Q query);

    int count(Q query);

    E one(Q query, String... returnFields);

    Optional<E> oneOptional(Q query, String... returnFields);

    List<E> list(Q query, String... returnFields);

    PaginationResult<E> pagination(Q query, String... returnFields);
}
