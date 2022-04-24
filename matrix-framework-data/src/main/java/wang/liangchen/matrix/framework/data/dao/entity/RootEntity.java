package wang.liangchen.matrix.framework.data.dao.entity;


import wang.liangchen.matrix.framework.commons.object.EnhancedMap;
import wang.liangchen.matrix.framework.data.dao.criteria.ColumnMeta;
import wang.liangchen.matrix.framework.data.dao.criteria.EntityGetter;
import wang.liangchen.matrix.framework.data.dao.criteria.TableMeta;
import wang.liangchen.matrix.framework.data.dao.criteria.TableMetas;

import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LiangChen.Wang
 * 实体基础抽象类
 */
public abstract class RootEntity extends EnhancedMap {
    @Transient
    private transient Map<String, Object> forceUpdateColumns;
    @Transient
    private final transient TableMeta tableMeta;

    protected RootEntity() {
        tableMeta = TableMetas.INSTANCE.tableMeta(this.getClass());
    }
    public <E extends RootEntity> void addForceUpdateColumn(String columnName, Object value) {
        if (null == forceUpdateColumns) {
            forceUpdateColumns = new HashMap<>();
        }
        forceUpdateColumns.put(columnName,value);
    }
    public <E extends RootEntity> void addForceUpdateField(EntityGetter<E> entityGetter, Object value) {
        if (null == forceUpdateColumns) {
            forceUpdateColumns = new HashMap<>();
        }
        Map<String, ColumnMeta> columnMetas = tableMeta.getColumnMetas();
        String fieldName = entityGetter.getFieldName();
        String columnName = columnMetas.get(fieldName).getColumnName();
        forceUpdateColumns.put(columnName, value);
    }

    public Map<String, Object> getForceUpdateColumns() {
        return forceUpdateColumns;
    }

    public TableMeta getTableMeta() {
        return tableMeta;
    }
}
