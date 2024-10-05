package wang.liangchen.matrix.framework.data.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.StringUtil;
import wang.liangchen.matrix.framework.data.datasource.dialect.AbstractDialect;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * @Author: Liangchen.Wang
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
        logger.debug("The datasource: {} enqueue.and data in the queue is: {}", dataSourceName, deque);
    }

    public String get() {
        Deque<String> deque = context.get();
        // 从队列中获取 但不出队
        String peekedDataSourceName = deque.peek();
        logger.debug("Peeked DataSource: {}.and data in the queue is: {}", peekedDataSourceName, deque);
        if (StringUtil.INSTANCE.isBlank(peekedDataSourceName)) {
            return PRIMARY_DATASOURCE_NAME;
        }
        return peekedDataSourceName;
    }

    public void clear() {
        Deque<String> deque = context.get();
        // 出队 后进先出
        String polledDataSourceName = deque.poll();
        logger.debug("Polled DataSource: {}.and data in the queue is: {}", polledDataSourceName, deque);
        if (null == polledDataSourceName) {
            logger.debug("The queue is empty, remove the context.");
            remove();
        }
    }

    public void remove() {
        context.remove();
    }


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
