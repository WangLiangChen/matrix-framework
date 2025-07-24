package wang.liangchen.matrix.framework.spring.data.mybatis;

import com.alibaba.ttl.TransmittableThreadLocal;

public enum MyBatisExecutorContext {
    INSTANCE;
    private final TransmittableThreadLocal<String> tableNameContext = new TransmittableThreadLocal<>();

    public void setTableName(String tableName) {
        tableNameContext.set(tableName);
    }

    public String getTableName() {
        return tableNameContext.get();
    }

    public void removeTableName() {
        tableNameContext.remove();
    }
}
