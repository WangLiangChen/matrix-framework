package wang.liangchen.matrix.framework.lock.rdbms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.data.datasource.ConnectionManager;
import wang.liangchen.matrix.framework.lock.core.LockConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Liangchen.Wang 2022-08-22 23:29
 */
public class ForUpdateLockImpl extends AbstractRdbmsLock {
    private Logger logger = LoggerFactory.getLogger(ForUpdateLockImpl.class);
    private final String SELECT_FOR_UPDATE_SQL = "select 0 from matrix_lock where lock_key=? for update";

    protected ForUpdateLockImpl(LockConfiguration lockConfiguration, Connection connection) {
        super(lockConfiguration, connection);
    }

    @Override
    protected boolean executeBlockingSQL(final Connection connection, final String lockKey) {
        logger.debug("Lock '{}' is being obtained, waiting...", lockKey);
        boolean obtainedLock = false;
        if (!inserted(lockKey)) {
            obtainedLock = lockByInsert(connection, lockKey);
        }
        if (obtainedLock) {
            logger.debug("Lock '{}' is obtained by insert", lockKey);
            return true;
        }
        obtainedLock = lockBySelectForUpdate(connection, lockKey);
        if (obtainedLock) {
            logger.debug("Lock '{}' is obtained by (select... for update)", lockKey);
            return true;
        }
        logger.debug("Lock '{}' is not obtained", lockKey);
        return false;
    }

    private boolean lockBySelectForUpdate(Connection connection, String lockName) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SELECT_FOR_UPDATE_SQL);
            preparedStatement.setString(1, lockName);
            resultSet = preparedStatement.executeQuery();
            // obtained lock, go
            return resultSet.next();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        } finally {
            ConnectionManager.INSTANCE.closeResultSet(resultSet);
            ConnectionManager.INSTANCE.closeStatement(preparedStatement);
        }
        return false;
    }
}
