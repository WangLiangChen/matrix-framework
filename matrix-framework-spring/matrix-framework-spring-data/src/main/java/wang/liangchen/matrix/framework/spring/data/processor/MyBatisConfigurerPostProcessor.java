package wang.liangchen.matrix.framework.spring.data.processor;

import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.core.Ordered;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import wang.liangchen.matrix.framework.commons.StringUtil;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MyBatisConfigurerPostProcessor
 * mapperLocations配置支持多个路径
 * MapperScanne
 */
@Component
public class MyBatisConfigurerPostProcessor implements BeanDefinitionRegistryPostProcessor, BeanPostProcessor, BeanFactoryAware, Ordered {
    private final static String DEFAULT_SCAN_PACKAGES = "wang.liangchen.matrix.framework";
    private final static String MapperScannerConfigurerClassName = MapperScannerConfigurer.class.getName();
    private final static String BASEPACKAGE = "basePackage";
    private BeanFactory beanFactory;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        BeanDefinition beanDefinition = registry.getBeanDefinition(MapperScannerConfigurerClassName);
        MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
        String basePackage = (String) propertyValues.get(BASEPACKAGE);
        propertyValues.removePropertyValue(BASEPACKAGE);
        basePackage = null == basePackage || basePackage.isBlank() ? DEFAULT_SCAN_PACKAGES : DEFAULT_SCAN_PACKAGES.concat(Symbol.COMMA.getSymbol()).concat(basePackage);

        propertyValues.add(BASEPACKAGE, basePackage);
        registry.removeBeanDefinition(MapperScannerConfigurerClassName);
        registry.registerBeanDefinition(MapperScannerConfigurerClassName, beanDefinition);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof MybatisProperties) {
            List<String> packages = AutoConfigurationPackages.get(beanFactory);
            Set<String> scanPackages = new HashSet<>(packages);
            scanPackages.add(DEFAULT_SCAN_PACKAGES);
            scanPackages = scanPackages.stream()
                    .map(StringUtil.INSTANCE::package2Path)
                    .map(path -> ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX.concat(path).concat("/**/*.mapper.xml")).collect(Collectors.toSet());

            MybatisProperties mybatisProperties = (MybatisProperties) bean;
            String[] mapperLocations = mybatisProperties.getMapperLocations();
            mapperLocations = null == mapperLocations ? new String[0] : mapperLocations;
            scanPackages.addAll(Arrays.asList(mapperLocations));
            mybatisProperties.setMapperLocations(scanPackages.toArray(new String[0]));
            return mybatisProperties;
        }

        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
