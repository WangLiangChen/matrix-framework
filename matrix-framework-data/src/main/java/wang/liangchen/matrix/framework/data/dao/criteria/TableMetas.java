package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.object.EnhancedObject;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.data.annotation.ColumnMarkDelete;
import wang.liangchen.matrix.framework.data.annotation.IdStrategy;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.dao.entity.RootId;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Liangchen.Wang 2021-10-22 16:04
 */
public enum TableMetas {
    /**
     * instance
     */
    INSTANCE;
    private final Map<Class<? extends RootEntity>, TableMeta> tableMetaCache = new ConcurrentHashMap<>(128);
    private final Set<Class<?>> EXCLUDED_CLASSES = new HashSet<Class<?>>() {{
        add(RootEntity.class);
        add(EnhancedObject.class);
    }};

    public TableMeta tableMeta(final Class<? extends RootEntity> entityClass) {
        return tableMetaCache.computeIfAbsent(entityClass, this::resolveTableMeta);
    }

    private ColumnMeta resolveColumnMeta(Field field, boolean isRootIdField) {
        IdStrategy idStrategy = resolveColumnId(field);
        boolean isId = isRootIdField || idStrategy != IdStrategy.NONE;
        return ColumnMeta.newInstance(field.getName(), field.getType(), resolveColumnName(field),
                isId, idStrategy, resolveColumnUnique(field), resolveColumnVersion(field), resolveColumnDelete(field));
    }

    private IdStrategy resolveColumnId(Field field) {
        wang.liangchen.matrix.framework.data.annotation.Id matrixIdAnnotation = field.getAnnotation(wang.liangchen.matrix.framework.data.annotation.Id.class);
        if (null != matrixIdAnnotation) {
            IdStrategy idStrategy = matrixIdAnnotation.value();
            return IdStrategy.NONE == idStrategy ? IdStrategy.AUTO_INCREMENT : idStrategy;
        }
        Id idAnnotation = field.getAnnotation(Id.class);
        if (null != idAnnotation) {
            //Default Strategy
            return IdStrategy.AUTO_INCREMENT;
        }
        return IdStrategy.NONE;
    }

    private String resolveColumnName(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        return null == columnAnnotation ? StringUtil.INSTANCE.camelCase2underline(field.getName()) : columnAnnotation.name();
    }

    public String resolveColumnDelete(Field field) {
        ColumnMarkDelete columnMarkDeleteAnnotation = field.getAnnotation(ColumnMarkDelete.class);
        return null == columnMarkDeleteAnnotation ? null : columnMarkDeleteAnnotation.value();
    }

    private boolean resolveColumnVersion(Field field) {
        Version versionAnnotation = field.getAnnotation(Version.class);
        return null != versionAnnotation;
    }

    private boolean resolveColumnUnique(Field field) {
        UniqueConstraint uniqueAnnotation = field.getAnnotation(UniqueConstraint.class);
        return null != uniqueAnnotation;
    }

    private String resolveTableName(Class<? extends RootEntity> entityClass) {
        Entity entity = entityClass.getAnnotation(Entity.class);
        if (null != entity) {
            return entity.name();
        }
        Table table = entityClass.getAnnotation(Table.class);
        if (null != table) {
            return table.name();
        }
        throw new MatrixInfoException("Entity class has no entity or table annotation:{}", entityClass.getName());
    }


    private TableMeta resolveTableMeta(Class<? extends RootEntity> entityClass) {
        // 排除列
        List<Field> fields = ClassUtil.INSTANCE.declaredFields(entityClass,
                clazz -> !EXCLUDED_CLASSES.contains(clazz),
                field -> !Modifier.isTransient(field.getModifiers())
                        && null == field.getAnnotation(Transient.class)
                        && !Modifier.isStatic(field.getModifiers()));
        Map<String, ColumnMeta> columnMetas = new HashMap<>(fields.size());
        for (Field field : fields) {
            if (RootId.class.isAssignableFrom(field.getType())) {
                List<Field> idFields = ClassUtil.INSTANCE.declaredFields(field.getType());
                for (Field idField : idFields) {
                    columnMetas.put(idField.getName(), resolveColumnMeta(idField, true));
                }
                continue;
            }
            columnMetas.put(field.getName(), resolveColumnMeta(field, false));
        }
        return TableMeta.newInstance(entityClass, resolveTableName(entityClass), columnMetas);
    }

}
