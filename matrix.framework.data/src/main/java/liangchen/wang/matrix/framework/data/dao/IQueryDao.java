package liangchen.wang.matrix.framework.data.dao;


import liangchen.wang.matrix.framework.data.dao.entity.RootEntity;
import liangchen.wang.matrix.framework.data.pagination.PaginationResult;
import liangchen.wang.matrix.framework.data.query.RootQuery;

import java.util.List;
import java.util.Optional;

/**
 * @author LiangChen.Wang
 */
public interface IQueryDao<E extends RootEntity, Q extends RootQuery> {

    boolean exist(Q query);

    int count(Q query);

    E one(Q query, String... returnFields);

    Optional<E> oneOptional(Q query, String... returnFields);

    List<E> list(Q query, String... returnFields);

    PaginationResult<E> pagination(Q query, String... returnFields);
}
