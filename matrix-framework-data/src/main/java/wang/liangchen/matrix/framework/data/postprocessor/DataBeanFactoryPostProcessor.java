package wang.liangchen.matrix.framework.data.postprocessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;
import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.enumeration.ConstantEnum;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.data.dao.AbstractDao;
import wang.liangchen.matrix.framework.data.enumeration.StateEnum;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author LiangChen.Wang
 */
@Component
public class DataBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        scanAndLoadClass(configurableListableBeanFactory);
        String[] abstractDao = configurableListableBeanFactory.getBeanNamesForType(AbstractDao.class);
        if (CollectionUtil.INSTANCE.isEmpty(abstractDao)) {
            return;
        }
        boolean hasDataSource = configurableListableBeanFactory.containsBean("dataSource");
        if (hasDataSource) {
            return;
        }
        throw new MatrixWarnException("Please use annotation '@EnableJdbc' to enable JDBC");
    }

    private void scanAndLoadClass(ConfigurableListableBeanFactory configurableListableBeanFactory) {
        // 主动实例化下StateEnum
        StateEnum none = StateEnum.NONE;
        ClassLoader beanClassLoader = configurableListableBeanFactory.getBeanClassLoader();
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(ConstantEnum.class));
        List<String> basePackages = AutoConfigurationPackages.get(configurableListableBeanFactory);
        Set<String> classNames = new HashSet<>();
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = provider.findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                classNames.add(candidateComponent.getBeanClassName());
            }
        }
        // load
        for (String className : classNames) {
            try {
                Class.forName(className, true, beanClassLoader);
            } catch (ClassNotFoundException e) {
                // skip
            }
        }
    }


}
