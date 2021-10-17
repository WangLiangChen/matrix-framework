package liangchen.wang.matrix.framework.data.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author LiangChen.Wang
 */
public class MultiDataSource extends AbstractRoutingDataSource {
    private final Logger logger = LoggerFactory.getLogger(MultiDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceName = MultiDataSourceContext.INSTANCE.get();
        if (null == dataSourceName || dataSourceName.length() == 0) {
            dataSourceName = "primary";
            MultiDataSourceContext.INSTANCE.set(dataSourceName);
        }
        logger.debug("Current DataSource: {}", dataSourceName);
        return dataSourceName;
    }
}
