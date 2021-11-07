package wang.liangchen.matrix.framework.springboot.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import wang.liangchen.matrix.framework.commons.utils.PrettyPrinter;

/**
 * 该类的方法会特别早的执行
 * Bean动态注册
 * Spring通过BeanFactory管理bean，具体的实现是DefaultListableBeanFactory
 * 在这个类当中有一个注入bean的方法：registerBeanDefinition，使用BeanDefinitionBuilder可以构建一个BeanDefinition
 * 只要获取到ApplicationContext对象即可获取到BeanFacory
 *
 * @author LiangChen.Wang 2020/9/15
 */
public class HighestPriorityBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered, EnvironmentAware, BeanClassLoaderAware {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        PrettyPrinter.INSTANCE.buffer("BeanDefinitionRegistryPostProcessor");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        PrettyPrinter.INSTANCE.buffer("BeanDefinitionRegistryPostProcessor");
        PrettyPrinter.INSTANCE.flush();
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {

    }

    @Override
    public void setEnvironment(Environment environment) {

    }
}
