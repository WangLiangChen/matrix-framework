package liangchen.wang.matrix.framework.data.dao;


import liangchen.wang.matrix.framework.commons.exception.MatrixInfoException;
import liangchen.wang.matrix.framework.data.dao.entity.RootEntity;
import liangchen.wang.matrix.framework.data.mybatis.MybatisStatementIdBuilder;
import liangchen.wang.matrix.framework.data.query.RootQuery;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author Liangchen.Wang 2021-10-19 18:35
 */
public abstract class AbstractParameterizedDao<E extends RootEntity, Q extends RootQuery> extends AbstractDao {
    private final Class<E> entityClass;
    private final Class<Q> queryClass;
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
        queryClass = (Class<Q>) argTypes[1];
    }
}
