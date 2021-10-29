package liangchen.wang.matrix.framework.data.dao.impl;

import liangchen.wang.matrix.framework.commons.exception.MatrixInfoException;
import liangchen.wang.matrix.framework.commons.utils.StringUtil;
import liangchen.wang.matrix.framework.data.dao.AbstractDBLock;
import liangchen.wang.matrix.framework.data.dao.IDBLock;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * @author LiangChen.Wang
 */
@Repository("Matrix_Data_UpdateLockLock")
public class UpdateLockImpl extends AbstractDBLock {

    private final String UPDATE_SQL = StringUtil.INSTANCE.format("update {} set lock_datetime=? where lock_key=?", IDBLock.TABLE_NAME);
    private final String INSERT_SQL = StringUtil.INSTANCE.format("insert into {} values(?,?,?,?)", IDBLock.TABLE_NAME);

    @Override
    protected void executeLockSQL(final Connection connection, final String lockName) throws SQLException {
        if (lockViaUpdate(connection, lockName)) {
            return;
        }
        // 当数据不存在时，线程并不会等待，所有线程都会到达这里;
        // 当多个线程都到这里执行insert时，第一个进入的线程会等待，其它线程会产生死锁异常，然后第一个进入线程会执行成功
        lockViaInsert(connection, lockName);
    }

    private boolean lockViaUpdate(Connection connection, String lockName) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(UPDATE_SQL);
        ps.setObject(1, LocalDateTime.now());
        ps.setString(2, lockName);
        getLogger().debug("Lock '{}' is being obtained: {}", lockName, Thread.currentThread().getName());
        try {
            return ps.executeUpdate() >= 1;
        } finally {
            closeStatement(ps);
        }
    }

    private boolean lockViaInsert(Connection connection, String lockName) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(INSERT_SQL);
        ps.setString(1, lockName);
        LocalDateTime now = LocalDateTime.now();
        ps.setObject(2, now);
        ps.setObject(3, now);
        ps.setString(4, "");
        try {
            if (ps.executeUpdate() == 1) {
                // obtained lock, go
                getLogger().debug("Inserting new lock row for lock: '{}' being obtained by thread: {}", lockName, Thread.currentThread().getName());
                return true;
            }
            throw new MatrixInfoException("unknown error,updated rows is 0");
        } finally {
            closeStatement(ps);
        }
    }
}
