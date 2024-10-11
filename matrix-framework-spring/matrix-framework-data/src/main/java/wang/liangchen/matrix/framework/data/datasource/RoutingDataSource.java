package wang.liangchen.matrix.framework.data.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import wang.liangchen.matrix.framework.data.context.DataSourceContext;

/**
 * @author Liangchen.Wang
 */
public class RoutingDataSource extends AbstractRoutingDataSource {
    private final Logger logger = LoggerFactory.getLogger(RoutingDataSource.class);


    @Override
    protected Object determineCurrentLookupKey() {
        // 从线程上下文中获取数据源名称
        String dataSourceName = DataSourceContext.INSTANCE.get();
        //AbstractDialect dialect = DataSourceContext.INSTANCE.getDialect(dataSourceName);
        //logger.debug("Current DataSource: {}, Dialect: {}", dataSourceName, dialect);
        return dataSourceName;
    }
}
