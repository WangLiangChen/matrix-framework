package liangchen.wang.matrix.framework.data.configuration;

import liangchen.wang.matrix.framework.data.dao.IDBLock;
import liangchen.wang.matrix.framework.data.dao.StandaloneDao;
import liangchen.wang.matrix.framework.data.dao.impl.ForUpdateLockImpl;
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
