package wang.liangchen.matrix.framework.data.datasource;


import wang.liangchen.matrix.framework.data.datasource.dialect.AbstractDialect;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LiangChen.Wang
 * 存储当前线程数据源，一个线程可以存储多个数据源
 */
public enum MultiDataSourceContext {
    /**
     * instance
     */
    INSTANCE;
    private final ThreadLocal<Deque<String>> context = ThreadLocal.withInitial(ArrayDeque::new);
    private final Map<String, AbstractDialect> cachedDialects = new HashMap<>();
    private final Map<String, DataSource> cachedDataSources = new HashMap<>();
    private final Set<String> cachedDataSourceNames = new HashSet<>();
    public final String PRIMARY_DATASOURCE_NAME = "primary";


    public void putDialect(String dataSourceName, AbstractDialect dialect) {
        cachedDialects.put(dataSourceName, dialect);
    }

    public void putDataSource(String dataSourceName, DataSource dataSource) {
        cachedDataSourceNames.add(dataSourceName);
        cachedDataSources.put(dataSourceName, dataSource);
    }

    public Map<String, AbstractDialect> getDialects() {
        return cachedDialects;
    }

    public AbstractDialect getDialect(String dataSourceName) {
        return cachedDialects.get(dataSourceName);
    }

    public AbstractDialect getDialect() {
        return getDialect(get());
    }

    public Set<String> getDataSourceNames() {
        return cachedDataSourceNames;
    }

    public Map<String, DataSource> getDataSources() {
        return cachedDataSources;
    }

    public DataSource getDataSource(String dataSourceName) {
        return cachedDataSources.get(dataSourceName);
    }

    public DataSource getDataSource() {
        return getDataSource(get());
    }

    public DataSource getPrimaryDataSource() {
        return cachedDataSources.get(PRIMARY_DATASOURCE_NAME);
    }

    public Map<String, DataSource> getSecondaryDataSources() {
        return cachedDataSources.entrySet().stream().filter(e -> !Objects.equals(PRIMARY_DATASOURCE_NAME, e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void set(String dataSourceName) {
        // 放入队列
        Deque<String> deque = context.get();
        deque.push(dataSourceName);
    }

    public String get() {
        // 从队列中获取
        Deque<String> deque = context.get();
        return deque.peek();
    }

    public void clear() {
        Deque<String> deque = context.get();
        if (deque.isEmpty()) {
            remove();
            return;
        }
        deque.pop();
    }

    public void remove() {
        context.remove();
    }
}
