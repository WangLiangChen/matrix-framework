package wang.liangchen.matrix.framework.spring.boot.startup;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.thread.Threading;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import wang.liangchen.matrix.framework.commons.json.jackson.JacksonUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.spring.boot.aop.ProxyObjectAware;
import wang.liangchen.matrix.framework.spring.boot.reflect.DelegatingConcurrentMethodInterceptor;

import java.util.Map;
import java.util.concurrent.Executor;

import static wang.liangchen.matrix.framework.spring.boot.startup.StartupStopWatch.watchTask;

public class StartupBean implements
        ApplicationContextAware,
        EnvironmentAware,
        ApplicationRunner,
        CommandLineRunner,
        BeanFactoryPostProcessor,
        BeanDefinitionRegistryPostProcessor,
        BeanPostProcessor,
        InstantiationAwareBeanPostProcessor,
        SmartLifecycle,
        InitializingBean,
        DisposableBean,
        SmartInitializingSingleton {
    private final static Logger logger = LoggerFactory.getLogger(StartupBean.class);
    private final static String SCHEDULINGCONFIGURER_BEANNAME = "matrixSchedulingConfigurer";
    private final static String ASYNC_THREAD_PREFIX = "matrix-async-";
    private final static String SCHEDULED_THREAD_PREFIX = "matrix-scheduled-";
    private final static String DEFAULT_EXECUTOR = "taskExecutor";
    private final static String DEFAULT_SCHEDULER = "taskScheduler";

    private boolean running;
    private ApplicationContext applicationContext;
    private Environment environment;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // InitializingBean
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // BeanDefinitionRegistryPostProcessor
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // BeanFactoryPostProcessor
        registerAsyncConfigurer(beanFactory);
        registerSchedulingConfigurer(beanFactory);
        // ConditionEvaluationReport conditionEvaluationReport = ConditionEvaluationReport.get(beanFactory);
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        // InstantiationAwareBeanPostProcessor
        return InstantiationAwareBeanPostProcessor.super.postProcessBeforeInstantiation(beanClass, beanName);
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        // InstantiationAwareBeanPostProcessor
        return InstantiationAwareBeanPostProcessor.super.postProcessAfterInstantiation(bean, beanName);
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        // InstantiationAwareBeanPostProcessor
        return InstantiationAwareBeanPostProcessor.super.postProcessProperties(pvs, bean, beanName);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // BeanPostProcessor
        // inject proxy object
        if (bean instanceof ProxyObjectAware proxyObjectAware) {
            proxyObjectAware.setProxyObject(applicationContext.getBean(beanName));
        }
        if (bean instanceof Validator validator) {
            ValidationUtil.INSTANCE.resetValidator(validator);
            watchTask.addMessage("Initialize Validator, Set Validator to ValidationUtil");
        }
        if (bean instanceof ObjectMapper objectMapper) {
            JacksonUtil.INSTANCE.resetObjectMapper(objectMapper);
            watchTask.addMessage("Initialize ObjectMapper, Set ObjectMapper to JacksonUtil");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // BeanPostProcessor
        return bean;
    }

    @Override
    public void afterSingletonsInstantiated() {
        // SmartInitializingSingleton
    }

    @Override
    public boolean isAutoStartup() {
        return SmartLifecycle.super.isAutoStartup();
    }

    @Override
    public int getPhase() {
        return SmartLifecycle.super.getPhase();
    }


    @Override
    public boolean isRunning() {
        // SmartLifecycle
        return this.running;
    }

    @Override
    public void start() {
        // SmartLifecycle
        this.running = true;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // ApplicationRunner
    }

    @Override
    public void run(String... args) throws Exception {
        // CommandLineRunner
    }

    @Override
    public void stop(Runnable callback) {
        SmartLifecycle.super.stop(callback);
    }

    @Override
    public void stop() {
        // SmartLifecycle
        this.running = false;
    }

    @Override
    public void destroy() throws Exception {
        // DisposableBean
    }


    /**
     * 使用 EnableAsync后，避免AsyncConfigurer出现 not eligible for getting processed by all BeanPostProcessors
     * 使用registerSingleton注册的Bean，立即进入单例缓存（singletonObjects），在BeanFactory的BeanDefinition集合中是不存在的，但通过getBean可以获取
     * ThreadPoolTaskExecutor 在TaskExecutionAutoConfiguration中注册 @Async
     * ThreadPoolTaskScheduler 在TaskSchedulingAutoConfiguration中注册 @Scheduled
     * 注意：在这里getBean会导致提前初始化
     *
     * @param beanFactory
     */
    private void registerAsyncConfigurer(ConfigurableListableBeanFactory beanFactory) {
        if (Threading.VIRTUAL.isActive(this.environment)) {
            return;
        }
        Map<String, AsyncConfigurer> asyncConfigurers = beanFactory.getBeansOfType(AsyncConfigurer.class);
        asyncConfigurers.forEach((beanName, asyncConfigurer) -> {
            Executor defaultExecutor = asyncConfigurer.getAsyncExecutor();

            if (beanFactory instanceof BeanDefinitionRegistry beanDefinitionRegistry) {
                beanDefinitionRegistry.removeBeanDefinition(beanName);
            }
            beanFactory.destroyBean(beanName);

            if (defaultExecutor instanceof ThreadPoolTaskExecutor threadPoolTaskExecutor) {
                threadPoolTaskExecutor.setThreadNamePrefix(ASYNC_THREAD_PREFIX);
                if (threadPoolTaskExecutor.getMaxPoolSize() == Integer.MAX_VALUE) {
                    threadPoolTaskExecutor.setMaxPoolSize(threadPoolTaskExecutor.getCorePoolSize() * 5);
                    threadPoolTaskExecutor.setQueueCapacity(threadPoolTaskExecutor.getCorePoolSize() * 100);
                    threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
                    threadPoolTaskExecutor.setAwaitTerminationSeconds(60);
                    threadPoolTaskExecutor.setThreadGroupName(DEFAULT_EXECUTOR);
                }
            }
            Executor delegatedExecutor = DelegatingConcurrentMethodInterceptor.createProxy(defaultExecutor);
            asyncConfigurer = new AsyncConfigurer() {
                @Override
                public Executor getAsyncExecutor() {
                    return delegatedExecutor;
                }
            };
            beanFactory.registerSingleton(beanName, asyncConfigurer);
        });
    }

    private void registerSchedulingConfigurer(ConfigurableListableBeanFactory beanFactory) {
        if (Threading.VIRTUAL.isActive(this.environment)) {
            return;
        }
        if (!beanFactory.containsBean(DEFAULT_SCHEDULER)) {
            return;
        }

        SchedulingConfigurer schedulingConfigurer = taskRegistrar -> {
            ThreadPoolTaskScheduler defaultScheduler = beanFactory.getBean(DEFAULT_SCHEDULER, ThreadPoolTaskScheduler.class);
            int corePoolSize = defaultScheduler.getScheduledThreadPoolExecutor().getCorePoolSize();
            if (corePoolSize == 1) {
                defaultScheduler.setPoolSize(8);
            }
            defaultScheduler.setThreadNamePrefix(SCHEDULED_THREAD_PREFIX);
            defaultScheduler.setWaitForTasksToCompleteOnShutdown(true);
            defaultScheduler.setAwaitTerminationSeconds(60);
            defaultScheduler.setThreadGroupName(DEFAULT_SCHEDULER);
            taskRegistrar.setTaskScheduler(defaultScheduler);
        };
        beanFactory.registerSingleton(SCHEDULINGCONFIGURER_BEANNAME, schedulingConfigurer);
    }
}
