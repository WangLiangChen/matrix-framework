package wang.liangchen.matrix.framework.data.dao.impl;

import jakarta.inject.Inject;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.dao.ExtendedColumnsManager;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.data.dao.criteria.Criteria;
import wang.liangchen.matrix.framework.data.dao.criteria.DeleteCriteria;
import wang.liangchen.matrix.framework.data.dao.entity.ExtendedColumns;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2023-03-23 21:59
 */
public class ExtendedColumnsManagerImpl implements ExtendedColumnsManager {
    private final StandaloneDao standaloneDao;

    @Inject
    public ExtendedColumnsManagerImpl(StandaloneDao standaloneDao) {
        this.standaloneDao = standaloneDao;
    }

    public void add(ExtendedColumns entity) {
        ValidationUtil.INSTANCE.notNull(entity);
        String tableName = entity.getTableName();
        ValidationUtil.INSTANCE.notEmpty(tableName);
        String columnName = entity.getColumnName();
        ValidationUtil.INSTANCE.notEmpty(columnName);
        // 判断重复
        ValidationUtil.INSTANCE.isFalse(exists(tableName, columnName), "tableName: {}, columnName: {} existed", tableName, columnName);
        this.standaloneDao.insert(entity);
    }

    public void remove(String tableName, String columnName) {
        ValidationUtil.INSTANCE.notEmpty(tableName);
        ValidationUtil.INSTANCE.notEmpty(columnName);
        DeleteCriteria<ExtendedColumns> criteria = DeleteCriteria.of(ExtendedColumns.class)
                ._equals(ExtendedColumns::getTableName, tableName)
                ._equals(ExtendedColumns::getColumnName, columnName);
        this.standaloneDao.delete(criteria);
    }

    public List<ExtendedColumns> list(String tableName) {
        ValidationUtil.INSTANCE.notEmpty(tableName);
        return this.standaloneDao.list(Criteria.of(ExtendedColumns.class)._equals(ExtendedColumns::getTableName, tableName));
    }

    public List<String> listColumnName(String tableName) {
        return list(tableName).stream().map(ExtendedColumns::getColumnName).collect(Collectors.toList());
    }

    public ExtendedColumns select(String tableName, String columnName) {
        ValidationUtil.INSTANCE.notEmpty(tableName);
        ValidationUtil.INSTANCE.notEmpty(columnName);
        Criteria<ExtendedColumns> criteria = Criteria.of(ExtendedColumns.class)
                ._equals(ExtendedColumns::getTableName, tableName)
                ._equals(ExtendedColumns::getColumnName, columnName);
        return this.standaloneDao.select(criteria);
    }

    public boolean exists(String tableName, String columnName) {
        ValidationUtil.INSTANCE.notEmpty(tableName);
        ValidationUtil.INSTANCE.notEmpty(columnName);
        Criteria<ExtendedColumns> criteria = Criteria.of(ExtendedColumns.class)
                ._equals(ExtendedColumns::getTableName, tableName)
                ._equals(ExtendedColumns::getColumnName, columnName);
        return this.standaloneDao.exists(criteria);
    }
}
