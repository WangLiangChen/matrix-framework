package wang.liangchen.matrix.framework.data.configuration;

import wang.liangchen.matrix.framework.data.dao.IDBLock;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.data.dao.impl.ForUpdateLockImpl;
import org.springframework.context.annotation.Bean;

public class ComponentAutoConfiguration {

    @Bean
    public StandaloneDao standaloneDao() {
        return new StandaloneDao();
    }

    @Bean
    public IDBLock dbLock() {
        return new ForUpdateLockImpl();
    }
}
