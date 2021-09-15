package liangchen.wang.matrix.framework.springboot.processor;

import liangchen.wang.gradf.framework.springboot.annotation.OverrideBeanName;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.MethodMetadata;
import org.springframework.stereotype.Component;

/**
 * 用于实现Bean被覆盖的效果
 * 比如将以xxxOverride命名的BeanDefinition重新注册名为xxx
 *
 * @author LiangChen.Wang 2020/9/15
 */
@Component
public class OverrideBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

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
            if (!methodMetadata.isAnnotated(OverrideBeanName.class.getName())) {
                continue;
            }
            MergedAnnotation<OverrideBeanName> overrideBeanNameMergedAnnotation = methodMetadata.getAnnotations().get(OverrideBeanName.class);
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

    }

}
