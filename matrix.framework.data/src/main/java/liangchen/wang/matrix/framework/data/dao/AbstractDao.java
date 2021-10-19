package liangchen.wang.matrix.framework.data.dao;


/**
 * @author Liangchen.Wang 2021-10-19 18:35
 */
public abstract class AbstractDao<E extends RootEntity, Q extends RootQuery> implements ICommandDao<E, Q>, IQueryDao<E, Q> {

}
