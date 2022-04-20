package wang.liangchen.matrix.framework.data.annotation;

import org.apache.commons.configuration2.Configuration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;
import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.commons.utils.PrettyPrinter;
import wang.liangchen.matrix.framework.data.configuration.ComponentAutoConfiguration;
import wang.liangchen.matrix.framework.data.configuration.JdbcAutoConfiguration;
import wang.liangchen.matrix.framework.data.configuration.MybatisAutoConfiguration;
import wang.liangchen.matrix.framework.data.datasource.MultiDataSourceContext;
import wang.liangchen.matrix.framework.data.datasource.MultiDataSourceRegister;
import wang.liangchen.matrix.framework.data.datasource.dialect.AbstractDialect;
import wang.liangchen.matrix.framework.data.datasource.dialect.MySQLDialect;
import wang.liangchen.matrix.framework.data.enumeration.DataStatus;
import wang.liangchen.matrix.framework.springboot.context.ConfigurationContext;

import javax.sql.DataSource;
import java.lang.annotation.*;
import java.util.*;

/**
 * @author LiangChen.Wang
 * 开启JDBC 根据配置加载初始化数据源
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({EnableJdbc.JdbcImportSelector.class})
@Order(Ordered.HIGHEST_PRECEDENCE)
@SuppressWarnings("NullableProblems")
public @interface EnableJdbc {
    class JdbcImportSelector implements ImportSelector {
        private final String JDBC_CONFIG_FILE = "jdbc.properties";
        private final Object[] requiredKeys = new String[]{"dialect", "datasource", "host", "port", "database", "username", "password"};
        private final String DIALECT_ITEM = "dialect", URL_ITEM = "url", EXTRA_ITEM = "extra";
        private static volatile boolean loaded = false;

        @Override
        public String[] selectImports(AnnotationMetadata annotationMetadata) {
            if (loaded) {
                return new String[0];
            }
            boolean exists = ConfigurationContext.INSTANCE.exists(JDBC_CONFIG_FILE);
            Assert.INSTANCE.isTrue(exists, "Configuration file: {} is required,because @EnableJdbc is setted", JDBC_CONFIG_FILE);

            PrettyPrinter.INSTANCE.buffer("@EnableJdbc......");
            PrettyPrinter.INSTANCE.buffer("@EnableJdbc matched class: {}", annotationMetadata.getClassName());
            instantiateDataSource();
            String[] imports = new String[]{MultiDataSourceRegister.class.getName(), AutoProxyRegistrar.class.getName(), JdbcAutoConfiguration.class.getName(), MybatisAutoConfiguration.class.getName(), ComponentAutoConfiguration.class.getName()};
            // 设置全局jdbc状态
            DataStatus.INSTANCE.setJdbcEnabled(true);
            loaded = true;
            PrettyPrinter.INSTANCE.flush();
            return imports;
        }

        private void instantiateDataSource() {
            Configuration configuration = ConfigurationContext.INSTANCE.resolve(JDBC_CONFIG_FILE);
            Iterator<String> keys = configuration.getKeys();
            Map<String, Properties> dataSourcePropertiesMap = new LinkedHashMap<>();
            keys.forEachRemaining(key -> {
                int firstDot = key.indexOf('.');
                String dataSourceName = key.substring(0, firstDot);
                Properties properties = dataSourcePropertiesMap.computeIfAbsent(dataSourceName, k -> new Properties());
                String item = key.substring(firstDot + 1);
                properties.put(item, configuration.getProperty(key));
            });
            ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases("datasource", "type");
            dataSourcePropertiesMap.forEach((dataSourceName, properties) -> {
                List<Object> requiredKeyList = Arrays.asList(requiredKeys);
                requiredKeyList.retainAll(properties.keySet());
                Assert.INSTANCE.isTrue(requiredKeys.length == requiredKeyList.size(), "DataSource: {}, configuration items :{} are required!", dataSourceName, requiredKeyList);

                ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
                Binder binder = new Binder(source.withAliases(aliases));
                DataSourceProperties dataSourceProperties = binder.bind(ConfigurationPropertyName.EMPTY, Bindable.of(DataSourceProperties.class)).get();
                AbstractDialect dialect = ClassUtil.INSTANCE.instantiate(properties.getProperty(DIALECT_ITEM));
                if (StringUtil.INSTANCE.isBlank(properties.getProperty(URL_ITEM))) {
                    String query, url = null;
                    if (dialect instanceof MySQLDialect) {
                        query = "serverTimezone=GMT%2B8&characterEncoding=utf-8&characterSetResults=utf-8&useUnicode=true&useSSL=false&nullCatalogMeansCurrent=true&allowPublicKeyRetrieval=true";
                        url = String.format("jdbc:mysql://%s:%s/%s?%s", properties.get("host"), properties.get("port"), properties.get("database"), query);
                    }
                    dataSourceProperties.setUrl(url);
                }
                dataSourceProperties.setBeanClassLoader(this.getClass().getClassLoader());
                try {
                    dataSourceProperties.afterPropertiesSet();
                } catch (Exception e) {
                    throw new IllegalStateException("Can't init dataSource:" + dataSourceName, e);
                }
                DataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().build();
                binder.bind(EXTRA_ITEM, Bindable.ofInstance(dataSource));
                MultiDataSourceContext.INSTANCE.putDialect(dataSourceName, dialect);
                MultiDataSourceContext.INSTANCE.putDataSource(dataSourceName, dataSource);
            });
        }
    }
}
