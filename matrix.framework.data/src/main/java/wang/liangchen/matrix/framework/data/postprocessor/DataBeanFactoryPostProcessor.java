package wang.liangchen.matrix.framework.data.postprocessor;

import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.utils.CollectionUtil;
import wang.liangchen.matrix.framework.data.dao.AbstractDao;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * @author LiangChen.Wang
 */
@Component
public class DataBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        String[] abstractDao = configurableListableBeanFactory.getBeanNamesForType(AbstractDao.class);
        if (CollectionUtil.INSTANCE.isEmpty(abstractDao)) {
            return;
        }
        boolean hasDataSource = configurableListableBeanFactory.containsBean("dataSource");
        if (hasDataSource) {
            return;
        }
        throw new MatrixInfoException("Please use annotation '@EnableJdbc' to enable JDBC");
    }
}
