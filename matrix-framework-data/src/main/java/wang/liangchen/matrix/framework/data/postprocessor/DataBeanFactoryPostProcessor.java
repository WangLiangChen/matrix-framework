package wang.liangchen.matrix.framework.data.postprocessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.data.dao.AbstractDao;

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
        throw new MatrixWarnException("Please use annotation '@EnableJdbc' to enable JDBC");
    }

}
