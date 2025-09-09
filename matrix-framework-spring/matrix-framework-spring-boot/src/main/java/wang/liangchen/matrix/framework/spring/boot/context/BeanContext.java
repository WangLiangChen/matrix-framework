package wang.liangchen.matrix.framework.spring.boot.context;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public enum BeanContext {
    INSTANCE;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private volatile ApplicationContext applicationContext;

    public void resetApplicationContext(ApplicationContext applicationContext) {
        lock.writeLock().lock();
        try {
            this.applicationContext = applicationContext;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public ApplicationContext getApplicationContext() {
        lock.readLock().lock();
        try {
            return this.applicationContext;
        } finally {
            lock.readLock().unlock();
        }
    }

    public <T> T getBean(String name) {
        Object bean = this.getApplicationContext().getBean(name);
        return ObjectUtil.INSTANCE.cast(bean);
    }

    public <T> T getBean(Class<T> clazz) {
        return this.getApplicationContext().getBean(clazz);
    }

    public <T> T getBean(String name, Class<T> clazz) {
        return this.getApplicationContext().getBean(name, clazz);
    }

    public <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return this.getApplicationContext().getBeansOfType(clazz);
    }

    public Class<?> getType(String name) {
        return this.getApplicationContext().getType(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T registerBean(String beanName, Class<T> beanClass, Object... constructorArgValues) {
        ApplicationContext innerApplicatonContext = this.getApplicationContext();
        if (innerApplicatonContext.containsBean(beanName)) {
            Object bean = innerApplicatonContext.getBean(beanName);
            if (bean.getClass().isAssignableFrom(beanClass)) {
                return (T) bean;
            } else {
                throw new MatrixErrorException("Duplicate Bean Name");
            }
        }
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        for (Object constructorArgValue : constructorArgValues) {
            beanDefinitionBuilder.addConstructorArgValue(constructorArgValue);
        }
        BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) innerApplicatonContext;
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) configurableApplicationContext.getBeanFactory();
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
        return innerApplicatonContext.getBean(beanName, beanClass);
    }
}
