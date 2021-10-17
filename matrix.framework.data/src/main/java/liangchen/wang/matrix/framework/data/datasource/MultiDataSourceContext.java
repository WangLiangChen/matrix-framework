package liangchen.wang.matrix.framework.data.datasource;


import liangchen.wang.matrix.framework.data.datasource.dialect.AbstractDialect;

import javax.sql.DataSource;
import java.util.*;

/**
 * @author LiangChen.Wang
 * 缓存初始化的数据源
 * 存储当前线程数据源
 */
public enum MultiDataSourceContext {
    /**
     *
     */
    INSTANCE;
    private final ThreadLocal<Deque<String>> context = ThreadLocal.withInitial(ArrayDeque::new);
    private final Map<String, AbstractDialect> cachedDialects = new LinkedHashMap<>();
    private final Map<String, DataSource> cachedDataSources = new LinkedHashMap<>();
    private final Set<String> cachedDataSourceNames = new LinkedHashSet<>();
    public final String PRIMARY_DATASOURCE_NAME = "primary";


    public void putDialect(String dataSourceName, AbstractDialect dialect) {
        cachedDialects.put(dataSourceName, dialect);
    }

    public void putDataSource(String dataSourceName, DataSource dataSource) {
        cachedDataSourceNames.add(dataSourceName);
        cachedDataSources.put(dataSourceName, dataSource);
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

    public DataSource getDataSource(String dataSourceName) {
        return cachedDataSources.get(dataSourceName);
    }

    public DataSource getPrimaryDataSource() {
        return cachedDataSources.get(PRIMARY_DATASOURCE_NAME);
    }

    public Map<String, DataSource> getSecondaryDataSources() {
        Map<String, DataSource> secondaryDataSources = new LinkedHashMap<>();
        cachedDataSources.forEach((dataSourceName, dataSource) -> {
            if (PRIMARY_DATASOURCE_NAME.equals(dataSourceName)) {
                return;
            }
            secondaryDataSources.put(dataSourceName, dataSource);
        });
        return secondaryDataSources;
    }

    public Map<String, DataSource> getDataSources() {
        return cachedDataSources;
    }

    /*********************** about context ***************************/

    public void set(String dataSourceName) {
        Deque<String> deque = context.get();
        deque.push(dataSourceName);
    }

    public String get() {
        Deque<String> deque = context.get();
        return deque.peek();
    }

    public void clear() {
        Deque<String> deque = context.get();
        if (deque.isEmpty()) {
            return;
        }
        deque.pop();
    }

    public void remove() {
        context.remove();
    }
}
