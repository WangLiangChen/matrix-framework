package wang.liangchen.matrix.framework.data.resolver;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author LiangChen.Wang 2024/10/15 18:01
 */
public final class EntityMeta {
    private final Class<?> entityClass;
    private final String tableName;
    private final List<FieldMeta> fieldMetas;
    private final Map<String, FieldMeta> fieldMetaMap;

    public EntityMeta(Class<?> entityClass, String tableName, List<FieldMeta> fieldsMetas) {
        this.entityClass = entityClass;
        this.tableName = tableName;
        this.fieldMetas = fieldsMetas;
        this.fieldMetaMap = fieldsMetas.stream().collect(Collectors.toMap(fieldMeta -> fieldMeta.getFieldName(), Function.identity()));
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getTableName() {
        return tableName;
    }

    public List<FieldMeta> getFieldMetas() {
        return fieldMetas;
    }

    public Map<String, FieldMeta> getFieldMetaMap() {
        return fieldMetaMap;
    }
}
