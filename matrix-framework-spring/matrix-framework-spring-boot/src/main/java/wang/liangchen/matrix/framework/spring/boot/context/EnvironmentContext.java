package wang.liangchen.matrix.framework.spring.boot.context;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import wang.liangchen.matrix.framework.spring.boot.config.MatrixConfigDataSource;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public enum EnvironmentContext {
    INSTANCE;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private Environment environment;

    public void resetEnvironmentContext(Environment environment) {
        lock.writeLock().lock();
        try {
            this.environment = environment;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Environment getEnvironment() {
        lock.readLock().lock();
        try {
            return this.environment;
        } finally {
            lock.readLock().unlock();
        }
    }

}
