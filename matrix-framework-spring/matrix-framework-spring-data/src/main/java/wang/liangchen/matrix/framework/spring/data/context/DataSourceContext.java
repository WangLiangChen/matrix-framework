package wang.liangchen.matrix.framework.spring.data.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.spring.data.datasource.dialect.AbstractDialect;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * author: Liangchen.Wang
 */
public enum DataSourceContext {
    /**
     * instance
     */
    INSTANCE;
    private final static Logger logger = LoggerFactory.getLogger(DataSourceContext.class);
    public final static String PRIMARY_DATASOURCE_NAME = "primary";
    // ArrayDeque当作栈(后进先出)
    private final static TransmittableThreadLocal<Deque<String>> context = TransmittableThreadLocal.withInitial(ArrayDeque::new);
    private final static Map<String, CachedDataSource> cache = new ConcurrentHashMap<>();

    //-----------------------------ThreadLocal-------------------------------
    public void set(String dataSourceName) {
        Deque<String> deque = context.get();
        // 入队
        deque.push(dataSourceName);
        logger.debug("The datasource: {} enqueue. And datasource in the queue is: {}", dataSourceName, deque);
    }

    public void setPrimary() {
        set(PRIMARY_DATASOURCE_NAME);
    }

    public String get() {
        Deque<String> deque = context.get();
        // 从队列中获取 但不出队
        String peekedDataSourceName = deque.peek();
        if (StringUtil.INSTANCE.isNullOrBlank(peekedDataSourceName)) {
            logger.debug("The queue is empty, return 'primary' datasource.");
            return PRIMARY_DATASOURCE_NAME;
        }
        logger.debug("The datasource is peeked: {}. And datasource in the queue is: {}", peekedDataSourceName, deque);
        return peekedDataSourceName;
    }

    public void evict(String dataSourceName) {
        Deque<String> deque = context.get();
        String peekedDataSourceName = deque.peek();
        if (!dataSourceName.equals(peekedDataSourceName)) {
            logger.error("The datasource: {} is not the datasource: {} located at the first. Cannot evict.", dataSourceName, peekedDataSourceName);
            return;
        }
        // 出队 后进先出
        String polledDataSourceName = deque.poll();
        logger.debug("The datasource: {} dequeue. And datasource in the queue is: {}", polledDataSourceName, deque);
        if (null == polledDataSourceName) {
            logger.debug("The queue is empty, remove from the context.");
            remove();
        }
    }

    public void evictPrimary() {
        evict(PRIMARY_DATASOURCE_NAME);
    }

    public void remove() {
        context.remove();
    }


    public void putDataSource(String dataSourceName, DataSource dataSource, AbstractDialect dialect) {
        cache.put(dataSourceName, new CachedDataSource(dataSourceName, dataSource, dialect));
    }

    public void executeWithDataSource(String dataSourceName, Runnable runnable) {
        set(dataSourceName);
        try {
            runnable.run();
        } finally {
            evict(dataSourceName);
        }
    }

    public void executeWithPrimaryDataSource(Runnable runnable) {
        executeWithDataSource(PRIMARY_DATASOURCE_NAME, runnable);
    }

    public <T> T executeWithDataSource(String dataSourceName, Supplier<T> supplier) {
        set(dataSourceName);
        try {
            return supplier.get();
        } finally {
            evict(dataSourceName);
        }
    }

    public <T> T executeWithPrimaryDataSource(Supplier<T> supplier) {
        return executeWithDataSource(PRIMARY_DATASOURCE_NAME, supplier);
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
        CachedDataSource cachedDataSource = cache.get(dataSourceName);
        if (null == cachedDataSource) {
            return null;
        }
        return cachedDataSource.getDialect();
    }

    public AbstractDialect getDialect() {
        return getDialect(get());
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
