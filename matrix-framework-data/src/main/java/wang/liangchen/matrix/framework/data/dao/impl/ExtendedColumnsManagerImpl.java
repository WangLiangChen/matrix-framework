package wang.liangchen.matrix.framework.data.dao.impl;

import jakarta.inject.Inject;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.dao.ExtendedColumnsManager;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.data.dao.criteria.Criteria;
import wang.liangchen.matrix.framework.data.dao.criteria.DeleteCriteria;
import wang.liangchen.matrix.framework.data.dao.criteria.TableMeta;
import wang.liangchen.matrix.framework.data.dao.criteria.TableMetas;
import wang.liangchen.matrix.framework.data.dao.entity.ExtendedColumn;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.enumeration.DataType;

import java.util.List;

/**
 * @author Liangchen.Wang 2023-03-23 21:59
 */
public class ExtendedColumnsManagerImpl implements ExtendedColumnsManager {
    private final StandaloneDao standaloneDao;

    @Inject
    public ExtendedColumnsManagerImpl(StandaloneDao standaloneDao) {
        this.standaloneDao = standaloneDao;
    }

    @Override
    public <T extends RootEntity> void add(Class<T> entityClass, String columnName, DataType dataType, String columnComment) {
        TableMeta tableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
        String tableName = tableMeta.getTableName();
        ExtendedColumn entity = ExtendedColumn.newInstance();
        entity.setTableName(tableName);
        entity.setColumnName(columnName);
        entity.setDataType(dataType);
        entity.setColumnComment(columnComment);
        ValidationUtil.INSTANCE.isFalse(exists(tableName, columnName), "tableName: {}, columnName: {} existed", tableName, columnName);
        this.standaloneDao.insert(entity);
    }

    public <T extends RootEntity> void remove(Class<T> entityClass, String columnName) {
        TableMeta tableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
        String tableName = tableMeta.getTableName();
        this.standaloneDao.delete(DeleteCriteria.of(ExtendedColumn.class)
                ._equals(ExtendedColumn::getTableName, tableName)
                ._equals(ExtendedColumn::getColumnName, columnName));
    }

    public <T extends RootEntity> List<ExtendedColumn> list(Class<T> entityClass) {
        TableMeta tableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
        String tableName = tableMeta.getTableName();
        return this.standaloneDao.list(Criteria.of(ExtendedColumn.class)._equals(ExtendedColumn::getTableName, tableName));
    }

    private boolean exists(String tableName, String columnName) {
        ValidationUtil.INSTANCE.notEmpty(tableName);
        ValidationUtil.INSTANCE.notEmpty(columnName);
        Criteria<ExtendedColumn> criteria = Criteria.of(ExtendedColumn.class)
                ._equals(ExtendedColumn::getTableName, tableName)
                ._equals(ExtendedColumn::getColumnName, columnName);
        return this.standaloneDao.exists(criteria);
    }
}
