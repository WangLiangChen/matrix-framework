package liangchen.wang.matrix.framework.springboot.processor;

import liangchen.wang.gradf.framework.commons.utils.Printer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;

/**
 * 该类会特别早的执行
 * Bean动态注册
 * Spring管理bean的对象是BeanFactory，具体的是DefaultListableBeanFactory
 * 在这个类当中有一个注入bean的方法：registerBeanDefinition
 * 在调用registerBeanDefinition方法时，需要BeanDefinition参数
 * Spring提供了BeanDefinitionBuilder可以构建一个BeanDefinition
 * 只要获取到ApplicationContext对象即可获取到BeanFacory
 *
 * @author LiangChen.Wang 2020/9/15
 */
public class FirstBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered, EnvironmentAware, BeanClassLoaderAware {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Printer.INSTANCE.prettyPrint("postProcessBeanDefinitionRegistry");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Printer.INSTANCE.prettyPrint("postProcessBeanFactory");
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
