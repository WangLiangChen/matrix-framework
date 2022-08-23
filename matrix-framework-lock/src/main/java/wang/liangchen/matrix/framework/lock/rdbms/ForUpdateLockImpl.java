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
public class ForUpdateLockImpl extends AbstractRdbmsLock{
    private Logger logger = LoggerFactory.getLogger(ForUpdateLockImpl.class);
    private final String SELECT_FOR_UPDATE_SQL = "select 0 from matrix_lock where lock_key=? for update";

    protected ForUpdateLockImpl(LockConfiguration lockConfiguration, Connection connection) {
        super(lockConfiguration, connection);
    }

    @Override
    protected void executeBlockingSQL(final Connection connection, final String lockName) throws SQLException {
        logger.debug("Lock '{}' is being obtained, waiting...", lockName);
        if (lockBySelectForUpdate(connection, lockName)) {
            logger.debug("Lock: '{}' is obtained", lockName);
            return;
        }
        lockByInsert(connection, lockName);
        logger.debug("Insert a row,Lock: '{}' is obtained", lockName);
    }

    private boolean lockBySelectForUpdate(Connection connection, String lockName) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_FOR_UPDATE_SQL);
        preparedStatement.setString(1, lockName);
        ResultSet resultSet = null;
        try {
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                // obtained lock, go
                return true;
            }
        } finally {
            ConnectionManager.INSTANCE.closeResultSet(resultSet);
            ConnectionManager.INSTANCE.closeStatement(preparedStatement);
        }
        return false;
    }
}
