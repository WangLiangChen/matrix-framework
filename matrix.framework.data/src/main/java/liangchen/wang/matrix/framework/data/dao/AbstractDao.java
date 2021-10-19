package liangchen.wang.matrix.framework.data.dao;


import liangchen.wang.matrix.framework.commons.exception.MatrixInfoException;
import org.hibernate.metamodel.model.domain.internal.EntityTypeImpl;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2021-10-19 18:35
 */
public abstract class AbstractDao<E extends RootEntity, Q extends RootQuery> {
    @Inject
    protected JdbcTemplate jdbcTemplate;
    @Inject
    protected SqlSessionTemplate sqlSessionTemplate;
    @PersistenceContext
    protected EntityManager entityManager;
    private final Class<E> entityClass;
    private final Class<Q> queryClass;
    private final EntityMeta entityMeta;
    private final MybatisStatementIdBuilder mybatisStatementIdBuilder;
    private final static String EXCEPTION = "Type must be ParameterizedType '<E extends RootEntity, Q extends RootQuery>'";

    @SuppressWarnings({"unchecked"})
    public AbstractDao() {
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

        Metamodel metamodel = entityManager.getMetamodel();
        EntityTypeImpl entityType = (EntityTypeImpl) metamodel.entity(entityClass);
        String tableName = entityType.getName();
        Set<?> attributes = entityType.getAttributes();
        Map<Boolean, Set<String>> fieldMap = attributes.stream().map(e -> (SingularAttribute) e).collect(Collectors.groupingBy(SingularAttribute::isId, Collectors.mapping(SingularAttribute::getName, Collectors.toSet())));
        entityMeta = new EntityMeta(tableName, fieldMap.get(Boolean.TRUE), fieldMap.get(Boolean.FALSE));
        mybatisStatementIdBuilder = new MybatisStatementIdBuilder(sqlSessionTemplate, entityMeta);
    }
}
