package wang.liangchen.matrix.framework.data.resolver;

import jakarta.persistence.*;
import wang.liangchen.matrix.framework.commons.StringUtil;
import wang.liangchen.matrix.framework.commons.object.EnhancedObject;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.annotation.*;
import wang.liangchen.matrix.framework.data.entity.ExtendedFields;
import wang.liangchen.matrix.framework.data.entity.RootEntity;
import wang.liangchen.matrix.framework.data.json.JsonField;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
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
        IdStrategy columnIdStrategyAnnotation = field.getAnnotation(IdStrategy.class);
        IdStrategy.Strategy idStrategy = null == columnIdStrategyAnnotation ? IdStrategy.Strategy.NONE : columnIdStrategyAnnotation.value();
        Id columnIdAnnotation = field.getAnnotation(Id.class);
        boolean isColumnId = null != columnIdAnnotation || null != columnIdStrategyAnnotation;
        UniqueConstraint columnUniqueAnnotation = field.getAnnotation(UniqueConstraint.class);
        boolean isColumnUnique = null != columnUniqueAnnotation;
        Version columnVersionAnnotation = field.getAnnotation(Version.class);
        boolean isColumnVersion = null != columnVersionAnnotation;
        ColumnJson columnJsonAnnotation = field.getAnnotation(ColumnJson.class);
        Class<?> fieldClass = field.getType();
        boolean isColumnJson = null != columnJsonAnnotation || JsonField.class.isAssignableFrom(fieldClass);
        boolean isColumnExtended = ExtendedFields.class.isAssignableFrom(fieldClass);
        ColumnState columnStateAnnotation = field.getAnnotation(ColumnState.class);
        boolean isColumnState = null != columnStateAnnotation;
        ColumnSoftDelete columnSoftDeleteAnnotation = field.getAnnotation(ColumnSoftDelete.class);
        String softDeleteValue = null == columnSoftDeleteAnnotation ? null : columnSoftDeleteAnnotation.value();

        Map<FieldLabel, Optional<String>> fieldTypes = new HashMap<>();
        if (isColumnId) {
            fieldTypes.put(FieldLabel.ID, Optional.of(idStrategy.name()));
        }
        if (isColumnUnique) {
            fieldTypes.put(FieldLabel.UNIQUE, Optional.empty());
        }
        if (isColumnVersion) {
            fieldTypes.put(FieldLabel.VERSION, Optional.empty());
        }
        if (isColumnJson) {
            fieldTypes.put(FieldLabel.JSON, Optional.empty());
        }
        if (isColumnExtended) {
            fieldTypes.put(FieldLabel.EXTENDED, Optional.empty());
        }
        if (isColumnState) {
            fieldTypes.put(FieldLabel.STATE, Optional.empty());
        }
        if (null != softDeleteValue) {
            fieldTypes.put(FieldLabel.SOFT_DELETE, Optional.of(softDeleteValue));
        }
        return new FieldMeta(fieldName, fieldClass, fieldType, columnName, fieldTypes);
    }
}
