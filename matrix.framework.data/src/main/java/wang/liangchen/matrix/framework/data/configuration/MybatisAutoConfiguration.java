package wang.liangchen.matrix.framework.data.configuration;

import com.google.common.base.Splitter;
import wang.liangchen.matrix.framework.commons.exception.AssertUtil;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.utils.CollectionUtil;
import wang.liangchen.matrix.framework.commons.utils.PrettyPrinter;
import wang.liangchen.matrix.framework.commons.utils.StringUtil;
import wang.liangchen.matrix.framework.data.mybatis.interceptor.PaginationInterceptor;
import wang.liangchen.matrix.framework.springboot.context.ConfigurationContext;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.MapWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2021-10-20 11:37
 */
public class MybatisAutoConfiguration {
    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private final static String AUTOSCAN_COFIG_FILE = "framework/autoscan.properties";
    private final static String DEFAULT_SCAN_PACKAGE = "wang.liangchen.matrix";
    private static String scanPackages;

    static {
        boolean exists = ConfigurationContext.INSTANCE.exists(AUTOSCAN_COFIG_FILE);
        AssertUtil.INSTANCE.isTrue(exists, "Configuration file:{} does not exists", AUTOSCAN_COFIG_FILE);
        Configuration configuration = ConfigurationContext.INSTANCE.resolve(AUTOSCAN_COFIG_FILE);
        String mybatis = configuration.getString("mybatis");
        if (StringUtil.INSTANCE.isBlank(mybatis)) {
            scanPackages = DEFAULT_SCAN_PACKAGE;
        } else {
            scanPackages = String.format("%s,%s", DEFAULT_SCAN_PACKAGE, mybatis);
        }
    }

    /*
    MapperScannerConfigurer 是 BeanFactoryPostProcessor 的一个实现，这类Bean会特别早的被初始化。会导致其所在的Configuration类过早初始化
    静态方法不需要依赖所在类的实例即可运行，用这种方式防止MybatisAutoConfiguration过早实例化
    static关键字一般有且仅用于@Bean方法返回为BeanPostProcessor、BeanFactoryPostProcessor等类型的方法
    https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/core.html#beans
    Also, be particularly careful with BeanPostProcessor and BeanFactoryPostProcessor definitions through @Bean. Those should usually be declared as static @Bean methods, not triggering the instantiation of their containing configuration class. Otherwise, @Autowired and @Value do not work on the configuration class itself, since it is being created as a bean instance too early.
    另外，通过@Bean使用BeanPostProcessor和BeanFactoryPostProcessor定义时要特别小心。 通常应将这些声明为静态@Bean方法，而不触发其包含的配置类的实例化。 否则，@Autowired和@Value不适用于配置类本身，因为它太早被创建为Bean实例。
    */
    @Bean
    public static MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        //mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setSqlSessionTemplateBeanName("sqlSessionTemplate");
        // 指定扫描的类型,只扫描有注解的接口
        // mapperScannerConfigurer.setMarkerInterface(IAbstractDao.class);
        mapperScannerConfigurer.setAnnotationClass(Mapper.class);
        mapperScannerConfigurer.setBasePackage(scanPackages);
        PrettyPrinter.INSTANCE.buffer("mapper scan packages:", scanPackages);
        PrettyPrinter.INSTANCE.flush();
        return mapperScannerConfigurer;
    }


    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(javax.sql.DataSource dataSource, @Nullable Interceptor[] interceptors) {
        // set DataSource
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        // set ConfigLocation
        URL url = ConfigurationContext.INSTANCE.getURL("framework/mybatis-config.xml");
        Resource configLocation = new UrlResource(url);
        if (configLocation.exists()) {
            sqlSessionFactoryBean.setConfigLocation(configLocation);
        } else {
            PrettyPrinter.INSTANCE.buffer("can't find mybatis-config.xml,use default configuration");
        }
        // other config
        Properties mybatisConfiguration = new Properties();
        mybatisConfiguration.put("cacheEnabled", false);
        mybatisConfiguration.put("localCacheScope", LocalCacheScope.STATEMENT.name());
        mybatisConfiguration.put("mapUnderscoreToCamelCase", true);
        sqlSessionFactoryBean.setConfigurationProperties(mybatisConfiguration);

        // 设置要扫描mapper.xml
        @SuppressWarnings("UnstableApiUsage")
        Set<String> packages = Splitter.on(',').splitToStream(scanPackages).map(e -> e.replace('.', '/')).collect(Collectors.toSet());
        Resource[] mapperLocations = new Resource[0];
        for (String pack : packages) {
            try {
                Resource[] mappers = resourcePatternResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX.concat(pack).concat("/**/*.mapper.xml"));
                mapperLocations = ArrayUtils.addAll(mapperLocations, mappers);
            } catch (IOException e) {
                throw new MatrixErrorException(e);
            }
        }
        if (CollectionUtil.INSTANCE.isEmpty(mapperLocations)) {
            PrettyPrinter.INSTANCE.buffer("can't find mybatis mapper file,pattern is:/**/*.mapper.xml");
            return sqlSessionFactoryBean;
        }
        PrettyPrinter.INSTANCE.buffer("find mybatis mapper file:");
        for (Resource resource : mapperLocations) {
            PrettyPrinter.INSTANCE.buffer(resource.toString());
        }
        sqlSessionFactoryBean.setMapperLocations(mapperLocations);

        // add plugins interceptors
        if (CollectionUtil.INSTANCE.isNotEmpty(interceptors)) {
            sqlSessionFactoryBean.setPlugins(interceptors);
        }
        //sqlSessionFactoryBean.setTypeAliasesPackage(typeAliasPackage);
        return sqlSessionFactoryBean;
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setObjectWrapperFactory(new ObjectWrapperFactory() {
            @Override
            public boolean hasWrapperFor(Object object) {
                return object instanceof Map;
            }

            @Override
            @SuppressWarnings("unchecked")
            public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
                return new MapWrapper(metaObject, (Map<String, Object>) object) {
                    @Override
                    public String findProperty(String name, boolean useCamelCaseMapping) {
                        if (useCamelCaseMapping) {
                            return StringUtil.INSTANCE.underline2camelCase(name);
                        }
                        return name;
                    }
                };
            }
        });
    }

    @Bean
    public Interceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
