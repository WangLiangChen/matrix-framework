package liangchen.wang.matrix.framework.springboot.initializer;

import liangchen.wang.gradf.framework.commons.exception.ErrorException;
import liangchen.wang.gradf.framework.commons.exception.InfoException;
import liangchen.wang.gradf.framework.commons.utils.ConfigurationUtil;
import liangchen.wang.gradf.framework.commons.utils.NetUtil;
import liangchen.wang.gradf.framework.commons.utils.Printer;
import liangchen.wang.gradf.framework.commons.utils.StringUtil;
import liangchen.wang.gradf.framework.springboot.context.BeanLoader;
import liangchen.wang.gradf.framework.springboot.processor.FirstBeanDefinitionRegistryPostProcessor;
import org.apache.commons.configuration2.Configuration;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author LiangChen.Wang
 */
public class DefaultApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, EnvironmentPostProcessor {
    private final static String DEFAULT_PACKAGES = "liangchen.wang.gradf";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication springApplication) {
        springApplication.setBannerMode(Banner.Mode.OFF);
        String configPath = resolveConfigPath(environment);
        ConfigurationUtil.INSTANCE.setBasePath(configPath);
        Printer.INSTANCE.prettyPrint("available configPath is: {}", ConfigurationUtil.INSTANCE.getBasePath());
        // 设置日志
        Configuration configuration = ConfigurationUtil.INSTANCE.getConfiguration("logger.properties");
        String logging_config = configuration.getString("logging.config");
        if (StringUtil.INSTANCE.isBlank(logging_config)) {
            throw new InfoException("logging.config is not set in the file 'logger.properties'");
        }
        URL logging_config_url = ConfigurationUtil.INSTANCE.getFullUrl(logging_config);
        logging_config = logging_config_url.toString();
        Printer.INSTANCE.prettyPrint("logging.config url is :{}", logging_config);
        String logging_file_path = configuration.getString("logging.file.path");
        if (StringUtil.INSTANCE.isBlank(logging_file_path)) {
            throw new InfoException("logging.file.path is not set");
        }
        Path logOutputPath = Paths.get(logging_file_path);
        if (logOutputPath.isAbsolute()) {
            Printer.INSTANCE.prettyPrint("logging.path is :{}", logging_file_path);
        } else {
            logOutputPath = Paths.get(configPath, logging_file_path);
            Printer.INSTANCE.prettyPrint("log file path is relative path,absolute path is:{}", logOutputPath.toAbsolutePath());
        }
        logging_file_path = logOutputPath.toString();
        Properties defaultProperties = new Properties();
        //配置默认属性
        //外置Tomcat容器启动的时候，Tomcat设置的环境会覆盖上面的设置，可以调试 org.springframework.boot.logging.LoggingApplicationListener#initializeSystem
        //中的String logConfig = environment.getProperty(CONFIG_PROPERTY);是在 systemProperties 的 PropertySource 中拿到了日志的配置
        //补充下面的语句使日志配置生效
        System.setProperty("logging.config", logging_config);
        System.setProperty("logging.file.path", logging_file_path);
        defaultProperties.setProperty("logging.config", logging_file_path);
        defaultProperties.setProperty("logging.file.path", logging_file_path);

        defaultProperties.setProperty("spring.jpa.properties.javax.persistence.validation.mode", "none");
        defaultProperties.setProperty("spring.data.jpa.repositories.enabled", "false");
        defaultProperties.setProperty(" spring.data.jpa.repositories.bootstrap-mode", "default");
        defaultProperties.setProperty("spring.autoconfigure.exclude[0]", "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration");
        defaultProperties.setProperty("spring.autoconfigure.exclude[1]", "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration");
        defaultProperties.setProperty("spring.autoconfigure.exclude[2]", "org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration");
        defaultProperties.setProperty("spring.autoconfigure.exclude[3]", "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration");
        defaultProperties.setProperty("spring.autoconfigure.exclude[4]", "org.springframework.boot.autoconfigure.aop.AopAutoConfiguration");
        defaultProperties.setProperty("spring.autoconfigure.exclude[5]", "org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration");

        // 将需要排除扫描的包放入Environment
        Set<Object> allSources = springApplication.getAllSources();
        String excludeScan = allSources.stream().map(e -> ((Class) e).getPackage().getName()).filter(e -> e.startsWith(DEFAULT_PACKAGES)).collect(Collectors.joining(","));
        System.setProperty("exclude.scan", excludeScan);
        // 因加载顺序的原因,此处使用springApplication.setDefaultProperties()无效
        environment.getPropertySources().addLast(new PropertiesPropertySource("defaultProperties", defaultProperties));
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        BeanLoader.INSTANCE.setApplicationContext(applicationContext);
        // 注册一个优先级非常高的BeanFactoryPostProcessor
        applicationContext.getBeanFactory().registerSingleton("firstBeanFactoryPostProcessor", new FirstBeanDefinitionRegistryPostProcessor());

        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        BeanDefinitionRegistry beanRegistry = (BeanDefinitionRegistry) beanFactory;
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanRegistry);
        scanner.setResourceLoader(applicationContext);
        // 判断是否排除当前扫描
        String excludeScan = System.getProperty("exclude.scan");
        System.clearProperty("exclude.scan");
        if (StringUtil.INSTANCE.isNotBlank(excludeScan)) {
            scanner.addExcludeFilter((metadataReader, metadataReaderFactory) -> {
                String className = metadataReader.getClassMetadata().getClassName();
                String[] packages = excludeScan.split(",");
                for (String p : packages) {
                    return className.startsWith(p);
                }
                return false;
            });
        }
        Configuration config = ConfigurationUtil.INSTANCE.getConfiguration("autoscan.properties");
        String scanPackages = config.getString("spring");
        if (StringUtil.INSTANCE.isBlank(scanPackages)) {
            Printer.INSTANCE.prettyPrint("Spring Scan Packages Configuration is Blank");
            scanPackages = DEFAULT_PACKAGES;
        } else {
            scanPackages = String.format("%s,%s", DEFAULT_PACKAGES, scanPackages);
        }
        scanner.scan(scanPackages.split(","));
        Printer.INSTANCE.prettyPrint("Spring Scan Packages is :{}", scanPackages);
    }

    private String resolveConfigPath(Environment environment) {
        // first
        String configPath = System.getProperty("configPath");
        if (StringUtil.INSTANCE.isNotBlank(configPath)) {
            Printer.INSTANCE.prettyPrint("configPath from System variables is :{}", configPath);
            return configPath;
        }
        Printer.INSTANCE.prettyPrint("Can't find 'configPath' from System variables");
        // second
        configPath = environment.getProperty("configPath");
        if (StringUtil.INSTANCE.isNotBlank(configPath)) {
            Printer.INSTANCE.prettyPrint("configPath from Environment variables is :{}", configPath);
            return configPath;
        }
        Printer.INSTANCE.prettyPrint("Can't find 'configPath' from Environment variables");
        // third,lookup root.properties
        Resource resource = new ClassPathResource("root.properties", Thread.currentThread().getContextClassLoader());
        if (!resource.exists()) {
            throw new InfoException("configPath does not set");
        }
        Properties properties = new Properties();
        try {
            URI uri = resource.getURI();
            Printer.INSTANCE.prettyPrint("find root.properties:{}", uri.getPath());
            properties.load(resource.getInputStream());
        } catch (Exception e) {
            throw new ErrorException(e, "An error occurred when looking for 'root.properties' file:" + e.getMessage());
        }
        String configPaths = properties.getProperty("configPath");
        if (StringUtil.INSTANCE.isBlank(configPaths)) {
            throw new InfoException("'configPath' is not set in 'root.properties'");
        }
        configPath = splitConfigRootPaths(configPaths);
        Printer.INSTANCE.prettyPrint("configPath from 'root.properties' is :{}", configPath);
        return configPath;
    }

    private String splitConfigRootPaths(String configPaths) {
        String[] split = configPaths.split(",");
        URL url;
        String configPath = null;
        for (String path : split) {
            url = NetUtil.INSTANCE.toURL(path);
            if (NetUtil.INSTANCE.isAvailableURL(url)) {
                configPath = path;
                break;
            }
        }
        if (StringUtil.INSTANCE.isBlank(configPath)) {
            throw new InfoException("All configPath is unavailable");
        }
        return configPath;
    }
}