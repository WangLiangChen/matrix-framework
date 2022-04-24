package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.data.annotation.ColumnDelete;
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

    @SuppressWarnings("unchecked")
    public TableMeta tableMeta(final Class<? extends RootEntity> entityClass) {
        return tableMetaCache.computeIfAbsent(entityClass, key -> resolveTableMeta(key));
    }

    private List<ColumnMeta> resolveColumnMetas(Field field, boolean isRooIdField) {
        List<ColumnMeta> fieldColumnMetas = new ArrayList<>();
        Id idAnnotation = field.getAnnotation(Id.class);
        boolean isId = null != idAnnotation;
        // 如果是RooId
        if (isId && RootId.class.isAssignableFrom(field.getType())) {
            List<ColumnMeta> idColumnMetas = new ArrayList<>();
            Set<Field> idFields = ClassUtil.INSTANCE.declaredFields(field.getType());
            // 递归调用解析
            for (Field idField : idFields) {
                idColumnMetas.addAll(resolveColumnMetas(idField, true));
            }
        }
        if (isRooIdField) {
            isId = true;
        }

        UniqueConstraint uniqueAnnotation = field.getAnnotation(UniqueConstraint.class);
        boolean isUnique = null != uniqueAnnotation;
        Version versionAnnotation = field.getAnnotation(Version.class);
        boolean isVersion = null != versionAnnotation;
        ColumnDelete columnDeleteAnnotation = field.getAnnotation(ColumnDelete.class);
        String deleteValue = null == columnDeleteAnnotation ? null : columnDeleteAnnotation.value();
        Column columnAnnotation = field.getAnnotation(Column.class);
        String fieldName = field.getName();
        String columnName = null == columnAnnotation ? StringUtil.INSTANCE.camelCase2underline(fieldName) : columnAnnotation.name();

        fieldColumnMetas.add(ColumnMeta.newInstance(fieldName, field.getType(), columnName, isId, isUnique, isVersion, deleteValue));
        return fieldColumnMetas;
    }


    private TableMeta resolveTableMeta(Class<? extends RootEntity> entityClass) {
        String tableName = null;
        Entity entity = entityClass.getAnnotation(Entity.class);
        if (null != entity) {
            tableName = entity.name();
        }
        Table table = entityClass.getAnnotation(Table.class);
        if (null != table) {
            tableName = table.name();
        }
        if (StringUtil.INSTANCE.isBlank(tableName)) {
            throw new MatrixInfoException("Entity class has no entity or table annotation:{}", entityClass.getName());
        }
        // 排除transient修饰的列
        Set<Field> fields = ClassUtil.INSTANCE.declaredFields(entityClass, field -> !Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers()) && null == field.getAnnotation(Transient.class), true);
        Map<String, ColumnMeta> columnMetas = new HashMap<>(fields.size());
        for (Field field : fields) {
            List<ColumnMeta> fieldColumnMetas = resolveColumnMetas(field, false);
            for (ColumnMeta fieldColumnMeta : fieldColumnMetas) {
                columnMetas.put(fieldColumnMeta.getFieldName(), fieldColumnMeta);
            }
        }
        return TableMeta.newInstance(entityClass, tableName, columnMetas);
    }

}
