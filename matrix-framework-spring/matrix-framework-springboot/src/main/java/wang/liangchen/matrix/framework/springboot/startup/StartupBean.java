package wang.liangchen.matrix.framework.springboot.startup;


import com.alibaba.ttl.threadpool.TtlExecutors;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.springboot.aop.ProxyAware;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StartupBean implements
        ApplicationContextAware,
        ApplicationRunner,
        CommandLineRunner,
        BeanFactoryPostProcessor,
        BeanDefinitionRegistryPostProcessor,
        BeanPostProcessor,
        InstantiationAwareBeanPostProcessor,
        SmartLifecycle,
        InitializingBean,
        DisposableBean,
        Ordered {
    private final static Logger logger = LoggerFactory.getLogger(StartupBean.class);
    private boolean running;
    private ApplicationContext applicationContext;
    private final static String ASYNCCONFIGURER_BEANNAME = "asyncConfigurer";
    private final static String ASYNC_THREAD_PREFIX = "async-";

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // InitializingBean
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
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // BeanDefinitionRegistryPostProcessor
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // BeanFactoryPostProcessor
        registerAsyncConfigurer(beanFactory);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // BeanPostProcessor
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // BeanPostProcessor
        if (bean instanceof ProxyAware) {
            proxyAware((ProxyAware) bean, beanName);
        }
        if (bean instanceof Validator) {
            ValidationUtil.INSTANCE.resetValidator((Validator) bean);
            StartupApplicationListener.startupTask.addMessage("Set validator to Validation");
            StartupApplicationListener.startupTask.prettyPrint();
        }
        return bean;
    }

    private void proxyAware(ProxyAware proxy, String beanName) {
        // 如果当前对象已经是代理对象，直接注入
        if (AopUtils.isAopProxy(proxy)) {
            proxy.setProxy(proxy);
            return;
        }
        // 如果不是代理对象，则获取代理对象后注入
        proxy.setProxy(applicationContext.getBean(beanName));
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

    @Override
    public boolean isRunning() {
        // SmartLifecycle
        return this.running;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 使用 EnableAsync后，避免AsyncConfigurer出现 not eligible for getting processed by all BeanPostProcessors
     * 用这种方式注册的Bean，在BeanFactory的BeanDefinition集合中是不存在的，但通过getBean可以获取对象
     * ThreadPoolTaskExecutor 在TaskExecutionAutoConfiguration中注册
     * ThreadPoolTaskScheduler 在TaskSchedulingAutoConfiguration中注册
     * 注意：在这里getBean会导致提前初始化
     *
     * @param beanFactory
     */
    private void registerAsyncConfigurer(ConfigurableListableBeanFactory beanFactory) {
        ThreadPoolTaskExecutor defaultExecutor = null;
        if (beanFactory.containsBean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)) {
            defaultExecutor = beanFactory.getBean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME, ThreadPoolTaskExecutor.class);
        }
        beanFactory.registerSingleton(ASYNCCONFIGURER_BEANNAME, asyncConfigurer(defaultExecutor));
    }

    /**
     * 使用TransmittableThreadLocal,用Ttl**包装一下
     * TtlRunnable和TtlCallable来包装Runnable和Callable。
     * getTtlExecutor包装Executor
     * getTtlExecutorService包装ExecutorService
     * getTtlScheduledExecutorService包装ScheduledExecutorService
     *
     * @param defaultExecutor
     * @return AsyncConfigurer
     */
    private AsyncConfigurer asyncConfigurer(ThreadPoolTaskExecutor defaultExecutor) {
        return new AsyncConfigurer() {
            @Override
            public Executor getAsyncExecutor() {
                if (null != defaultExecutor) {
                    defaultExecutor.setThreadNamePrefix(ASYNC_THREAD_PREFIX);
                    return TtlExecutors.getTtlExecutor(defaultExecutor);
                }
                ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
                int processors = Runtime.getRuntime().availableProcessors();
                executor.setCorePoolSize(processors + 2);
                executor.setMaxPoolSize(processors * 16);
                executor.setQueueCapacity(processors * 16);
                executor.setThreadNamePrefix(ASYNC_THREAD_PREFIX);
                executor.setWaitForTasksToCompleteOnShutdown(true);
                executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
                executor.initialize();
                //jvm退出时关闭task，解决使用tomcat的shutdown后进程依然存在的问题
                Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));
                return TtlExecutors.getTtlExecutor(executor);
            }

            @Override
            public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
                return (ex, method, params) -> logger.error("Async thread run exception", ex);
            }
        };
    }
}
