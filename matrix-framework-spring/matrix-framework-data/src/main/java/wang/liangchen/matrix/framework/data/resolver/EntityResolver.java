package wang.liangchen.matrix.framework.data.resolver;

import jakarta.persistence.*;
import wang.liangchen.matrix.framework.commons.StringUtil;
import wang.liangchen.matrix.framework.commons.object.EnhancedObject;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.annotation.ColumnIgnore;
import wang.liangchen.matrix.framework.data.annotation.ColumnJson;
import wang.liangchen.matrix.framework.data.annotation.ColumnMarkDelete;
import wang.liangchen.matrix.framework.data.annotation.ColumnState;
import wang.liangchen.matrix.framework.data.entity.RootEntity;
import wang.liangchen.matrix.framework.data.json.JsonField;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LiangChen.Wang 2024/10/14 16:57
 */
public enum EntityResolver {
    INSTANCE;
    private final Map<Class<? extends RootEntity>, EntityMeta> tableMetaCache = new ConcurrentHashMap<>(128);
    private final Set<Class<?>> EXCLUDED_CLASSES = new HashSet<>() {{
        add(RootEntity.class);
        add(EnhancedObject.class);
    }};

    public EntityMeta resolveEntity(Class<? extends RootEntity> entityClass) {
        return tableMetaCache.computeIfAbsent(entityClass, key -> {
            Entity entityAnnotation = entityClass.getAnnotation(Entity.class);
            String tableName = null;
            if (null != entityAnnotation) {
                tableName = entityAnnotation.name();
            }
            if (null == tableName) {
                Table tableAnnotation = entityClass.getAnnotation(Table.class);
                if (null != tableAnnotation) {
                    tableName = tableAnnotation.name();
                }
            }
            ValidationUtil.INSTANCE.notNull(tableName, "Entity class '{}' must has @Entity or @Table annotation", entityClass.getName());

            // find and exclude fields
            List<Field> fields = ClassUtil.INSTANCE.declaredFields(entityClass,
                    clazz -> !EXCLUDED_CLASSES.contains(clazz),
                    field -> !Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())
                            && (null == field.getAnnotation(Transient.class) || null == field.getAnnotation(ColumnIgnore.class)));

            List<FieldMeta> fieldMetas = fields.stream().map(this::resolveField).toList();
            return new EntityMeta(entityClass, tableName, fieldMetas);
        });
    }

    private FieldMeta resolveField(Field field) {
        String fieldName = field.getName();
        Type fieldType = field.getGenericType();
        Column columnAnnotation = field.getAnnotation(Column.class);
        String columnName = null == columnAnnotation ? StringUtil.INSTANCE.camelCase2underline(fieldName) : columnAnnotation.name();
        Id columnIdAnnotation = field.getAnnotation(Id.class);
        boolean isColumnId = null != columnIdAnnotation;
        UniqueConstraint columnUniqueAnnotation = field.getAnnotation(UniqueConstraint.class);
        boolean isColumnUnique = null != columnUniqueAnnotation;
        Version columnVersionAnnotation = field.getAnnotation(Version.class);
        boolean isColumnVersion = null != columnVersionAnnotation;
        ColumnJson columnJsonAnnotation = field.getAnnotation(ColumnJson.class);
        Class<?> fieldClass = field.getType();
        boolean isColumnJson = null != columnJsonAnnotation || JsonField.class.isAssignableFrom(fieldClass);
        ColumnMarkDelete columnMarkDeleteAnnotation = field.getAnnotation(ColumnMarkDelete.class);
        String markDeleteValue = null == columnMarkDeleteAnnotation ? null : columnMarkDeleteAnnotation.value();
        ColumnState columnStateAnnotation = field.getAnnotation(ColumnState.class);
        boolean isColumnState = null != columnStateAnnotation;
        return new FieldMeta(fieldName, fieldClass, fieldType, columnName, isColumnId, isColumnUnique, isColumnVersion, isColumnJson, isColumnState, markDeleteValue);
    }
}
