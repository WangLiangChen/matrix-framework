package wang.liangchen.matrix.framework.data.dao.criteria;

import jakarta.persistence.*;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.object.EnhancedObject;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.data.annotation.ColumnJson;
import wang.liangchen.matrix.framework.data.annotation.ColumnMarkDelete;
import wang.liangchen.matrix.framework.data.annotation.ColumnState;
import wang.liangchen.matrix.framework.data.annotation.IdStrategy;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

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

    private ColumnMeta resolveColumnMeta(Field field) {
        return ColumnMeta.newInstance(field.getName(), field.getType(), field.getGenericType(), resolveColumnName(field),
                resolveColumnId(field), resolveColumnIdStrategy(field), resolveColumnUnique(field), resolveColumnVersion(field), resolveColumnJson(field), resolveColumnState(field), resolveColumnDelete(field));
    }

    private boolean resolveColumnId(Field field) {
        Id idAnnotation = field.getAnnotation(Id.class);
        return null != idAnnotation;
    }

    private IdStrategy.Strategy resolveColumnIdStrategy(Field field) {
        IdStrategy idStrategyAnnotation = field.getAnnotation(IdStrategy.class);
        if (null == idStrategyAnnotation) {
            return null;
        }
        return idStrategyAnnotation.value();
    }

    private String resolveColumnName(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        return null == columnAnnotation ? StringUtil.INSTANCE.camelCase2underline(field.getName()) : columnAnnotation.name();
    }

    public String resolveColumnDelete(Field field) {
        ColumnMarkDelete columnMarkDeleteAnnotation = field.getAnnotation(ColumnMarkDelete.class);
        return null == columnMarkDeleteAnnotation ? null : columnMarkDeleteAnnotation.value();
    }

    public boolean resolveColumnJson(Field field) {
        ColumnJson columnJsonAnnotation = field.getAnnotation(ColumnJson.class);
        return null != columnJsonAnnotation;
    }

    public boolean resolveColumnState(Field field) {
        ColumnState columnStateAnnotation = field.getAnnotation(ColumnState.class);
        return null != columnStateAnnotation;
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
        throw new MatrixWarnException("Entity class has no entity or table annotation:{}", entityClass.getName());
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
            columnMetas.put(field.getName(), resolveColumnMeta(field));
        }
        return TableMeta.newInstance(entityClass, resolveTableName(entityClass), columnMetas);
    }

}
