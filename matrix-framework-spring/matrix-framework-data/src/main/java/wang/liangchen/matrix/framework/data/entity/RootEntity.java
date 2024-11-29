package wang.liangchen.matrix.framework.data.entity;

import jakarta.persistence.Transient;
import wang.liangchen.matrix.framework.commons.object.EnhancedObject;
import wang.liangchen.matrix.framework.data.resolver.EntityGetter;
import wang.liangchen.matrix.framework.data.resolver.EntityMeta;
import wang.liangchen.matrix.framework.data.resolver.EntityResolver;
import wang.liangchen.matrix.framework.data.resolver.FieldMeta;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author LiangChen.Wang 2024/10/12 16:27
 */
public abstract class RootEntity extends EnhancedObject {
    @Transient
    private transient final Set<String> updateToNullColumns = new HashSet<>();
    @Transient
    private transient final EntityMeta entityMeta;

    protected RootEntity() {
        this.entityMeta = EntityResolver.INSTANCE.resolveEntity(this.getClass());
    }

    public void addUpdateToNullColumn(String columnName) {
        this.updateToNullColumns.add(columnName);
    }

    public <E extends RootEntity> void addUpdateToNullColumn(EntityGetter<E> entityGetter) {
        this.updateToNullColumns.add(resolveColumnName(entityGetter));
    }

    public void removeUpdateToNullColumn(String columnName) {
        this.updateToNullColumns.remove(columnName);
    }

    public <E extends RootEntity> void removeUpdateToNullColumn(EntityGetter<E> entityGetter) {
        this.updateToNullColumns.remove(resolveColumnName(entityGetter));
    }

    public void addUpdateToNullColumns(Collection<String> updateToNullColumns) {
        this.updateToNullColumns.addAll(updateToNullColumns);
    }

    private <E extends RootEntity> String resolveColumnName(EntityGetter<E> entityGetter) {
        String fieldName = entityGetter.getFieldName();
        Map<String, FieldMeta> fieldMetas = this.entityMeta.getFieldMetas();
        return fieldMetas.get(fieldName).getColumnName();
    }

    public Set<String> getUpdateToNullColumns() {
        return updateToNullColumns;
    }

    public EntityMeta getEntityMeta() {
        return entityMeta;
    }

    @Override
    public String toString() {
        return "RootEntity{" +
                "updateToNullColumns=" + updateToNullColumns +
                '}';
    }
}
