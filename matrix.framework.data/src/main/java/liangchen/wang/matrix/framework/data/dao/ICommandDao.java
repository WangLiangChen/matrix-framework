package liangchen.wang.matrix.framework.data.dao;


import liangchen.wang.matrix.framework.data.dao.entity.RootEntity;
import liangchen.wang.matrix.framework.data.query.RootQuery;

import java.util.List;

/**
 * @author LiangChen.Wang
 */
public interface ICommandDao<E extends RootEntity, Q extends RootQuery> {

    boolean insert(E entity);

    int insertBatch(List<E> entities);

    int deleteByQuery(Q query);

    int updateByQuery(E entity, Q query);

}
