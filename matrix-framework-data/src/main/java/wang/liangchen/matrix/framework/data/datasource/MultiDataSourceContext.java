package wang.liangchen.matrix.framework.data.datasource;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.data.datasource.dialect.AbstractDialect;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author LiangChen.Wang
 * 存储当前线程数据源，一个线程可以存储多个数据源
 * 也就是可以嵌套
 */
public enum MultiDataSourceContext {
    /**
     * instance
     */
    INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(MultiDataSourceContext.class);
    // ArrayDeque当作栈(后进先出)
    private final ThreadLocal<Deque<String>> context = ThreadLocal.withInitial(ArrayDeque::new);
    public final String PRIMARY_DATASOURCE_NAME = "primary";
    public final Map<String, CachedDataSource> cache = new ConcurrentHashMap<>();

    public void putDataSource(String dataSourceName, DataSource dataSource, AbstractDialect dialect) {
        cache.put(dataSourceName, new CachedDataSource(dataSourceName, dataSource, dialect));
    }

    public Set<String> getDataSourceNames() {
        return cache.keySet();
    }

    public Map<String, DataSource> getDataSources() {
        return cache.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getDataSource()));
    }

    public DataSource getDataSource(String dataSourceName) {
        return cache.get(dataSourceName).getDataSource();
    }

    public DataSource getDataSource() {
        return getDataSource(get());
    }

    public DataSource getPrimaryDataSource() {
        return getDataSource(PRIMARY_DATASOURCE_NAME);
    }

    public Map<String, DataSource> getSecondaryDataSources() {
        return cache.entrySet().stream().filter(e -> !PRIMARY_DATASOURCE_NAME.equals(e.getKey())).collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getDataSource()));
    }

    public AbstractDialect getDialect(String dataSourceName) {
        return cache.get(dataSourceName).getDialect();
    }

    public AbstractDialect getDialect() {
        return getDialect(get());
    }

    //-----------------------------ThreadLocal-------------------------------
    public void set(String dataSourceName) {
        // 入队
        Deque<String> deque = context.get();
        deque.push(dataSourceName);
        logger.debug("switched datasource to:{} , Nested data:{}", dataSourceName, deque);
    }

    public String get() {
        // 从队列中获取 但不出队
        Deque<String> deque = context.get();
        return deque.peek();
    }

    public void clear() {
        // 出栈 后进先出
        Deque<String> deque = context.get();
        String polledDataSourceName = deque.poll();
        logger.debug("cleared datasource:{} , Nested data:{}", polledDataSourceName, deque);
        if (null == polledDataSourceName) {
            // 栈空 清理当前线程
            remove();
        }
    }

    public void remove() {
        context.remove();
    }

    static class CachedDataSource {
        private final String dataSourceName;
        private final DataSource dataSource;
        private final AbstractDialect dialect;

        CachedDataSource(String dataSourceName, DataSource dataSource, AbstractDialect dialect) {
            this.dataSourceName = dataSourceName;
            this.dataSource = dataSource;
            this.dialect = dialect;
        }

        public String getDataSourceName() {
            return dataSourceName;
        }

        public DataSource getDataSource() {
            return dataSource;
        }

        public AbstractDialect getDialect() {
            return dialect;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CachedDataSource that = (CachedDataSource) o;
            return Objects.equals(dataSourceName, that.dataSourceName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dataSourceName);
        }
    }
}
