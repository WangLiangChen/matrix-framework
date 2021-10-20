package liangchen.wang.matrix.framework.data.dao;


import liangchen.wang.matrix.framework.data.dao.entity.EntityMeta;
import liangchen.wang.matrix.framework.data.dao.entity.RootEntity;
import org.hibernate.metamodel.model.domain.internal.EntityTypeImpl;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2021-10-19 18:35
 */
public abstract class AbstractDao {
    private final static Logger logger = LoggerFactory.getLogger(AbstractDao.class);
    @Inject
    protected JdbcTemplate jdbcTemplate;
    @Inject
    protected SqlSessionTemplate sqlSessionTemplate;
    @PersistenceContext
    protected EntityManager entityManager;
    private final Map<Class<? extends RootEntity>, EntityMeta> entityMetaCache = new ConcurrentHashMap<>(128);

    protected EntityMeta entityMeta(Class<? extends RootEntity> entityClass) {
        EntityMeta entityMeta = entityMetaCache.computeIfAbsent(entityClass, key -> {
            Metamodel metamodel = entityManager.getMetamodel();
            EntityTypeImpl entityType = (EntityTypeImpl) metamodel.entity(entityClass);
            String tableName = entityType.getName();
            Set<?> attributes = entityType.getAttributes();
            Map<Boolean, Set<String>> fieldMap = attributes.stream().map(e -> (SingularAttribute) e).collect(Collectors.groupingBy(SingularAttribute::isId, Collectors.mapping(SingularAttribute::getName, Collectors.toSet())));
            logger.debug("create entity meta:{}", entityClass.getName());
            return new EntityMeta(tableName, fieldMap.get(Boolean.TRUE), fieldMap.get(Boolean.FALSE));
        });
        return entityMeta;
    }
}
