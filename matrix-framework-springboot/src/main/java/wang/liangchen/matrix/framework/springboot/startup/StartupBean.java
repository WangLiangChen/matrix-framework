package wang.liangchen.matrix.framework.springboot.startup;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StartupBean implements
        ApplicationRunner,
        CommandLineRunner,
        BeanFactoryPostProcessor,
        BeanDefinitionRegistryPostProcessor,
        BeanPostProcessor,
        SmartLifecycle,
        InitializingBean,
        DisposableBean,
        Ordered {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // ApplicationRunner
        System.out.println("--- StartupBean: ApplicationRunner");
    }

    @Override
    public void run(String... args) throws Exception {
        // CommandLineRunner
        System.out.println("--- StartupBean: CommandLineRunner");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // BeanFactoryPostProcessor
        System.out.println("--- StartupBean: postProcessBeanFactory");
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // BeanDefinitionRegistryPostProcessor
        System.out.println("--- StartupBean: postProcessBeanDefinitionRegistry");
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // BeanPostProcessor
        System.out.println("--- StartupBean: postProcessBeforeInitialization(" + beanName + ")");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //BeanPostProcessor
        System.out.println("--- StartupBean: postProcessAfterInitialization(" + beanName + ")");
        return bean;
    }

    @Override
    public void start() {
        System.out.println("--- StartupBean: start");
        // SmartLifecycle
    }

    @Override
    public void stop() {
        System.out.println("--- StartupBean: stop");
        // SmartLifecycle
    }

    @Override
    public boolean isRunning() {
        System.out.println("--- StartupBean: isRunning");
        // SmartLifecycle
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("--- StartupBean: afterPropertiesSet");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("--- StartupBean: destroy");
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
