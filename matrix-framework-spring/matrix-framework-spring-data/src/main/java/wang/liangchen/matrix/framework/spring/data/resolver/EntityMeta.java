package wang.liangchen.matrix.framework.spring.data.resolver;

import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.spring.data.annotation.IdStrategy;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LiangChen.Wang 2024/10/15 18:01
 */
public final class EntityMeta {
    private final Class<?> entityClass;
    private final String tableName;
    private final Map<String, FieldMeta> fieldMetas = new LinkedHashMap<>();
    private final Map<String, FieldMeta> pkFieldMetas = new LinkedHashMap<>();
    private final Map<String, FieldMeta> nonPkFieldMetas = new LinkedHashMap<>();
    private final FieldMeta pkFieldMeta;
    private FieldMeta softDeleteFieldMeta;
    private FieldMeta versionFieldMeta;


    public EntityMeta(Class<?> entityClass, String tableName, List<FieldMeta> fieldsMetas) {
        this.entityClass = entityClass;
        this.tableName = tableName;
        fieldsMetas.forEach(fieldMeta -> {
            String fieldName = fieldMeta.getFieldName();
            this.fieldMetas.put(fieldName, fieldMeta);
            if (fieldMeta.checkFieldLabel(FieldLabel.ID)) {
                this.pkFieldMetas.put(fieldName, fieldMeta);
                return;
            }
            this.nonPkFieldMetas.put(fieldName, fieldMeta);
            if (fieldMeta.checkFieldLabel(FieldLabel.SOFT_DELETE)) {
                this.softDeleteFieldMeta = fieldMeta;
            }
            if (fieldMeta.checkFieldLabel(FieldLabel.VERSION)) {
                this.versionFieldMeta = fieldMeta;
            }
        });
        if (this.pkFieldMetas.size() == 1) {
            this.pkFieldMeta = this.pkFieldMetas.values().iterator().next();
            return;
        }
        this.pkFieldMeta = null;
        // if composite primary key, id strategy must not be AUTO_INCREMENT
        long count = this.pkFieldMetas.values().stream().filter(fieldMeta -> IdStrategy.Strategy.AUTO_INCREMENT == fieldMeta.getIdStrategy()).count();
        if (count > 0) {
            throw new MatrixErrorException("Entity class '{}' has composite key, which does not support auto increment", entityClass.getName());
        }
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getTableName() {
        return tableName;
    }

    public Map<String, FieldMeta> getFieldMetas() {
        return fieldMetas;
    }

    public Map<String, FieldMeta> getPkFieldMetas() {
        return pkFieldMetas;
    }

    public Map<String, FieldMeta> getNonPkFieldMetas() {
        return nonPkFieldMetas;
    }

    public FieldMeta getPkFieldMeta() {
        return pkFieldMeta;
    }

    public FieldMeta getSoftDeleteFieldMeta() {
        return softDeleteFieldMeta;
    }

    public FieldMeta getVersionFieldMeta() {
        return versionFieldMeta;
    }
}
