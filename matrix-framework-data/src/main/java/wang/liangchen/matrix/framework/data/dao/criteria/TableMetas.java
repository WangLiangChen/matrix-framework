package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.data.annotation.ColumnDelete;
import wang.liangchen.matrix.framework.data.annotation.ColumnState;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

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
    public  TableMeta tableMeta(final Class<? extends RootEntity> entityClass) {
        return  tableMetaCache.computeIfAbsent(entityClass, key -> resolveTableMeta(key));
    }

    private ColumnMeta resolveColumnMeta(Field field) {
        Id idAnnotation = field.getAnnotation(Id.class);
        boolean isId = null != idAnnotation;
        UniqueConstraint uniqueAnnotation = field.getAnnotation(UniqueConstraint.class);
        boolean isUnique = null != uniqueAnnotation;
        Version versionAnnotation = field.getAnnotation(Version.class);
        boolean isVersion = null != versionAnnotation;
        ColumnDelete columnDeleteAnnotation = field.getAnnotation(ColumnDelete.class);
        String deleteValue = null == columnDeleteAnnotation ? null : columnDeleteAnnotation.value();
        ColumnState columnStateAnnotation = field.getAnnotation(ColumnState.class);
        boolean isState = null != columnStateAnnotation;
        Column columnAnnotation = field.getAnnotation(Column.class);
        String fieldName = field.getName();
        if (null != columnAnnotation) {
            return ColumnMeta.newInstance(fieldName, field.getType(), columnAnnotation.name(), isId, isUnique, isVersion, isState, deleteValue);
        }
        return ColumnMeta.newInstance(fieldName, field.getType(), StringUtil.INSTANCE.camelCase2underline(fieldName), isId, isUnique, isVersion, isState, deleteValue);
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
            ColumnMeta columnMeta = resolveColumnMeta(field);
            columnMetas.put(columnMeta.getFieldName(), columnMeta);
        }
        return TableMeta.newInstance(entityClass, tableName, columnMetas);
    }

}
