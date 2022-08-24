package wang.liangchen.matrix.framework.lock.rdbms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.network.NetUtil;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.thread.ThreadUtil;
import wang.liangchen.matrix.framework.data.datasource.ConnectionManager;
import wang.liangchen.matrix.framework.data.datasource.dialect.PostgreSQLDialect;
import wang.liangchen.matrix.framework.lock.core.AbstractLock;
import wang.liangchen.matrix.framework.lock.core.LockConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Liangchen.Wang 2022-08-22 23:03
 */
public abstract class AbstractRdbmsLock extends AbstractLock {
    private final static Logger logger = LoggerFactory.getLogger(AbstractRdbmsLock.class);
    private final static Set<String> insertedLock = new HashSet<>();
    private final static int retryCount = 3;
    private final static long retryPeriod = 100L;
    private final String INSERT_SQL = "insert into matrix_lock values(?,?,?,?)";
    private final String UNLOCK_SQL = "update matrix_lock SET lock_expire = ? WHERE lock_key = ?";
    private final Connection connection;

    protected AbstractRdbmsLock(LockConfiguration lockConfiguration, Connection connection) {
        super(lockConfiguration);
        this.connection = ObjectUtil.INSTANCE.validateNotNull(connection);
    }

    protected abstract boolean executeBlockingSQL(Connection connection, String lockKey) throws SQLException;

    protected boolean inserted(String lockKey) {
        return insertedLock.contains(lockKey);
    }

    protected boolean lockByInsert(Connection connection, String lockKey) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(INSERT_SQL);
            preparedStatement.setString(1, lockKey);
            preparedStatement.setObject(2, LocalDateTime.now());
            preparedStatement.setObject(3, LocalDateTime.from(lockConfiguration().getLockAtMost()));
            preparedStatement.setString(4, NetUtil.INSTANCE.getLocalHostName());
            // obtained lock, go
            if (preparedStatement.executeUpdate() == 1) {
                insertedLock.add(lockKey);
                return true;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            // 主键冲突 说明有其它线程插入成功
            insertedLock.add(lockKey);
        } catch (SQLException ex) {
            // PostgreSQL 主键冲突
            if (PostgreSQLDialect.EXCEPTION_CLASS.equals(ex.getClass().getName())
                    && PostgreSQLDialect.PSQLState.UNIQUE_VIOLATION.getState().equals(ex.getSQLState())) {
                insertedLock.add(lockKey);
            }
            logger.error(ex.getMessage());
        } finally {
            ConnectionManager.INSTANCE.closeStatement(preparedStatement);
        }
        return false;
    }

    @Override
    protected boolean doLock() {
        String lockKey = lockKey();
        // 循环执行SQL,竞争不到锁则在此阻塞；竞争成功获取到锁则加入到当前线程；
        SQLException lastException = null;
        // 循环执行锁定SQL,[出现异常的线程]延时重试
        for (int count = 1; count <= retryCount; count++) {
            logger.debug("Lock '{}' will attempt - count:{} ", lockKey, count);
            try {
                /**
                 * 这里有阻塞、成功、失败三个状态，是否阻塞取决于事务提交的阶段
                 * 1、一个线程获锁成功后，在处理业务逻辑前提交事务，则不等待，其它线程获锁失败。
                 * 2、一个线程获锁成功后，在处理业务逻辑后提交事务，则其它线程等待获锁
                 */
                return executeBlockingSQL(this.connection, lockKey);
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

    @Override
    protected void doUnlock() {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(UNLOCK_SQL);
            preparedStatement.setString(1, lockKey());
            preparedStatement.setObject(2, LocalDateTime.from(lockConfiguration().getUnLockInstant()));
            int rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        } finally {
            ConnectionManager.INSTANCE.closeStatement(preparedStatement);
        }
    }

    public Connection getConnection() {
        return connection;
    }

}
