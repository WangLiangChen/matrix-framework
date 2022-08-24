package wang.liangchen.matrix.framework.lock.rdbms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.data.datasource.ConnectionManager;
import wang.liangchen.matrix.framework.lock.core.Lock;
import wang.liangchen.matrix.framework.lock.core.LockConfiguration;
import wang.liangchen.matrix.framework.lock.core.LockManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * @author Liangchen.Wang 2022-08-22 23:35
 */
public class RdbmsFailFastLockManager implements LockManager {
    private final static Logger logger = LoggerFactory.getLogger(RdbmsFailFastLockManager.class);
    private final DataSource dataSource;

    public RdbmsFailFastLockManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Lock getLock(LockConfiguration lockConfiguration) {
        return new UpdateLockImpl(lockConfiguration, ConnectionManager.INSTANCE.nonManagedConnection(this.dataSource));
    }

    @Override
    public void executeInLock(LockConfiguration lockConfiguration, Runnable task) {
        executeInLock(lockConfiguration, () -> {
            task.run();
            return null;
        });
    }

    @Override
    public <R> R executeInLock(LockConfiguration lockConfiguration, Supplier<R> task) {
        AbstractRdbmsLock lock = (AbstractRdbmsLock) getLock(lockConfiguration);
        Connection connection = lock.getConnection();
        try {
            connection.setAutoCommit(true);
            return task.get();
        } catch (SQLException e) {
            throw new MatrixInfoException("get Connection AutoCommit error", e);
        } finally {
            lock.unlock();
        }
    }
}
