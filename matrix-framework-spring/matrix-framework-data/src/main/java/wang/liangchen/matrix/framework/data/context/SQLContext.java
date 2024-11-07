package wang.liangchen.matrix.framework.data.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;


/**
 * @Author: Liangchen.Wang
 */
public enum SQLContext {
    /**
     * instance
     */
    INSTANCE;

    private final static TransmittableThreadLocal<String> context = TransmittableThreadLocal.withInitial(Symbol.EMPTY::getSymbol);

    public void setTableName(String tableName) {
        context.set(tableName);
    }

    public void remove() {
        context.remove();
    }

}
