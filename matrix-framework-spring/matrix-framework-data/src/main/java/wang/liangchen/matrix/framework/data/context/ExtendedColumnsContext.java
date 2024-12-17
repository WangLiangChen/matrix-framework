package wang.liangchen.matrix.framework.data.context;

import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;

/**
 * @author Liangchen.Wang 2023-03-30 19:14
 */
public enum ExtendedColumnsContext {
    INSTANCE;
    private final ThreadLocal<ExtendedColumnIsolationKey> context = new InheritableThreadLocal<>();

    public void setColumnGroup(String columnGroup) {
        ExtendedColumnIsolationKey extendedColumnIsolationKey = ExtendedColumnIsolationKey.newInstance();
        extendedColumnIsolationKey.columnGroup = columnGroup;
        context.set(extendedColumnIsolationKey);
    }

    public void setTableName(String tableName) {
        ExtendedColumnIsolationKey extendedColumnIsolationKey = context.get();
        if (null != extendedColumnIsolationKey) {
            extendedColumnIsolationKey.tableName = tableName;
        }
    }

    public String getColumnGroup() {
        ExtendedColumnIsolationKey extendedColumnIsolationKey = context.get();
        if (null == extendedColumnIsolationKey) {
            throw new MatrixErrorException("columnGroup isn't set");
        }
        return extendedColumnIsolationKey.columnGroup;
    }

    public String getTableName() {
        ExtendedColumnIsolationKey extendedColumnIsolationKey = context.get();
        if (null == extendedColumnIsolationKey) {
            throw new MatrixErrorException("columnGroup isn't set");
        }
        return extendedColumnIsolationKey.tableName;
    }


    public void remove() {
        context.remove();
    }

    public static class ExtendedColumnIsolationKey {
        private String columnGroup;
        private String tableName;

        public static ExtendedColumnIsolationKey newInstance() {
            return ClassUtil.INSTANCE.instantiate(ExtendedColumnIsolationKey.class);
        }
    }
}
