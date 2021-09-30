package liangchen.wang.matrix.framework.springboot.monitor;

import liangchen.wang.matrix.framework.commons.utils.PrettyPrinter;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.*;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

/**
 * @author LiangChen.Wang 2021/7/2
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StartProcessMonitor implements EnvironmentPostProcessor,
        ApplicationContextInitializer<ConfigurableApplicationContext>,
        SpringApplicationRunListener,
        ApplicationListener<ApplicationEvent>,
        Lifecycle,
        ApplicationRunner,
        CommandLineRunner,
        BeanDefinitionRegistryPostProcessor,
        InstantiationAwareBeanPostProcessor,
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
    private boolean isRunning;

    public StartProcessMonitor(SpringApplication springApplication, String[] args) {
        PrettyPrinter.INSTANCE.buffer("SpringApplicationRunListener");
        springApplication.setBannerMode(Banner.Mode.OFF);
        PrettyPrinter.INSTANCE.buffer("set BannerMode=OFF");
        PrettyPrinter.INSTANCE.flush();
    }

    public StartProcessMonitor() {
    }

    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        PrettyPrinter.INSTANCE.buffer("SpringApplicationRunListener");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        PrettyPrinter.INSTANCE.buffer("SpringApplicationRunListener");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        PrettyPrinter.INSTANCE.buffer("SpringApplicationRunListener");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        PrettyPrinter.INSTANCE.buffer("SpringApplicationRunListener");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        PrettyPrinter.INSTANCE.buffer("SpringApplicationRunListener");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void running(ConfigurableApplicationContext context) {
        PrettyPrinter.INSTANCE.buffer("SpringApplicationRunListener");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        PrettyPrinter.INSTANCE.buffer("SpringApplicationRunListener");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        PrettyPrinter.INSTANCE.buffer("ApplicationRunner");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void run(String... args) throws Exception {
        PrettyPrinter.INSTANCE.buffer("CommandLineRunner");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        PrettyPrinter.INSTANCE.buffer("EnvironmentPostProcessor");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        PrettyPrinter.INSTANCE.buffer("ApplicationContextInitializer");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        PrettyPrinter.INSTANCE.buffer(String.format("%s#%s", "ApplicationListener", event.getClass().getSimpleName()));
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void start() {
        PrettyPrinter.INSTANCE.buffer("Lifecycle");
        PrettyPrinter.INSTANCE.flush();
        this.isRunning = true;
    }

    @Override
    public void stop() {
        PrettyPrinter.INSTANCE.buffer("Lifecycle");
        PrettyPrinter.INSTANCE.flush();
        this.isRunning = false;
    }

    @Override
    public boolean isRunning() {
        PrettyPrinter.INSTANCE.buffer("Lifecycle");
        PrettyPrinter.INSTANCE.flush();
        return this.isRunning;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        PrettyPrinter.INSTANCE.buffer("BeanDefinitionRegistryPostProcessor");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        PrettyPrinter.INSTANCE.buffer("BeanFactoryPostProcessor");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        PrettyPrinter.INSTANCE.buffer("InstantiationAwareBeanPostProcessor");
        PrettyPrinter.INSTANCE.flush();
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        PrettyPrinter.INSTANCE.buffer("InstantiationAwareBeanPostProcessor");
        PrettyPrinter.INSTANCE.flush();
        return true;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        PrettyPrinter.INSTANCE.buffer("InstantiationAwareBeanPostProcessor");
        PrettyPrinter.INSTANCE.flush();
        return pvs;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        PrettyPrinter.INSTANCE.buffer("BeanPostProcessor");
        PrettyPrinter.INSTANCE.flush();
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        PrettyPrinter.INSTANCE.buffer("BeanPostProcessor");
        PrettyPrinter.INSTANCE.flush();
        return bean;
    }

    @Override
    public void destroy() throws Exception {
        PrettyPrinter.INSTANCE.buffer("DisposableBean");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PrettyPrinter.INSTANCE.buffer("InitializingBean");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        PrettyPrinter.INSTANCE.buffer("BeanClassLoaderAware");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        PrettyPrinter.INSTANCE.buffer("BeanFactoryAware");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void setBeanName(String s) {
        PrettyPrinter.INSTANCE.buffer("BeanNameAware");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        PrettyPrinter.INSTANCE.buffer("ApplicationContextAware");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        PrettyPrinter.INSTANCE.buffer("ApplicationEventPublisherAware");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        PrettyPrinter.INSTANCE.buffer("EmbeddedValueResolverAware");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void setEnvironment(Environment environment) {
        PrettyPrinter.INSTANCE.buffer("EnvironmentAware");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        PrettyPrinter.INSTANCE.buffer("MessageSourceAware");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        PrettyPrinter.INSTANCE.buffer("ResourceLoaderAware");
        PrettyPrinter.INSTANCE.flush();
    }
}
