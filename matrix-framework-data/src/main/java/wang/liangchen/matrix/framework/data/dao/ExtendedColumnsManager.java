package wang.liangchen.matrix.framework.data.dao;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.enumeration.DataType;

/**
 * @author Liangchen.Wang 2023-03-23 21:58
 */
public interface ExtendedColumnsManager {
    <T extends RootEntity> void add(Class<T> entityClass, String columnName, DataType dataType, String columnComment);
}
