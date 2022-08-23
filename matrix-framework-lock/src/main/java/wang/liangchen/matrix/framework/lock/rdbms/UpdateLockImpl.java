package wang.liangchen.matrix.framework.lock.rdbms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.data.datasource.ConnectionManager;
import wang.liangchen.matrix.framework.lock.core.LockConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * @author Liangchen.Wang 2022-08-22 23:30
 */
public class UpdateLockImpl extends AbstractRdbmsLock {
    private final Logger logger = LoggerFactory.getLogger(UpdateLockImpl.class);
    private final String UPDATE_SQL = "update matrix_lock set lock_datetime=? where lock_key=?";

    protected UpdateLockImpl(LockConfiguration lockConfiguration, Connection connection) {
        super(lockConfiguration, connection);
    }

    @Override
    protected void executeBlockingSQL(final Connection connection, final String lockName) throws SQLException {
        logger.debug("Lock '{}' is being obtained, waiting...", lockName);
        if (lockByUpdate(connection, lockName)) {
            logger.debug("Lock '{}' is obtained", lockName);
            return;
        }
        lockByInsert(connection, lockName);
        logger.debug("Insert a row, Lock '{}' is obtained", lockName);
    }

    private boolean lockByUpdate(Connection connection, String lockName) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);
        preparedStatement.setObject(1, LocalDateTime.now());
        preparedStatement.setString(2, lockName);
        try {
            return preparedStatement.executeUpdate() >= 1;
        } finally {
            ConnectionManager.INSTANCE.closeStatement(preparedStatement);
        }
    }
}
