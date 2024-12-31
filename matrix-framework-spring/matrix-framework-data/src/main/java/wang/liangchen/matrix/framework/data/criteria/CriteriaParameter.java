package wang.liangchen.matrix.framework.data.criteria;

import jakarta.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.encryption.DigestSignUtil;
import wang.liangchen.matrix.framework.commons.encryption.enums.DigestAlgorithm;
import wang.liangchen.matrix.framework.data.entity.RootEntity;
import wang.liangchen.matrix.framework.data.resolver.EntityMeta;
import wang.liangchen.matrix.framework.data.resolver.EntityResolver;

/**
 * @author Liangchen.Wang 2022-04-17 23:14
 */
public class CriteriaParameter<E extends RootEntity> extends QueryParameter {
    private final static Logger logger = LoggerFactory.getLogger(CriteriaParameter.class);
    @Transient
    private transient final Class<E> entityClass;
    @Transient
    private transient final EntityMeta entityMeta;


    @SuppressWarnings("unchecked")
    public CriteriaParameter(E entity) {
        this.entityClass = (Class<E>) entity.getClass();
        this.entityMeta = EntityResolver.INSTANCE.resolveEntity(entityClass);
        setTableName(this.entityMeta.getTableName());
    }

    public CriteriaParameter(Class<E> entityClass) {
        this.entityClass = entityClass;
        this.entityMeta = EntityResolver.INSTANCE.resolveEntity(entityClass);
        setTableName(this.entityMeta.getTableName());
    }

    public String cacheKey() {
        String cacheKey = toString();
        String md5 = DigestSignUtil.INSTANCE.digest(DigestAlgorithm.MD5, cacheKey);
        logger.debug("CriteriaParameter cacheKey, MD5:{}, cacheKey:{}", md5, cacheKey);
        return md5;
    }

    public Class<? extends RootEntity> findEntityClass() {
        return entityClass;
    }

    public EntityMeta findEntityMeta() {
        return entityMeta;
    }
}
