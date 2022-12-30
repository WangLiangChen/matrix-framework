package wang.liangchen.matrix.framework.springboot.env;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
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
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;
import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.utils.PrettyPrinter;
import wang.liangchen.matrix.framework.springboot.context.BeanLoader;
import wang.liangchen.matrix.framework.springboot.context.MessageSourceLoader;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author LiangChen.Wang 2021/7/2
 */
@SuppressWarnings("NullableProblems")
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@EnableAsync
@EnableScheduling
public class StartProcessMonitor implements
        EnvironmentPostProcessor,
        BeanPostProcessor,
        BeanFactoryPostProcessor,
        BeanDefinitionRegistryPostProcessor,
        ApplicationContextInitializer<ConfigurableApplicationContext>,
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
    private final static String STARTED = Symbol.LINE_SEPARATOR.getSymbol() + "------------------------------------> Matrix Framework is Started <------------------------------------" + Symbol.LINE_SEPARATOR.getSymbol();
    private final static String TASKSCHEDULER_THREAD_PREFIX = "taskScheduler-";
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
            "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
    };
    private boolean isRunning;

    @Override
    public void run(ApplicationArguments args) {
        PrettyPrinter.INSTANCE.buffer("Overrided from ApplicationRunner").flush();
    }

    @Override
    public void run(String... args) {
        PrettyPrinter.INSTANCE.buffer("Overrided from CommandLineRunner").buffer("args:" + Arrays.asList(args)).flush();
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
        // handleLogger(environment, defaultProperties);
        // 因加载顺序的原因,此时使用springApplication.setDefaultProperties()无效
        PrettyPrinter.INSTANCE.buffer("set defaultProperties");
        environment.getPropertySources().addLast(new PropertiesPropertySource("defaultProperties", defaultProperties));
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ThreadPoolTaskScheduler) {
            ((ThreadPoolTaskScheduler) bean).setThreadNamePrefix(TASKSCHEDULER_THREAD_PREFIX);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        PrettyPrinter.INSTANCE.buffer("Overrided from BeanFactoryPostProcessor");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        PrettyPrinter.INSTANCE.buffer("Overrided from BeanDefinitionRegistryPostProcessor");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // 可以使用下面的语句判断是否springcloud
        // boolean containsBootstrap = applicationContext.getEnvironment().getPropertySources().contains("bootstrap");

        PrettyPrinter.INSTANCE.buffer("Overrided from ApplicationContextInitializer");
        // 初始化BeanLoader
        BeanLoader.INSTANCE.setApplicationContext(applicationContext);
        PrettyPrinter.INSTANCE.buffer("Initialized BeanLoader");
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
            throw new MatrixWarnException("'config.file' does not exist. Matrix Framework properties may not be loaded");
        }

        configFile = EnvironmentContext.INSTANCE.getURL(configFile).toString();
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
        // 扫描框架包
        BeanDefinitionRegistry beanRegistry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanRegistry);
        scanner.setResourceLoader(applicationContext);
        scanner.scan(DEFAULT_SCAN_PACKAGES);
    }

    @Override
    public int getOrder() {
        // 排在它后面,用于获取到Environment,比如profile
        return EnvironmentPostProcessorApplicationListener.DEFAULT_ORDER + 1;
    }
}
