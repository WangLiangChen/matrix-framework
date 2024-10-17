package wang.liangchen.matrix.framework.data.resolver;

import java.util.LinkedHashMap;
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
    private final Map<String, FieldMeta> fieldMetas = new LinkedHashMap<>();
    private final Map<String, FieldMeta> pkFieldMetas = new LinkedHashMap<>();
    private final Map<String, FieldMeta> nonPkFieldMetas = new LinkedHashMap<>();


    public EntityMeta(Class<?> entityClass, String tableName, List<FieldMeta> fieldsMetas) {
        this.entityClass = entityClass;
        this.tableName = tableName;
        fieldsMetas.forEach(fieldMeta -> {
            String fieldName = fieldMeta.getFieldName();
            this.fieldMetas.put(fieldName, fieldMeta);
            if (fieldMeta.isColumnId()) {
                pkFieldMetas.put(fieldName, fieldMeta);
            } else {
                nonPkFieldMetas.put(fieldName, fieldMeta);
            }
        });
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
}
