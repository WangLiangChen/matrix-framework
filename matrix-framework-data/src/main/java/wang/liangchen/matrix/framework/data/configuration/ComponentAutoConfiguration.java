package wang.liangchen.matrix.framework.data.configuration;

import org.springframework.context.annotation.Bean;
import wang.liangchen.matrix.framework.data.dao.ISequenceDao;
import wang.liangchen.matrix.framework.data.dao.impl.SequenceDaoImpl;
import wang.liangchen.matrix.framework.data.postprocessor.DataBeanFactoryPostProcessor;

/**
 * @author Liangchen.Wang
 */

public class ComponentAutoConfiguration {
    @Bean("Matrix_Data_SequenceDao")
    public ISequenceDao sequenceDao() {
        return new SequenceDaoImpl();
    }

    @Bean
    public DataBeanFactoryPostProcessor dataBeanFactoryPostProcessor() {
        return new DataBeanFactoryPostProcessor();
    }
}
