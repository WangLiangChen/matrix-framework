package liangchen.wang.matrix.framework.springboot.monitor;

import com.google.common.base.Splitter;
import liangchen.wang.matrix.framework.commons.enumeration.Symbol;
import liangchen.wang.matrix.framework.commons.exception.MatrixErrorException;
import liangchen.wang.matrix.framework.commons.exception.MatrixInfoException;
import liangchen.wang.matrix.framework.commons.utils.CollectionUtil;
import liangchen.wang.matrix.framework.commons.utils.PrettyPrinter;
import liangchen.wang.matrix.framework.commons.utils.StringUtil;
import liangchen.wang.matrix.framework.springboot.context.BeanLoader;
import liangchen.wang.matrix.framework.springboot.context.ConfigContext;
import liangchen.wang.matrix.framework.springboot.processor.HighestPriorityBeanDefinitionRegistryPostProcessor;
import org.apache.commons.configuration2.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.event.*;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.*;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author LiangChen.Wang 2021/7/2
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StartProcessMonitor implements EnvironmentPostProcessor,
        ApplicationContextInitializer<ConfigurableApplicationContext>,
        SpringApplicationRunListener,
        ApplicationListener<ApplicationEvent>,
        SmartLifecycle,
        ApplicationRunner,
        CommandLineRunner,
        InitializingBean,
        DisposableBean,
        BeanNameAware,
        BeanFactoryAware,
        ApplicationContextAware,
        BeanClassLoaderAware,
        EnvironmentAware,
        ResourceLoaderAware,
        EmbeddedValueResolverAware,
        ApplicationEventPublisherAware,
        MessageSourceAware {
    private final String DEFAULT_PACKAGES = "liangchen.wang.matrix";
    private final String CONFIG_ROOT = "configRoot";
    private final String EXCLUDE_SCAN_PACKAGES = "exclude.scan.packages";
    private final Class<?>[] events = new Class[]{ApplicationStartingEvent.class,
            ApplicationEnvironmentPreparedEvent.class,
            ApplicationContextInitializedEvent.class,
            ApplicationPreparedEvent.class,
            ContextRefreshedEvent.class,
            ApplicationStartedEvent.class,
            ApplicationReadyEvent.class,
            ApplicationFailedEvent.class,
            ContextClosedEvent.class};
    private final String[] excludeAutoConfigure = new String[]{
            AopAutoConfiguration.class.getName(),
            ErrorMvcAutoConfiguration.class.getName(),
            ErrorWebFluxAutoConfiguration.class.getName(),
            WebMvcAutoConfiguration.class.getName(),
            WebFluxAutoConfiguration.class.getName(),
            "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
            "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration"
    };
    private boolean isRunning;

    public StartProcessMonitor(SpringApplication springApplication, String[] args) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener");
        PrettyPrinter.INSTANCE.buffer("args are:{}", Arrays.asList(args).toString());
        springApplication.setBannerMode(Banner.Mode.OFF);
        PrettyPrinter.INSTANCE.buffer("set BannerMode=OFF");
        String configRoot = resolveConfigRoot();
        ConfigContext.INSTANCE.setBaseUri(configRoot);
        PrettyPrinter.INSTANCE.buffer("set configRoot={}", configRoot);
        // 需要排除扫描的包
        Set<Object> allSources = springApplication.getAllSources();
        String excludeScanPackages = allSources.stream().map(e -> ((Class) e).getPackage().getName()).filter(e -> e.startsWith(DEFAULT_PACKAGES)).collect(Collectors.joining(Symbol.COMMA.getSymbol()));
        // 因为在使用到这个值的地方，跟当前对象不是一个对象，所以通过这种方式传递
        System.setProperty(EXCLUDE_SCAN_PACKAGES, excludeScanPackages);
        PrettyPrinter.INSTANCE.buffer("set exclude.scan.packages={}", excludeScanPackages);
        PrettyPrinter.INSTANCE.flush();
    }

    public StartProcessMonitor() {
    }

    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener");
        Properties defaultProperties = new Properties();
        handleLogger(defaultProperties);
        populateDefaultProperties(defaultProperties);
        PrettyPrinter.INSTANCE.buffer("populateDefaultProperties");
        // 因加载顺序的原因,此时使用springApplication.setDefaultProperties()无效
        environment.getPropertySources().addLast(new PropertiesPropertySource("defaultProperties", defaultProperties));
        PrettyPrinter.INSTANCE.flush();
    }


    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void running(ConfigurableApplicationContext context) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener");
        PrettyPrinter.INSTANCE.buffer("Exception is:{}", exception.getMessage());
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void run(ApplicationArguments args) {
        PrettyPrinter.INSTANCE.buffer("Overrided from ApplicationRunner");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void run(String... args) {
        PrettyPrinter.INSTANCE.buffer("Overrided from CommandLineRunner");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        PrettyPrinter.INSTANCE.buffer("Overrided from EnvironmentPostProcessor");
        PrettyPrinter.INSTANCE.flush();
    }


    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        PrettyPrinter.INSTANCE.buffer("Overrided from ApplicationContextInitializer");
        BeanLoader.INSTANCE.setApplicationContext(applicationContext);
        PrettyPrinter.INSTANCE.buffer("Initialize BeanLoader");
        // 注册一个优先级非常高的BeanFactoryPostProcessor
        applicationContext.getBeanFactory().registerSingleton("highestPriorityBeanDefinitionRegistryPostProcessor", new HighestPriorityBeanDefinitionRegistryPostProcessor());
        PrettyPrinter.INSTANCE.buffer("register HighestPriorityBeanDefinitionRegistryPostProcessor");
        hanldeAutoScan(applicationContext);
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (!CollectionUtil.INSTANCE.contains(events, event.getClass())) {
            return;
        }
        PrettyPrinter.INSTANCE.buffer("Overrided from ApplicationListener");
        PrettyPrinter.INSTANCE.buffer("EventName:{}", event.getClass().getSimpleName());
        PrettyPrinter.INSTANCE.flush();
        if (event instanceof ApplicationReadyEvent) {
            if (isRunning) {
                // 系统启动完成
                System.out.println("------------------------------------> System Started <------------------------------------");
                // 注册一个最后的关闭钩子
                SpringApplication.getShutdownHandlers().add(() -> System.out.println("------------------------------------> System Closed <------------------------------------"));
            }
        }
    }

    @Override
    public void start() {
        this.isRunning = true;
        PrettyPrinter.INSTANCE.buffer("Overrided from Lifecycle");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void stop() {
        this.isRunning = false;
        PrettyPrinter.INSTANCE.buffer("Overrided from Lifecycle");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public boolean isRunning() {
        PrettyPrinter.INSTANCE.buffer("Overrided from Lifecycle");
        PrettyPrinter.INSTANCE.flush();
        return this.isRunning;
    }

    @Override
    public int getPhase() {
        return Integer.MIN_VALUE;
    }


    @Override
    public void destroy() {
        PrettyPrinter.INSTANCE.buffer("Overrided from DisposableBean");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void afterPropertiesSet() {
        PrettyPrinter.INSTANCE.buffer("Overrided from InitializingBean");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        PrettyPrinter.INSTANCE.buffer("Overrided from BeanClassLoaderAware");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        PrettyPrinter.INSTANCE.buffer("Overrided from BeanFactoryAware");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void setBeanName(String beanName) {
        PrettyPrinter.INSTANCE.buffer("Overrided from BeanNameAware");
        PrettyPrinter.INSTANCE.buffer("BeanName is:{}", beanName);
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        PrettyPrinter.INSTANCE.buffer("Overrided from ApplicationContextAware");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        PrettyPrinter.INSTANCE.buffer("Overrided from ApplicationEventPublisherAware");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        PrettyPrinter.INSTANCE.buffer("Overrided from EmbeddedValueResolverAware");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void setEnvironment(Environment environment) {
        PrettyPrinter.INSTANCE.buffer("Overrided from EnvironmentAware");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        PrettyPrinter.INSTANCE.buffer("Overrided from MessageSourceAware");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        PrettyPrinter.INSTANCE.buffer("Overrided from ResourceLoaderAware");
        PrettyPrinter.INSTANCE.flush();
    }

    @PostConstruct
    public void postConstruct() {
        PrettyPrinter.INSTANCE.buffer("@PostConstruct");
        PrettyPrinter.INSTANCE.flush();
    }

    @PreDestroy
    public void preDestroy() {
        PrettyPrinter.INSTANCE.buffer("@PreDestroy");
        PrettyPrinter.INSTANCE.flush();
    }

    private String resolveConfigRoot() {
        String configRoot = System.getenv(CONFIG_ROOT);
        if (StringUtil.INSTANCE.isNotBlank(configRoot)) {
            PrettyPrinter.INSTANCE.buffer("'configRoot' in the 'System.getenv' is :{}", configRoot);
            return configRoot;
        }
        PrettyPrinter.INSTANCE.buffer("'configRoot' in the 'System.getenv' does not exist");
        configRoot = System.getProperty(CONFIG_ROOT);
        if (StringUtil.INSTANCE.isNotBlank(configRoot)) {
            PrettyPrinter.INSTANCE.buffer("'configRoot' in the 'System.getProperty' is :{}", configRoot);
            return configRoot;
        }
        PrettyPrinter.INSTANCE.buffer("'configRoot' in the 'System.getProperty' does not exist");
        Resource resource = new ClassPathResource("root.properties", Thread.currentThread().getContextClassLoader());
        if (!resource.exists()) {
            PrettyPrinter.INSTANCE.flush();
            throw new MatrixInfoException("'root.properties' does not exist in the class path");
        }
        Properties properties = new Properties();
        try (InputStream in = resource.getInputStream()) {
            properties.load(in);
        } catch (IOException e) {
            PrettyPrinter.INSTANCE.flush();
            throw new MatrixErrorException("An error occurred when looking for 'root.properties' file:" + e.getMessage());
        }
        configRoot = properties.getProperty(CONFIG_ROOT);
        if (StringUtil.INSTANCE.isNotBlank(configRoot)) {
            PrettyPrinter.INSTANCE.buffer("'configRoot' in the 'root.properties' is :{}", configRoot);
            return configRoot;
        }
        PrettyPrinter.INSTANCE.flush();
        throw new MatrixInfoException("'configRoot' in the 'root.properties' does not exist");
    }

    private void handleLogger(Properties defaultProperties) {
        final String LOGGER_ROOT = "framework/logger.properties";
        final String LOGGING_CONFIG = "logging.config";
        final String CONFIG_FILE = "config.file";
        Configuration configuration = ConfigContext.INSTANCE.resolve(LOGGER_ROOT);
        /*配置默认属性
        使用外置Tomcat时,Tomcat会设置环境变量logging.config
        在org.springframework.boot.logging.LoggingApplicationListener#initializeSystem中
        String logConfig = environment.getProperty(CONFIG_PROPERTY);是在 systemProperties 的 PropertySource 中拿到了日志的配置
        下面覆盖这个配置
        */
        String configFile = configuration.getString(CONFIG_FILE);
        if (StringUtil.INSTANCE.isBlank(configFile)) {
            PrettyPrinter.INSTANCE.flush();
            throw new MatrixInfoException("'config.file' does not exist in the file:" + LOGGER_ROOT);
        }
        configFile = ConfigContext.INSTANCE.getURI(LOGGER_ROOT).resolve(configFile).toString();
        System.setProperty(LOGGING_CONFIG, configFile);
        PrettyPrinter.INSTANCE.buffer("set {} is:{}", LOGGING_CONFIG, configFile);
        defaultProperties.setProperty(LOGGING_CONFIG, configFile);
    }

    private void populateDefaultProperties(Properties defaultProperties) {
        defaultProperties.setProperty("spring.jpa.properties.javax.persistence.validation.mode", "none");
        defaultProperties.setProperty("spring.data.jpa.repositories.enabled", "false");
        defaultProperties.setProperty(" spring.data.jpa.repositories.bootstrap-mode", "default");
        for (int i = 0; i < excludeAutoConfigure.length; i++) {
            defaultProperties.setProperty(StringUtil.INSTANCE.format("spring.autoconfigure.exclude[{}]", i), excludeAutoConfigure[i]);
        }
    }

    private void hanldeAutoScan(ConfigurableApplicationContext applicationContext) {
        // 处理自动扫描和排除扫描项目
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        BeanDefinitionRegistry beanRegistry = (BeanDefinitionRegistry) beanFactory;
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanRegistry);
        scanner.setResourceLoader(applicationContext);
        // 获取要排除扫描的包
        String excludeScanPackages = System.getProperty(EXCLUDE_SCAN_PACKAGES, Symbol.BLANK.getSymbol());
        System.clearProperty(EXCLUDE_SCAN_PACKAGES);
        List<String> excludeScanList = Splitter.on(',').omitEmptyStrings().splitToList(excludeScanPackages);
        if (CollectionUtil.INSTANCE.isNotEmpty(excludeScanList)) {
            scanner.addExcludeFilter((metadataReader, metadataReaderFactory) -> {
                String className = metadataReader.getClassMetadata().getClassName();
                for (String p : excludeScanList) {
                    return className.startsWith(p);
                }
                return false;
            });
        }
        // 获取自动扫描配置
        Configuration configuration = ConfigContext.INSTANCE.resolve("framework/autoscan.properties");
        String autoScanPackages = configuration.getString("packages", Symbol.BLANK.getSymbol());
        autoScanPackages = String.format("%s,%s", DEFAULT_PACKAGES, autoScanPackages);
        List<String> autoScanList = Splitter.on(',').omitEmptyStrings().splitToList(autoScanPackages);
        PrettyPrinter.INSTANCE.buffer("set auto.scan.packages={}", autoScanList);
        scanner.scan(autoScanList.toArray(new String[autoScanList.size()]));
    }
}
