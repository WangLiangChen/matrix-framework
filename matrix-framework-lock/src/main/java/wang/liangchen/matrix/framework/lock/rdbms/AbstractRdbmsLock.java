package wang.liangchen.matrix.framework.lock.rdbms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.network.NetUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.datasource.ConnectionManager;
import wang.liangchen.matrix.framework.data.datasource.dialect.PostgreSQLDialect;
import wang.liangchen.matrix.framework.lock.core.AbstractLock;
import wang.liangchen.matrix.framework.lock.core.LockProperties;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;


/**
 * @author Liangchen.Wang 2022-08-22 23:03
 */
public abstract class AbstractRdbmsLock extends AbstractLock {
    private final static Logger logger = LoggerFactory.getLogger(AbstractRdbmsLock.class);
    private final static Set<LockProperties.LockKey> insertedLock = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
    private final String SELECT_SQL = "select 0 from matrix_lock where lock_group=? and lock_key = ?";
    private final String INSERT_SQL = "insert into matrix_lock values(?,?,?,?,?)";
    private final String UNLOCK_SQL = "update matrix_lock SET lock_expire = ? WHERE lock_group=? and lock_key = ?";
    private final DataSource dataSource;

    protected AbstractRdbmsLock(LockProperties lockProperties, DataSource dataSource) {
        super(lockProperties);
        this.dataSource = ValidationUtil.INSTANCE.notNull(dataSource);
    }

    protected abstract boolean executeBlockingSQL(Connection connection, LockProperties.LockKey lockKey);

    protected boolean inserted(Connection connection, LockProperties.LockKey lockKey) {
        if (insertedLock.contains(lockKey)) {
            return true;
        }
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SELECT_SQL);
            preparedStatement.setString(1, lockKey.getLockGroup());
            preparedStatement.setString(2, lockKey.getLockKey());
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

    protected boolean lockByInsert(Connection connection, LockProperties.LockKey lockKey) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(INSERT_SQL);
            preparedStatement.setString(1, lockKey.getLockGroup());
            preparedStatement.setString(2, lockKey.getLockKey());
            preparedStatement.setObject(3, LocalDateTime.ofInstant(lockProperties().getLockAt(), ZoneId.systemDefault()));
            preparedStatement.setObject(4, LocalDateTime.ofInstant(lockProperties().getLockAtMost(), ZoneId.systemDefault()));
            preparedStatement.setString(5, NetUtil.INSTANCE.getLocalHostName());
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
        LockProperties.LockKey lockKey = lockKey();
        return ConnectionManager.INSTANCE.executeInNonManagedConnection(this.dataSource, (connection) -> executeBlockingSQL(connection, lockKey), Connection.TRANSACTION_READ_COMMITTED);
    }

    @Override
    protected void doUnlock() {
        ConnectionManager.INSTANCE.executeInNonManagedConnection(this.dataSource, (connection) -> {
            PreparedStatement preparedStatement = null;
            int rows = 0;
            try {
                preparedStatement = connection.prepareStatement(UNLOCK_SQL);
                preparedStatement.setObject(1, LocalDateTime.ofInstant(lockProperties().getUnLockInstant(), ZoneId.systemDefault()));
                LockProperties.LockKey lockKey = lockKey();
                preparedStatement.setString(2, lockKey.getLockGroup());
                preparedStatement.setString(3, lockKey.getLockKey());
                rows = preparedStatement.executeUpdate();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            } finally {
                ConnectionManager.INSTANCE.closeStatement(preparedStatement);
            }
            return rows;
        }, Connection.TRANSACTION_READ_COMMITTED);
    }

}
