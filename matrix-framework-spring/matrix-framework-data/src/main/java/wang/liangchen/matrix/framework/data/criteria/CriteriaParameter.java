package wang.liangchen.matrix.framework.data.criteria;

import jakarta.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.encryption.DigestSignUtil;
import wang.liangchen.matrix.framework.commons.encryption.enums.DigestAlgorithm;
import wang.liangchen.matrix.framework.commons.jackson.JacksonUtil;
import wang.liangchen.matrix.framework.data.entity.RootEntity;
import wang.liangchen.matrix.framework.data.resolver.EntityMeta;
import wang.liangchen.matrix.framework.data.resolver.EntityResolver;
import wang.liangchen.matrix.framework.data.resolver.FieldMeta;

import java.util.Map;

/**
 * @author Liangchen.Wang 2022-04-17 23:14
 */
public class CriteriaParameter<E extends RootEntity> extends QueryParameter {
    private final static Logger logger = LoggerFactory.getLogger(CriteriaParameter.class);
    @Transient
    private transient final E entity;
    @Transient
    private transient final Class<E> entityClass;
    @Transient
    private transient final EntityMeta entityMeta;
    @Transient
    private transient final Map<String, FieldMeta> fieldMetas;
    @Transient
    private transient String softDeleteColumnValue;


    @SuppressWarnings("unchecked")
    public CriteriaParameter(E entity) {
        this.entity = entity;
        this.entityClass = (Class<E>) entity.getClass();
        this.entityMeta = EntityResolver.INSTANCE.resolveEntity(entityClass);
        this.fieldMetas = this.entityMeta.getFieldMetas();
        setTableName(this.entityMeta.getTableName());
    }

    public CriteriaParameter(Class<E> entityClass) {
        this.entity = null;
        this.entityClass = entityClass;
        this.entityMeta = EntityResolver.INSTANCE.resolveEntity(entityClass);
        this.fieldMetas = this.entityMeta.getFieldMetas();
        setTableName(this.entityMeta.getTableName());
    }

    public String cacheKey() {
        String cacheKey = JacksonUtil.INSTANCE.writeValueAsString(this);
        logger.debug("CriteriaParameter cacheKey: {}", cacheKey);
        return DigestSignUtil.INSTANCE.digest(DigestAlgorithm.MD5, cacheKey);
    }


    public E getEntity() {
        return entity;
    }

    public Class<? extends RootEntity> getEntityClass() {
        return entityClass;
    }

    public EntityMeta getEntityMeta() {
        return entityMeta;
    }

    public String getSoftDeleteColumnValue() {
        return softDeleteColumnValue;
    }

    public void setSoftDeleteColumnValue(String softDeleteColumnValue) {
        this.softDeleteColumnValue = softDeleteColumnValue;
    }
}
