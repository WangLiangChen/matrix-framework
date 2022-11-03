package wang.liangchen.matrix.framework.springboot.env;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.event.*;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.EnvironmentPostProcessorApplicationListener;
import org.springframework.context.*;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;
import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.utils.PrettyPrinter;
import wang.liangchen.matrix.framework.springboot.context.BeanLoader;
import wang.liangchen.matrix.framework.springboot.context.MessageSourceLoader;
import wang.liangchen.matrix.framework.springboot.processor.HighestPriorityBeanDefinitionRegistryPostProcessor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author LiangChen.Wang 2021/7/2
 */
@SuppressWarnings("NullableProblems")
@Component
@EnableAsync
@EnableScheduling
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StartProcessMonitor implements
        EnvironmentPostProcessor,
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
        MessageSourceAware,
        Ordered {
    private final static String DEFAULT_SCAN_PACKAGES = "wang.liangchen.matrix";
    private final static String STARTING = Symbol.LINE_SEPARATOR.getSymbol() + "------------------------------------> Matrix Framework is Starting <------------------------------------" + Symbol.LINE_SEPARATOR.getSymbol();
    private final static String STARTED = Symbol.LINE_SEPARATOR.getSymbol() + "------------------------------------> Matrix Framework is Started <------------------------------------" + Symbol.LINE_SEPARATOR.getSymbol();
    private final static String CLOSED = Symbol.LINE_SEPARATOR.getSymbol() + "------------------------------------> Matrix Framework is Closed <------------------------------------" + Symbol.LINE_SEPARATOR.getSymbol();
    private final static String SPRING_CONFIG_IMPORT = "spring.config.import";
    private static Set<String> excludeScanPackages;
    private final static Class<?>[] events = new Class[]{
            ApplicationStartingEvent.class,
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
            "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration"
    };
    private boolean isRunning;

    public StartProcessMonitor(SpringApplication springApplication, String[] args) {
        // 注册一个关闭钩子，打印系统关闭信息
        SpringApplication.getShutdownHandlers().add(() -> System.out.println(CLOSED));
        // 打印系统开始启动信息
        System.out.println(STARTING);

        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener");
        PrettyPrinter.INSTANCE.buffer("args are:{}", Arrays.asList(args).toString());
        springApplication.setBannerMode(Banner.Mode.OFF);
        PrettyPrinter.INSTANCE.buffer("set BannerMode=OFF");

        // 需要排除主动扫描的包,防止wang.liangchen.matrix包被重复扫描
        // 因为在使用到这个值的地方，跟当前对象不是一个对象，所以用类变量传递
        Set<Object> allSources = springApplication.getAllSources();
        excludeScanPackages = allSources.stream().map(e -> ((Class<?>) e).getPackage().getName())
                .filter(e -> e.startsWith(DEFAULT_SCAN_PACKAGES)).collect(Collectors.toSet());
        PrettyPrinter.INSTANCE.buffer("set excludeScanPackages={}", excludeScanPackages);
        PrettyPrinter.INSTANCE.flush();
    }

    public StartProcessMonitor() {
    }

    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener").flush();
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener");
        // 设置默认的配置根路径 classpath
        environment.getSystemProperties().put(SPRING_CONFIG_IMPORT, "matrix://".concat(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX));
        PrettyPrinter.INSTANCE.buffer("set spring.config.import={}", environment.getProperty(SPRING_CONFIG_IMPORT));
        PrettyPrinter.INSTANCE.flush();
    }


    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener").flush();
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener").flush();
    }

    @Override
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener");
        PrettyPrinter.INSTANCE.buffer("duration:" + timeTaken.getSeconds());
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener");
        PrettyPrinter.INSTANCE.buffer("duration:" + timeTaken.getSeconds());
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        PrettyPrinter.INSTANCE.buffer("Overrided from SpringApplicationRunListener").flush();
        exception.printStackTrace();
    }

    @Override
    public void run(ApplicationArguments args) {
        PrettyPrinter.INSTANCE.buffer("Overrided from ApplicationRunner").flush();
    }

    @Override
    public void run(String... args) {
        PrettyPrinter.INSTANCE.buffer("Overrided from CommandLineRunner")
                .buffer("args:" + Arrays.asList(args)).flush();
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        PrettyPrinter.INSTANCE.buffer("Overrided from EnvironmentPostProcessor");
        // 这里可以最早的获取到初始完成的environment,包括Profiles
        // 设置到全局上下文
        PrettyPrinter.INSTANCE.buffer("put environment into EnvironmentContext");
        EnvironmentContext.INSTANCE.setEnvironment(environment);

        // 覆盖DefaultProperties
        Properties defaultProperties = new Properties();
        populateDefaultProperties(defaultProperties);
        // logger
        handleLogger(environment, defaultProperties);
        // 因加载顺序的原因,此时使用springApplication.setDefaultProperties()无效
        PrettyPrinter.INSTANCE.buffer("set defaultProperties");
        environment.getPropertySources().addLast(new PropertiesPropertySource("defaultProperties", defaultProperties));
        PrettyPrinter.INSTANCE.flush();
    }


    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        PrettyPrinter.INSTANCE.buffer("Overrided from ApplicationContextInitializer");
        // 初始化BeanLoader
        BeanLoader.INSTANCE.setApplicationContext(applicationContext);
        PrettyPrinter.INSTANCE.buffer("Initialized BeanLoader");

        // 注册一个优先级非常高的BeanFactoryPostProcessor
        applicationContext.getBeanFactory().registerSingleton("highestPriorityBeanDefinitionRegistryPostProcessor", new HighestPriorityBeanDefinitionRegistryPostProcessor());
        PrettyPrinter.INSTANCE.buffer("register HighestPriorityBeanDefinitionRegistryPostProcessor");
        // sacan package
        hanldeScanPackages(applicationContext);
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
                System.out.println(STARTED);
            }
        }
    }

    @Override
    public void start() {
        this.isRunning = true;
        PrettyPrinter.INSTANCE.buffer("Overrided from Lifecycle");
        PrettyPrinter.INSTANCE.buffer("set running=true");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void stop() {
        this.isRunning = false;
        PrettyPrinter.INSTANCE.buffer("Overrided from Lifecycle");
        PrettyPrinter.INSTANCE.buffer("set running=false");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public boolean isRunning() {
        PrettyPrinter.INSTANCE.buffer("Overrided from Lifecycle").flush();
        return this.isRunning;
    }

    @Override
    public int getPhase() {
        return Integer.MIN_VALUE;
    }


    @Override
    public void destroy() {
        PrettyPrinter.INSTANCE.buffer("Overrided from DisposableBean").flush();
    }

    @Override
    public void afterPropertiesSet() {
        PrettyPrinter.INSTANCE.buffer("Overrided from InitializingBean").flush();
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        PrettyPrinter.INSTANCE.buffer("Overrided from BeanClassLoaderAware").flush();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        PrettyPrinter.INSTANCE.buffer("Overrided from BeanFactoryAware").flush();
    }

    @Override
    public void setBeanName(String beanName) {
        PrettyPrinter.INSTANCE.buffer("Overrided from BeanNameAware");
        PrettyPrinter.INSTANCE.buffer("BeanName is:{}", beanName);
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        PrettyPrinter.INSTANCE.buffer("Overrided from ApplicationContextAware").flush();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        PrettyPrinter.INSTANCE.buffer("Overrided from ApplicationEventPublisherAware").flush();
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        PrettyPrinter.INSTANCE.buffer("Overrided from EmbeddedValueResolverAware").flush();
    }

    @Override
    public void setEnvironment(Environment environment) {
        PrettyPrinter.INSTANCE.buffer("Overrided from EnvironmentAware").flush();
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        MessageSourceLoader.INSTANCE.initMessageSource(messageSource);
        PrettyPrinter.INSTANCE.buffer("Overrided from MessageSourceAware").flush();
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        PrettyPrinter.INSTANCE.buffer("Overrided from ResourceLoaderAware").flush();
    }

    @PostConstruct
    public void postConstruct() {
        PrettyPrinter.INSTANCE.buffer("@PostConstruct").flush();
    }

    @PreDestroy
    public void preDestroy() {
        PrettyPrinter.INSTANCE.buffer("@PreDestroy").flush();
    }


    private void handleLogger(ConfigurableEnvironment environment, Properties defaultProperties) {
        final String LOGGING_CONFIG = "logging.config";
        final String CONFIG_FILE = "config.file";
        /*
        使用外置Tomcat时,Tomcat会设置环境变量logging.config
        在org.springframework.boot.logging.LoggingApplicationListener#initializeSystem中
        通过String logConfig = environment.getProperty(CONFIG_PROPERTY);在 systemProperties 的 PropertySource 中拿到了日志的配置
        下面要覆盖这个配置
        */
        String configFile = environment.getProperty(CONFIG_FILE);
        if (StringUtil.INSTANCE.isBlank(configFile)) {
            PrettyPrinter.INSTANCE.flush();
            throw new MatrixWarnException("'config.file' does not exist.");
        }
        configFile = EnvironmentContext.INSTANCE.getURI(configFile).toString();
        System.setProperty(LOGGING_CONFIG, configFile);
        defaultProperties.setProperty(LOGGING_CONFIG, configFile);
        PrettyPrinter.INSTANCE.buffer("set {} is:{}", LOGGING_CONFIG, configFile);
    }

    private void populateDefaultProperties(Properties defaultProperties) {
        // defaultProperties.setProperty("spring.jpa.properties.javax.persistence.validation.mode", "none");
        // defaultProperties.setProperty("spring.data.jpa.repositories.enabled", "false");
        // defaultProperties.setProperty(" spring.data.jpa.repositories.bootstrap-mode", "default");

        // 设置排除的AutoConfiguration
        for (int i = 0; i < excludeAutoConfigure.length; i++) {
            defaultProperties.setProperty(StringUtil.INSTANCE.format("spring.autoconfigure.exclude[{}]", i), excludeAutoConfigure[i]);
        }
        defaultProperties.setProperty("spring.jackson.mapper.propagate-transient-marker", "true");
    }

    private void hanldeScanPackages(ConfigurableApplicationContext applicationContext) {
        // 处理自动扫描和排除扫描项目
        BeanDefinitionRegistry beanRegistry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanRegistry);
        scanner.setResourceLoader(applicationContext);
        // 获取要排除扫描的包
        if (CollectionUtil.INSTANCE.isNotEmpty(excludeScanPackages)) {
            scanner.addExcludeFilter((metadataReader, metadataReaderFactory) -> {
                String className = metadataReader.getClassMetadata().getClassName();
                for (String excludeScanPackage : excludeScanPackages) {
                    return className.startsWith(excludeScanPackage);
                }
                return false;
            });
        }
        // 获取扫描配置
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String autoScanPackages = environment.getProperty("packages");
        autoScanPackages = StringUtil.INSTANCE.isBlank(autoScanPackages) ? DEFAULT_SCAN_PACKAGES
                : DEFAULT_SCAN_PACKAGES.concat(Symbol.COMMA.getSymbol()).concat(autoScanPackages);
        String[] autoScanArray = autoScanPackages.split(Symbol.COMMA.getSymbol());
        PrettyPrinter.INSTANCE.buffer("scan packages: {}", autoScanPackages);
        scanner.scan(autoScanArray);
    }

    @Override
    public int getOrder() {
        // 排在它后面,用于获取到Environment,比如profile
        return EnvironmentPostProcessorApplicationListener.DEFAULT_ORDER + 1;
    }
}
