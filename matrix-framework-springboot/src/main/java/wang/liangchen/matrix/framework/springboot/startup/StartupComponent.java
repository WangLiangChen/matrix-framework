package wang.liangchen.matrix.framework.springboot.startup;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StartupComponent implements
        BeanFactoryPostProcessor,
        BeanDefinitionRegistryPostProcessor,
        BeanPostProcessor,
//        SmartLifecycle,
//        ApplicationRunner,
//        CommandLineRunner,
//        InitializingBean,
//        DisposableBean,
//        BeanNameAware,
//        BeanFactoryAware,
//        ApplicationContextAware,
//        BeanClassLoaderAware,
//        EnvironmentAware,
//        ResourceLoaderAware,
//        EmbeddedValueResolverAware,
//        ApplicationEventPublisherAware,
//        MessageSourceAware,
        Ordered {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // BeanFactoryPostProcessor
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // BeanDefinitionRegistryPostProcessor
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // BeanPostProcessor
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //BeanPostProcessor
        return bean;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
