package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.data.annotation.ColumnMarkDelete;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.dao.entity.RootId;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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

    @SuppressWarnings("unchecked")
    public TableMeta tableMeta(final Class<? extends RootEntity> entityClass) {
        return tableMetaCache.computeIfAbsent(entityClass, key -> resolveTableMeta(key));
    }

    private ColumnMeta resolveColumnMeta(Field field, boolean isRooIdField) {
        boolean isId = isRooIdField || resolveColumnId(field);
        return ColumnMeta.newInstance(field.getName(), field.getType(), resolveColumnName(field),
                isId, resolveColumnUnique(field), resolveColumnVersion(field), resolveColumnDelete(field));
    }

    private boolean resolveColumnId(Field field) {
        Id idAnnotation = field.getAnnotation(Id.class);
        return null != idAnnotation;
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
        // 排除transient修饰的列
        Set<Field> fields = ClassUtil.INSTANCE.declaredFields(entityClass, field -> !Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers()) && null == field.getAnnotation(Transient.class), true);
        Map<String, ColumnMeta> columnMetas = new HashMap<>(fields.size());
        for (Field field : fields) {
            if (RootId.class.isAssignableFrom(field.getType())) {
                Set<Field> idFields = ClassUtil.INSTANCE.declaredFields(field.getType());
                for (Field idField : idFields) {
                    columnMetas.put(field.getName(), resolveColumnMeta(field, true));
                }
                continue;
            }
            columnMetas.put(field.getName(), resolveColumnMeta(field, false));
        }
        return TableMeta.newInstance(entityClass, resolveTableName(entityClass), columnMetas);
    }

}
