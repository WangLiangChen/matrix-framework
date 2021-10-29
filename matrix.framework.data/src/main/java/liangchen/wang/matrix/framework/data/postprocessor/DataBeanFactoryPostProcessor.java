package liangchen.wang.matrix.framework.data.postprocessor;

import liangchen.wang.matrix.framework.commons.exception.MatrixInfoException;
import liangchen.wang.matrix.framework.commons.utils.CollectionUtil;
import liangchen.wang.matrix.framework.data.dao.AbstractDao;
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
