package wang.liangchen.matrix.framework.data.configuration;

import org.springframework.context.annotation.Bean;
import wang.liangchen.matrix.framework.data.dao.ISequenceDao;
import wang.liangchen.matrix.framework.data.dao.impl.SequenceDaoImpl;

/**
 * @author Liangchen.Wang
 */

public class ComponentAutoConfiguration {
    @Bean
    public ISequenceDao sequenceDao() {
        return new SequenceDaoImpl();
    }
}
