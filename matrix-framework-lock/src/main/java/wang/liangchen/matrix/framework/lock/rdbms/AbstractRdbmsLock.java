package wang.liangchen.matrix.framework.lock.rdbms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.thread.ThreadUtil;
import wang.liangchen.matrix.framework.data.datasource.ConnectionManager;
import wang.liangchen.matrix.framework.lock.core.AbstractLock;
import wang.liangchen.matrix.framework.lock.core.LockConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Liangchen.Wang 2022-08-22 23:03
 */
public abstract class AbstractRdbmsLock extends AbstractLock {
    private final static Logger logger = LoggerFactory.getLogger(AbstractRdbmsLock.class);
    private final static int retryCount = 3;
    private final static long retryPeriod = 100L;
    private final String INSERT_SQL = "insert into matrix_lock values(?,?,?,?)";
    private final String DELETE_SQL = "delete from matrix_lock where lock_key=?";
    private final static ThreadLocal<Set<String>> currentThreadHoldLocks = ThreadLocal.withInitial(() -> new HashSet<>(16));
    private final Connection connection;

    protected AbstractRdbmsLock(LockConfiguration lockConfiguration, Connection connection) {
        super(lockConfiguration);
        this.connection = ObjectUtil.INSTANCE.validateNotNull(connection);
    }

    protected abstract void executeBlockingSQL(Connection connection, String lockKey) throws SQLException;

    protected boolean lockByInsert(Connection connection, String lockName) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL);
        preparedStatement.setString(1, lockName);
        LocalDateTime now = LocalDateTime.now();
        preparedStatement.setObject(2, now);
        preparedStatement.setObject(3, now);
        preparedStatement.setString(4, "");
        try {
            if (preparedStatement.executeUpdate() == 1) {
                // obtained lock, go
                return true;
            }
            throw new MatrixInfoException("unknown error,updated rows is 0");
        } finally {
            ConnectionManager.INSTANCE.closeStatement(preparedStatement);
        }
    }

    @Override
    public boolean doLock() {
        String lockKey = lockConfiguration().getLockKey();
        logger.debug("Lock '{}' is desired", lockKey);
        // 当前线程已经持有锁
        if (currentThreadHoldLock(lockKey)) {
            logger.debug("Lock '{}' is already owned", lockKey);
            return true;
        }
        // 循环执行SQL,竞争不到锁则在此阻塞；竞争成功获取到锁则加入到当前线程；
        loopExecuteBlockingSQL(lockKey);
        currentThreadHoldLocks().add(lockKey);
        logger.debug("Lock '{}' is given", lockKey);
        return true;
    }

    @Override
    public void doUnlock() {
        String lockKey = lockConfiguration().getLockKey();
        logger.debug("Lock '{}' is returned", lockKey);
        currentThreadHoldLocks().remove(lockKey);
    }

    private void loopExecuteBlockingSQL(String lockKey) {
        SQLException lastException = null;
        // 循环执行锁定SQL,[出现异常的线程]延时重试
        for (int count = 1; count <= retryCount; count++) {
            logger.debug("Lock '{}' will attempt - count:{} ", lockKey, count);
            try {
                // 在这里阻塞
                executeBlockingSQL(this.connection, lockKey);
                return;
            } catch (SQLException e) {
                // 插入冲突异常或者死锁异常或者锁定超时异常
                lastException = e;
                // 达到重试次数
                if (count >= retryCount) {
                    logger.debug("Lock '{}' was not obtained - finally", lockKey);
                    break;
                }
                // 延时重试
                logger.debug("Lock '{}' was not obtained - will try again.count:{}", lockKey, count);
                ThreadUtil.INSTANCE.sleep(retryPeriod);
            }
        }
        throw new MatrixInfoException("Failure obtaining lock, reached maximum number of attempts. Initial exception (if any) attached as root cause.{}", lastException.getMessage());
    }

    public boolean currentThreadHoldLock(String lockKey) {
        return currentThreadHoldLocks().contains(lockKey);
    }

    public Set<String> currentThreadHoldLocks() {
        return this.currentThreadHoldLocks.get();
    }

    public Connection getConnection() {
        return connection;
    }
}
