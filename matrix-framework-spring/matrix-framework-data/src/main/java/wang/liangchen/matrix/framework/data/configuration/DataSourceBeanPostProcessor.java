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
import wang.liangchen.matrix.framework.data.context.DataSourceContext;
import wang.liangchen.matrix.framework.data.datasource.MatrixDataSourceProperties;
import wang.liangchen.matrix.framework.data.datasource.MatrixMultiDataSourceProperties;
import wang.liangchen.matrix.framework.data.datasource.MatrixPrimaryDataSourceProperties;
import wang.liangchen.matrix.framework.data.datasource.RoutingDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DataSourceBeanPostProcessor implements EnvironmentAware, BeanClassLoaderAware, InstantiationAwareBeanPostProcessor, Ordered {
    private final static String DATASOURCE_PREFIX = "spring.datasource";
    private final static String MATRIX_DATASOURCE_PREFIX = "spring.datasource.matrix";
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
        datasources.forEach((name, properties) -> {
            properties.setName(name);
            properties.setBeanClassLoader(classLoader);
            EmbeddedDatabaseConnection embeddedDatabaseConnection = properties.getEmbeddedDatabaseConnection();
            // simulate afterPropertiesSet
            if (null == embeddedDatabaseConnection) {
                properties.setEmbeddedDatabaseConnection(EmbeddedDatabaseConnection.get(this.classLoader));
            }
            String driverClassName = properties.determineDriverClassName();




            DataSource dataSource = properties.initializeDataSourceBuilder().build();


            // bind other properties
            String prefix = DataSourceContext.PRIMARY_DATASOURCE_NAME.equals(name) ? DATASOURCE_PREFIX : MATRIX_DATASOURCE_PREFIX;
            for (String bindableName : bindableNames) {
                BindResult<DataSource> bindDataSourceResult = Binder.get(environment).bind(prefix + "." + bindableName, Bindable.ofInstance(dataSource));
                if (bindDataSourceResult.isBound()) {
                    dataSource = bindDataSourceResult.get();
                    break;
                }
            }
            DataSourceContext.INSTANCE.putDataSource(name, dataSource, null);
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
