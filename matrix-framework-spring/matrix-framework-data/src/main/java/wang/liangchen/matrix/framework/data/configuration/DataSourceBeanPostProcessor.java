package wang.liangchen.matrix.framework.data.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.data.context.DataSourceContext;
import wang.liangchen.matrix.framework.data.datasource.MatrixDataSourceProperties;
import wang.liangchen.matrix.framework.data.datasource.MatrixMultiDataSourceProperties;
import wang.liangchen.matrix.framework.data.datasource.MatrixPrimaryDataSourceProperties;
import wang.liangchen.matrix.framework.data.datasource.RoutingDataSource;
import wang.liangchen.matrix.framework.data.datasource.dialect.AbstractDialect;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DataSourceBeanPostProcessor implements EnvironmentAware, BeanClassLoaderAware, InstantiationAwareBeanPostProcessor, Ordered {
    private final static String DATASOURCE_PREFIX = "spring.datasource";
    private final static String MATRIX_DATASOURCE_PREFIX = "spring.datasource.matrix.datasources";
    private final static String DATASOURCE_BEAN_NAME = "dataSource";

    private final static List<String> bindableNames = new ArrayList<>() {{
        add("hikari");
        add("druid");
        add("dbcp2");
        add("oracleucp");
    }};
    private Environment environment;
    private ClassLoader classLoader;

    @Override
    public Object postProcessBeforeInstantiation(@NonNull Class<?> beanClass, @NonNull String beanName) throws BeansException {
        if (!DATASOURCE_BEAN_NAME.equals(beanName)) {
            return null;
        }
        BindResult<MatrixPrimaryDataSourceProperties> bindResult = Binder.get(environment).bind(DATASOURCE_PREFIX, MatrixPrimaryDataSourceProperties.class);
        if (!bindResult.isBound()) {
            return null;
        }
        MatrixPrimaryDataSourceProperties primaryDataSourceProperties = bindResult.get();
        MatrixMultiDataSourceProperties multiDataSourceProperties = primaryDataSourceProperties.getMatrix();
        if (null == multiDataSourceProperties) {
            return null;
        }
        Map<String, MatrixDataSourceProperties> datasources = multiDataSourceProperties.getDatasources();
        datasources.put(DataSourceContext.PRIMARY_DATASOURCE_NAME, primaryDataSourceProperties);
        datasources.forEach((dataSourceName, dataSourceProperties) -> {
            dataSourceProperties.setName(dataSourceName);
            dataSourceProperties.setBeanClassLoader(classLoader);
            EmbeddedDatabaseConnection embeddedDatabaseConnection = dataSourceProperties.getEmbeddedDatabaseConnection();
            // simulate afterPropertiesSet
            if (null == embeddedDatabaseConnection) {
                dataSourceProperties.setEmbeddedDatabaseConnection(EmbeddedDatabaseConnection.get(this.classLoader));
            }
            // resolve dialect
            String driverClassName = dataSourceProperties.determineDriverClassName();
            AbstractDialect dialect = AbstractDialect.getDialect(driverClassName);
            if (null == dialect) {
                throw new MatrixErrorException("Unsupported database driver: {}, Please contact the author.", driverClassName);
            }
            DataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().build();
            // bind other properties to dataSource
            String prefix = DataSourceContext.PRIMARY_DATASOURCE_NAME.equals(dataSourceName) ? DATASOURCE_PREFIX : MATRIX_DATASOURCE_PREFIX.concat(Symbol.DOT.getSymbol()).concat(dataSourceName);
            for (String bindableName : bindableNames) {
                BindResult<DataSource> bindDataSourceResult = Binder.get(environment).bind(prefix.concat(Symbol.DOT.getSymbol()).concat(bindableName), Bindable.ofInstance(dataSource));
                if (bindDataSourceResult.isBound()) {
                    dataSource = bindDataSourceResult.get();
                    break;
                }
            }
            // put dataSource to DataSourceContext
            DataSourceContext.INSTANCE.putDataSource(dataSourceName, dataSource, dialect);
        });


        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setDefaultTargetDataSource(DataSourceContext.INSTANCE.getPrimaryDataSource());
        Map<Object, Object> targetDataSources = DataSourceContext.INSTANCE.getDataSources().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanClassLoader(@NonNull ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
