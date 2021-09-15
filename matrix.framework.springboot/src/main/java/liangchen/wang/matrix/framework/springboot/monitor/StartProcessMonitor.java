package liangchen.wang.matrix.framework.springboot.monitor;

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
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

/**
 * @author LiangChen.Wang 2021/7/2
 */
@Component
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
    private final String prefix = "------ StartProcessMonitor,interface:%s,method:%s";
    private boolean isRunning;

    public StartProcessMonitor(SpringApplication application, String[] args) {
        System.out.println(String.format(prefix, "SpringApplicationRunListener", "constructor"));
    }

    public StartProcessMonitor() {
    }

    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        System.out.println(String.format(prefix, "SpringApplicationRunListener", "starting"));
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        System.out.println(String.format(prefix, "SpringApplicationRunListener", "environmentPrepared"));
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        System.out.println(String.format(prefix, "SpringApplicationRunListener", "contextPrepared"));
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        System.out.println(String.format(prefix, "SpringApplicationRunListener", "contextLoaded"));
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        System.out.println(String.format(prefix, "SpringApplicationRunListener", "started"));
    }

    @Override
    public void running(ConfigurableApplicationContext context) {
        System.out.println(String.format(prefix, "SpringApplicationRunListener", "running"));
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        System.out.println(String.format(prefix, "SpringApplicationRunListener", "failed"));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(String.format(prefix, "ApplicationRunner", "run"));
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(String.format(prefix, "CommandLineRunner", "run"));
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        System.out.println(String.format(prefix, "EnvironmentPostProcessor", "postProcessEnvironment"));
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        System.out.println(String.format(prefix, "ApplicationContextInitializer", "initialize"));
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println(String.format(prefix, "ApplicationListener", "onApplicationEvent"));
    }

    @Override
    public void start() {
        this.isRunning = true;
        System.out.println(String.format(prefix, "Lifecycle", "start"));
    }

    @Override
    public void stop() {
        this.isRunning = false;
        System.out.println(String.format(prefix, "Lifecycle", "stop"));
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        System.out.println(String.format(prefix, "BeanDefinitionRegistryPostProcessor", "postProcessBeanDefinitionRegistry"));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        System.out.println(String.format(prefix, "BeanFactoryPostProcessor", "postProcessBeanFactory"));
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        System.out.println(String.format(prefix, "InstantiationAwareBeanPostProcessor", "postProcessBeforeInstantiation"));
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        System.out.println(String.format(prefix, "InstantiationAwareBeanPostProcessor", "postProcessAfterInstantiation"));
        return true;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        System.out.println(String.format(prefix, "InstantiationAwareBeanPostProcessor", "postProcessProperties"));
        return pvs;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println(String.format(prefix, "BeanPostProcessor", "postProcessBeforeInitialization"));
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println(String.format(prefix, "BeanPostProcessor", "postProcessAfterInitialization"));
        return bean;
    }

    @Override
    public void destroy() throws Exception {
        System.out.println(String.format(prefix, "DisposableBean", "destroy"));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(String.format(prefix, "InitializingBean", "afterPropertiesSet"));
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        System.out.println(String.format(prefix, "BeanClassLoaderAware", "setBeanClassLoader"));
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println(String.format(prefix, "BeanFactoryAware", "setBeanFactory"));
    }

    @Override
    public void setBeanName(String s) {
        System.out.println(String.format(prefix, "BeanNameAware", "setBeanName"));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println(String.format(prefix, "ApplicationContextAware", "setApplicationContext"));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        System.out.println(String.format(prefix, "ApplicationEventPublisherAware", "setApplicationEventPublisher"));
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        System.out.println(String.format(prefix, "EmbeddedValueResolverAware", "setEmbeddedValueResolver"));
    }

    @Override
    public void setEnvironment(Environment environment) {
        System.out.println(String.format(prefix, "EnvironmentAware", "setEnvironment"));
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        System.out.println(String.format(prefix, "MessageSourceAware", "setMessageSource"));
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        System.out.println(String.format(prefix, "ResourceLoaderAware", "setResourceLoader"));
    }
}
