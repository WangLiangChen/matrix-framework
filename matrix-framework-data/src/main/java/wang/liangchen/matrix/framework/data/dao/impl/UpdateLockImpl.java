package wang.liangchen.matrix.framework.data.dao.impl;

import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.data.dao.AbstractDBLock;
import wang.liangchen.matrix.framework.data.dao.IDBLock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * @author LiangChen.Wang
 */
public class UpdateLockImpl extends AbstractDBLock {

    private final String UPDATE_SQL = StringUtil.INSTANCE.format("update {} set lock_datetime=? where lock_key=?", IDBLock.TABLE_NAME);
    private final String INSERT_SQL = StringUtil.INSTANCE.format("insert into {} values(?,?,?,?)", IDBLock.TABLE_NAME);

    @Override
    protected void executeLockSQL(final Connection connection, final String lockName) throws SQLException {
        getLogger().debug("Lock '{}' is being obtained by thread:{}", lockName, Thread.currentThread().getName());
        if (lockViaUpdate(connection, lockName)) {
            getLogger().debug("Lock '{}' is obtained by thread:{}", lockName, Thread.currentThread().getName());
            return;
        }
        /*
         * 数据存在：第一个线程执行SQL成功，返回
         * 数据不存在：所有的线程不会等待，都会到这里执行insert
         * 第一个进入的线程会等待(因为上面的for update)，其它线程会产生死锁异常，然后第一个进入线程会执行成功，返回
         */
        lockViaInsert(connection, lockName);
        getLogger().debug("Insert a row,Lock '{}' is obtained by thread:{}", lockName, Thread.currentThread().getName());
    }

    private boolean lockViaUpdate(Connection connection, String lockName) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(UPDATE_SQL);
        ps.setObject(1, LocalDateTime.now());
        ps.setString(2, lockName);
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
                return true;
            }
            throw new MatrixInfoException("unknown error,updated rows is 0");
        } finally {
            closeStatement(ps);
        }
    }
}