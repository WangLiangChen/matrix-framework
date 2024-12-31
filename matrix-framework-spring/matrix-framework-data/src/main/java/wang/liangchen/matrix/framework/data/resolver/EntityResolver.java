package wang.liangchen.matrix.framework.data.resolver;

import jakarta.persistence.*;
import wang.liangchen.matrix.framework.commons.StringUtil;
import wang.liangchen.matrix.framework.commons.object.EnhancedObject;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.data.annotation.*;
import wang.liangchen.matrix.framework.data.entity.ExtendedFields;
import wang.liangchen.matrix.framework.data.entity.RootEntity;
import wang.liangchen.matrix.framework.data.json.JsonField;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LiangChen.Wang 2024/10/14 16:57
 */
public enum EntityResolver {
    INSTANCE;
    private final Map<Class<? extends RootEntity>, EntityMeta> entityMetaCache = new ConcurrentHashMap<>(128);
    private final Set<Class<?>> EXCLUDED_CLASSES = new HashSet<>() {{
        add(RootEntity.class);
        add(EnhancedObject.class);
    }};

    public <T extends RootEntity> EntityMeta resolveEntity(Class<T> entityClass) {
        return entityMetaCache.computeIfAbsent(entityClass, key -> {
            String tableName = resolveTableName(entityClass);
            // find and exclude fields
            List<Field> fields = ClassUtil.INSTANCE.declaredFields(entityClass,
                    clazz -> !EXCLUDED_CLASSES.contains(clazz),
                    field -> !Modifier.isTransient(field.getModifiers()) && !field.isAnnotationPresent(Transient.class) && !field.isAnnotationPresent(ColumnIgnore.class) && !Modifier.isStatic(field.getModifiers()));

            List<FieldMeta> fieldMetas = fields.stream().map(this::resolveField).toList();
            return new EntityMeta(entityClass, tableName, fieldMetas);
        });
    }

    private String resolveTableName(Class<? extends RootEntity> entityClass) {
        Entity entityAnnotation = entityClass.getAnnotation(Entity.class);
        if (null != entityAnnotation) {
            return entityAnnotation.name();
        }
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        if (null != tableAnnotation) {
            return tableAnnotation.name();
        }
        return StringUtil.INSTANCE.camelCase2underline(entityClass.getSimpleName());
    }

    private FieldMeta resolveField(Field field) {
        String fieldName = field.getName();
        Column columnAnnotation = field.getAnnotation(Column.class);
        String columnName = null == columnAnnotation ? StringUtil.INSTANCE.camelCase2underline(fieldName) : columnAnnotation.name();
        // is id when the field is annotated with @Id or @IdStrategy
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
        Object softDeleteValue = null == columnSoftDeleteAnnotation ? null : columnSoftDeleteAnnotation.value();
        softDeleteValue = null == softDeleteValue ? null : ObjectUtil.INSTANCE.castTo(softDeleteValue, columnSoftDeleteAnnotation.type());

        Map<FieldLabel, Optional<Object>> fieldLabels = new HashMap<>();
        if (isColumnId) {
            fieldLabels.put(FieldLabel.ID, Optional.of(idStrategy.name()));
        }
        if (isColumnUnique) {
            fieldLabels.put(FieldLabel.UNIQUE, Optional.empty());
        }
        if (isColumnVersion) {
            fieldLabels.put(FieldLabel.VERSION, Optional.empty());
        }
        if (isColumnJson) {
            fieldLabels.put(FieldLabel.JSON, Optional.empty());
        }
        if (isColumnExtended) {
            fieldLabels.put(FieldLabel.EXTENDED, Optional.empty());
        }
        if (isColumnState) {
            fieldLabels.put(FieldLabel.STATE, Optional.empty());
        }
        if (null != softDeleteValue) {
            fieldLabels.put(FieldLabel.SOFT_DELETE, Optional.of(softDeleteValue));
        }
        return new FieldMeta(fieldName, fieldClass, field.getGenericType(), columnName, fieldLabels);
    }
}
