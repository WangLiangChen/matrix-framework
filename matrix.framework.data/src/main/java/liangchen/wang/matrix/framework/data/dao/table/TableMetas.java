package liangchen.wang.matrix.framework.data.dao.table;

import liangchen.wang.matrix.framework.commons.enumeration.Symbol;
import liangchen.wang.matrix.framework.commons.exception.MatrixInfoException;
import liangchen.wang.matrix.framework.commons.object.ClassUtil;
import liangchen.wang.matrix.framework.data.annotation.Query;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Liangchen.Wang 2021-10-22 16:04
 */
public enum TableMetas {
    INSTANCE;
    private final Map<Class<?>, TableMeta> tableMetaCache = new ConcurrentHashMap<>(128);

    public TableMeta tableMeta(final Class<?> clazz) {
        return tableMetaCache.computeIfAbsent(clazz, key -> {
            String tableName = resolveTableName(clazz);
            Set<Field> fields = ClassUtil.INSTANCE.fields(clazz, true, field -> !Modifier.isTransient(field.getModifiers()));
            Set<ColumnMeta> columnMetas = new HashSet<>(fields.size());
            for (Field field : fields) {
                columnMetas.add(resolveColumnMeta(field));
            }
            return TableMeta.newInstance(tableName, columnMetas);
        });
    }

    private ColumnMeta resolveColumnMeta(Field field) {
        Id idAnnotation = field.getAnnotation(Id.class);
        boolean isId = null != idAnnotation;
        Query queryAnnotation = field.getAnnotation(Query.class);
        Column columnAnnotation = field.getAnnotation(Column.class);
        String fieldName = field.getName();
        if (null != columnAnnotation) {
            return ColumnMeta.newInstance(columnAnnotation.name(), isId, queryAnnotation, fieldName, field.getType());
        }
        return ColumnMeta.newInstance(field2Column(fieldName), isId, queryAnnotation, fieldName, field.getType());
    }

    private String field2Column(final String fieldName) {
        char[] chars = fieldName.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (char c : chars) {
            if (c >= 'a' && c <= 'z') {
                builder.append(c);
                continue;
            }
            builder.append(Symbol.UNDERLINE.getSymbol()).append((char) (c - 32));
        }
        return builder.toString();
    }

    private String resolveTableName(Class<?> clazz) {
        Entity entity = clazz.getAnnotation(Entity.class);
        if (null != entity) {
            return entity.name();
        }
        Table table = clazz.getAnnotation(Table.class);
        if (null != table) {
            return table.name();
        }
        throw new MatrixInfoException("Entity class has no entity or table annotation:{}", clazz.getName());
    }

}
