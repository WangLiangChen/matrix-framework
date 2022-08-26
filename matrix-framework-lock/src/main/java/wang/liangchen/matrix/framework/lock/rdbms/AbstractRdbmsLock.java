package wang.liangchen.matrix.framework.lock.rdbms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.network.NetUtil;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.data.datasource.ConnectionManager;
import wang.liangchen.matrix.framework.data.datasource.dialect.PostgreSQLDialect;
import wang.liangchen.matrix.framework.lock.core.AbstractLock;
import wang.liangchen.matrix.framework.lock.core.LockConfiguration;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Liangchen.Wang 2022-08-22 23:03
 */
public abstract class AbstractRdbmsLock extends AbstractLock {
    private final static Logger logger = LoggerFactory.getLogger(AbstractRdbmsLock.class);
    private final static Set<String> insertedLock = new HashSet<>();
    private final String SELECT_SQL = "select 0 from matrix_lock where lock_key = ?";
    private final String INSERT_SQL = "insert into matrix_lock values(?,?,?,?)";
    private final String UNLOCK_SQL = "update matrix_lock SET lock_expire = ? WHERE lock_key = ?";
    private final Connection connection;

    protected AbstractRdbmsLock(LockConfiguration lockConfiguration, Connection connection) {
        super(lockConfiguration);
        this.connection = ObjectUtil.INSTANCE.validateNotNull(connection);
    }

    protected abstract boolean executeBlockingSQL(Connection connection, String lockKey);

    protected boolean inserted(Connection connection, String lockKey) {
        if (insertedLock.contains(lockKey)) {
            return true;
        }
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SELECT_SQL);
            preparedStatement.setString(1, lockKey);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                insertedLock.add(lockKey);
                return true;
            }
        } catch (SQLException e) {
            return false;
        } finally {
            ConnectionManager.INSTANCE.closeResultSet(resultSet);
            ConnectionManager.INSTANCE.closeStatement(preparedStatement);
        }
        return false;
    }

    protected boolean lockByInsert(Connection connection, String lockKey) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(INSERT_SQL);
            preparedStatement.setString(1, lockKey);
            preparedStatement.setObject(2, LocalDateTime.ofInstant(lockConfiguration().getLockAt(), ZoneId.systemDefault()));
            preparedStatement.setObject(3, LocalDateTime.ofInstant(lockConfiguration().getLockAtMost(), ZoneId.systemDefault()));
            preparedStatement.setString(4, NetUtil.INSTANCE.getLocalHostName());
            // obtained lock, go
            if (preparedStatement.executeUpdate() == 1) {
                insertedLock.add(lockKey);
                return true;
            }
        } catch (SQLException ex) {
            // PostgreSQL 主键冲突
            if (ex instanceof SQLIntegrityConstraintViolationException
                    || (PostgreSQLDialect.EXCEPTION_CLASS.equals(ex.getClass().getName()) && PostgreSQLDialect.PSQLState.UNIQUE_VIOLATION.getState().equals(ex.getSQLState()))) {
                insertedLock.add(lockKey);
                logger.debug("Primary key  conflict, '{}'", lockKey);
                return false;
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
        return executeBlockingSQL(connection, lockKey);
    }

    @Override
    protected void doUnlock() {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(UNLOCK_SQL);
            preparedStatement.setObject(1, LocalDateTime.ofInstant(lockConfiguration().getUnLockInstant(), ZoneId.systemDefault()));
            preparedStatement.setString(2, lockKey());
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
