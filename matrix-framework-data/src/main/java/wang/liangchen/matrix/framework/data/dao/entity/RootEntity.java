package wang.liangchen.matrix.framework.data.dao.entity;


import wang.liangchen.matrix.framework.commons.object.EnhancedObject;
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
public abstract class RootEntity extends EnhancedObject {
    @Transient
    private transient final Map<String, Object> forceUpdateColumns = new HashMap<>();
    @Transient
    private transient final TableMeta tableMeta;

    protected RootEntity() {
        this.tableMeta = TableMetas.INSTANCE.tableMeta(this.getClass());
    }

    public void addForceUpdateColumn(String columnName, Object value) {
        forceUpdateColumns.put(columnName, value);
    }

    public <E extends RootEntity> void addForceUpdateField(EntityGetter<E> entityGetter, Object value) {
        TableMeta tableMeta = TableMetas.INSTANCE.tableMeta(this.getClass());
        Map<String, ColumnMeta> columnMetas = tableMeta.getColumnMetas();
        String fieldName = entityGetter.getFieldName();
        String columnName = columnMetas.get(fieldName).getColumnName();
        forceUpdateColumns.put(columnName, value);
    }

    public Map<String, Object> getForceUpdateColumns() {
        return forceUpdateColumns;
    }

    public TableMeta tableMeta() {
        return this.tableMeta;
    }
}
