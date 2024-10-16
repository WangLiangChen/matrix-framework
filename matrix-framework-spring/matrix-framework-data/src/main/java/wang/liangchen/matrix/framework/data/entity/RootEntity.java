package wang.liangchen.matrix.framework.data.entity;

import jakarta.persistence.Transient;
import wang.liangchen.matrix.framework.commons.object.EnhancedMap;
import wang.liangchen.matrix.framework.commons.object.EnhancedObject;
import wang.liangchen.matrix.framework.data.resolver.EntityGetter;
import wang.liangchen.matrix.framework.data.resolver.EntityMeta;
import wang.liangchen.matrix.framework.data.resolver.EntityResolver;
import wang.liangchen.matrix.framework.data.resolver.FieldMeta;

import java.util.Map;

/**
 * @author LiangChen.Wang 2024/10/12 16:27
 */
public abstract class RootEntity extends EnhancedObject {
    @Transient
    private transient final Map<String, Object> mandatoryUpdatedColumns = new EnhancedMap<>();
    @Transient
    private transient final EntityMeta entityMeta;

    protected RootEntity() {
        this.entityMeta = EntityResolver.INSTANCE.resolveEntity(this.getClass());
    }

    public void addMandatoryUpdatedColumns(String columnName, Object value) {
        this.mandatoryUpdatedColumns.put(columnName, value);
    }

    public <E extends RootEntity> void addMandatoryUpdatedColumns(EntityGetter<E> entityGetter, Object value) {
        String fieldName = entityGetter.getFieldName();
        Map<String, FieldMeta> fieldMetaMap = entityMeta.getFieldMetaMap();
        String columnName = fieldMetaMap.get(fieldName).getColumnName();
        this.mandatoryUpdatedColumns.put(columnName, value);
    }

    public Map<String, Object> getMandatoryUpdatedColumns() {
        return mandatoryUpdatedColumns;
    }

    public EntityMeta getEntityMeta() {
        return entityMeta;
    }
}
