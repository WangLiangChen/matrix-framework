package wang.liangchen.matrix.framework.data.criteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.encryption.DigestSignUtil;
import wang.liangchen.matrix.framework.commons.encryption.enums.DigestAlgorithm;
import wang.liangchen.matrix.framework.data.entity.RootEntity;
import wang.liangchen.matrix.framework.data.resolver.EntityMeta;
import wang.liangchen.matrix.framework.data.resolver.EntityResolver;

import java.util.Map;
import java.util.StringJoiner;

/**
 * @author Liangchen.Wang 2022-04-17 23:14
 */
public class CriteriaParameter<E extends RootEntity> extends QueryParameter {
    private final static Logger logger = LoggerFactory.getLogger(CriteriaParameter.class);
    private final E entity;
    private final Class<? extends RootEntity> entityClass;
    private final EntityMeta entityMeta;
    private final String tableName;

    private String driverClassName;
    private String whereSql;
    private Map<String, Object> whereSqlValues;

    private SoftDeleteColumnMeta deleteMeta;
    private VersionColumnMeta versionMeta;


    public CriteriaParameter(E entity) {
        this.entity = entity;
        this.entityClass = entity.getClass();
        this.entityMeta = entity.getEntityMeta();
        this.tableName = this.entityMeta.getTableName();
    }

    public CriteriaParameter(Class<? extends RootEntity> entityClass) {
        this.entity = null;
        this.entityClass = entityClass;
        this.entityMeta = EntityResolver.INSTANCE.resolveEntity(entityClass);
        this.tableName = this.entityMeta.getTableName();
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", "CriteriaParameter[", "]")
                .add("dataSourceType='" + dataSourceType + "'")
                .add("tableName='" + tableName + "'")
                .add("whereSql='" + whereSql + "'")
                .add("whereSqlValues=" + whereSqlValues)
                .add(super.toString()).toString();
    }

    public Object cacheKey() {
        String cacheKey = toString();
        logger.debug("CacheKey: {}", cacheKey);
        cacheKey = DigestSignUtil.INSTANCE.digest(DigestAlgorithm.MD5, cacheKey);
        logger.debug("Digested CacheKey: {}", cacheKey);
        return cacheKey;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getDriverClassName() {
        return driverClassName;
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

    public String getTableName() {
        return tableName;
    }

    public String getWhereSql() {
        return whereSql;
    }

    public void setWhereSql(String whereSql) {
        this.whereSql = whereSql;
    }

    public Map<String, Object> getWhereSqlValues() {
        return whereSqlValues;
    }

    public void setWhereSqlValues(Map<String, Object> whereSqlValues) {
        this.whereSqlValues = whereSqlValues;
    }

    public SoftDeleteColumnMeta getDeleteMeta() {
        return deleteMeta;
    }

    public void setDeleteMeta(SoftDeleteColumnMeta deleteMeta) {
        this.deleteMeta = deleteMeta;
    }

    public VersionColumnMeta getVersionMeta() {
        return versionMeta;
    }

    public void setVersionMeta(VersionColumnMeta versionMeta) {
        this.versionMeta = versionMeta;
    }
}
