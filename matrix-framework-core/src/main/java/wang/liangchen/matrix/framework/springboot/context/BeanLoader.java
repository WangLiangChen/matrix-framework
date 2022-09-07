package wang.liangchen.matrix.framework.springboot.context;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;

import java.util.Map;

/**
 * @author LiangChen.Wang
 * 在spring容器启动早期即完成初始化
 */
public enum BeanLoader {
    /**
     * instance;
     */
    INSTANCE;
    private ApplicationContext applicationContext;

    public void setApplicationContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    public <T> T getBean(String name) {
        Object bean = this.applicationContext.getBean(name);
        return ObjectUtil.INSTANCE.cast(bean);
    }

    public <T> T getBean(Class<T> clazz) {
        return this.applicationContext.getBean(clazz);
    }

    public <T> T getBean(String name, Class<T> clazz) {
        return this.applicationContext.getBean(name, clazz);
    }

    public <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return this.applicationContext.getBeansOfType(clazz);
    }

    public Class<?> getType(String name) {
        return this.applicationContext.getType(name);
    }

    public String[] getBeanDefinitionNames() {
        return this.applicationContext.getBeanDefinitionNames();
    }

    @SuppressWarnings("unchecked")
    public <T> T registerBean(String name, Class<T> clazz, Object... args) {
        if (applicationContext.containsBean(name)) {
            Object bean = applicationContext.getBean(name);
            if (bean.getClass().isAssignableFrom(clazz)) {
                return (T) bean;
            } else {
                throw new MatrixWarnException("Duplicate Bean Name");
            }
        }
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        for (Object arg : args) {
            beanDefinitionBuilder.addConstructorArgValue(arg);
        }
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) configurableApplicationContext.getBeanFactory();
        beanFactory.registerBeanDefinition(name, beanDefinition);
        return applicationContext.getBean(name, clazz);
    }

    public void removeBean(String beanName) {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) configurableApplicationContext.getBeanFactory();
        beanFactory.removeBeanDefinition(beanName);
    }
}
