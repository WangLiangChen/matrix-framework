package wang.liangchen.matrix.framework.data.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.data.datasource.dialect.AbstractDialect;

/**
 * @author LiangChen.Wang
 */
public class MultiDataSource extends AbstractRoutingDataSource {
    private final Logger logger = LoggerFactory.getLogger(MultiDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceName = MultiDataSourceContext.INSTANCE.get();
        if (StringUtil.INSTANCE.isBlank(dataSourceName)) {
            dataSourceName = MultiDataSourceContext.INSTANCE.PRIMARY_DATASOURCE_NAME;
            logger.debug("Use Default DataSource: {}", dataSourceName);
        }
        AbstractDialect dialect = MultiDataSourceContext.INSTANCE.getDialect(dataSourceName);
        logger.debug("Current DataSource: {}, Dialect: {}", dataSourceName, dialect.getClass().getSimpleName());
        return dataSourceName;
    }
}
