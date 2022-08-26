package wang.liangchen.matrix.framework.lock.rdbms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.network.NetUtil;
import wang.liangchen.matrix.framework.data.datasource.ConnectionManager;
import wang.liangchen.matrix.framework.lock.core.LockConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author Liangchen.Wang 2022-08-22 23:30
 */
public class UpdateLockImpl extends AbstractRdbmsLock {
    private final Logger logger = LoggerFactory.getLogger(UpdateLockImpl.class);
    private final String UPDATE_SQL = "update matrix_lock set lock_at=?,lock_expire=?,lock_owner=? where lock_key=? and lock_expire<=?";


    protected UpdateLockImpl(LockConfiguration lockConfiguration, Connection connection) {
        super(lockConfiguration, connection);
    }

    @Override
    protected boolean executeBlockingSQL(final Connection connection, final String lockKey) {
        logger.debug("Lock '{}' is being obtained, waiting...", lockKey);
        if (inserted(connection, lockKey)) {
            logger.debug("Lock '{}' is inserted.", lockKey);
            boolean obtainedLock = lockByUpdate(connection, lockKey);
            if (obtainedLock) {
                logger.debug("Lock '{}' is obtained by update", lockKey);
                return true;
            }
            logger.debug("Lock '{}' is not obtained", lockKey);
            return false;
        }
        boolean obtainedLock = lockByInsert(connection, lockKey);
        if (obtainedLock) {
            logger.debug("Lock '{}' is obtained by insert", lockKey);
            return true;
        }
        logger.debug("Lock '{}' is not obtained", lockKey);
        return false;
    }

    protected boolean lockByUpdate(Connection connection, String lockKey) {
        PreparedStatement preparedStatement = null;
        LocalDateTime now = LocalDateTime.now();
        try {
            preparedStatement = connection.prepareStatement(UPDATE_SQL);
            preparedStatement.setObject(1, now);
            preparedStatement.setObject(2, LocalDateTime.ofInstant(lockConfiguration().getLockAtMost(), ZoneId.systemDefault()));
            preparedStatement.setString(3, NetUtil.INSTANCE.getLocalHostName());
            preparedStatement.setString(4, lockKey);
            preparedStatement.setObject(5, now);
            // obtained lock, go
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.error(e.getMessage());
        } finally {
            ConnectionManager.INSTANCE.closeStatement(preparedStatement);
        }
        return false;
    }
}
