package wang.liangchen.matrix.framework.springboot.processor;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.MethodMetadata;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import wang.liangchen.matrix.framework.springboot.annotation.OverrideBean;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 用注解的方式实现Bean被覆盖的效果
 *
 * @author LiangChen.Wang 2020/9/15
 */
@Component
public class OverrideBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(OverrideBeanDefinitionRegistryPostProcessor.class);

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        String[] beanDefinitionNames = registry.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanDefinitionName);
            if (!(beanDefinition instanceof AnnotatedBeanDefinition)) {
                continue;
            }
            Object source = beanDefinition.getSource();
            if (!(source instanceof MethodMetadata)) {
                continue;
            }
            MethodMetadata methodMetadata = (MethodMetadata) source;
            if (!methodMetadata.isAnnotated(OverrideBean.class.getName())) {
                continue;
            }
            MergedAnnotation<OverrideBean> overrideBeanNameMergedAnnotation = methodMetadata.getAnnotations().get(OverrideBean.class);
            String value = overrideBeanNameMergedAnnotation.getString("value");
            if (!registry.containsBeanDefinition(value)) {
                continue;
            }
            registry.removeBeanDefinition(beanDefinitionName);
            registry.removeBeanDefinition(value);
            registry.registerBeanDefinition(value, beanDefinition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        /*
        使用 EnableAsync后，避免AsyncConfigurer出现 not eligible for getting processed by all BeanPostProcessors
        用这种方式注册的Bean，在BeanFactory的BeanDefinition集合中是不存在的，但通过getBean可以获取对象
         */
        // beanFactory.registerSingleton("asyncConfigurer", asyncConfigurer());
        // beanFactory.registerSingleton("schedulingConfigurer", schedulingConfigurer());
    }

    /*
    使用TransmittableThreadLocal,用Ttl**包装一下
    TtlRunnable和TtlCallable来包装Runnable和Callable。
    getTtlExecutor包装Executor
    getTtlExecutorService包装ExecutorService
    getTtlScheduledExecutorService包装ScheduledExecutorService
     */
    private AsyncConfigurer asyncConfigurer() {
        return new AsyncConfigurer() {
            @Override
            public Executor getAsyncExecutor() {
                ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
                executor.initialize();
                int processors = Runtime.getRuntime().availableProcessors();
                executor.setCorePoolSize(processors + 2);
                executor.setMaxPoolSize(processors * 16);
                executor.setQueueCapacity(processors * 16);
                executor.setThreadNamePrefix("async_");
                executor.setWaitForTasksToCompleteOnShutdown(true);
                executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

                //jvm退出时关闭task，解决使用tomcat的shutdown后进程依然存在的问题
                Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));
                return TtlExecutors.getTtlExecutor(executor);
            }

            @Override
            public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
                return (ex, method, params) -> logger.error("异步线程执行异常", ex);
            }
        };
    }

    private SchedulingConfigurer schedulingConfigurer() {
        return taskRegistrar -> {
            ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
            int processors = Runtime.getRuntime().availableProcessors();
            taskScheduler.setPoolSize(processors * 2);
            taskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            taskScheduler.setThreadNamePrefix("taskScheduler-");
            taskRegistrar.setTaskScheduler(taskScheduler);
        };
    }
}
