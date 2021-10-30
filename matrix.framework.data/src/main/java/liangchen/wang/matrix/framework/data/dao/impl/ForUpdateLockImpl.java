package liangchen.wang.matrix.framework.data.dao.impl;

import liangchen.wang.matrix.framework.commons.exception.MatrixInfoException;
import liangchen.wang.matrix.framework.commons.utils.StringUtil;
import liangchen.wang.matrix.framework.data.dao.AbstractDBLock;
import liangchen.wang.matrix.framework.data.dao.IDBLock;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

;

/**
 * @author LiangChen.Wang
 */
@Repository("Matrix_Data_ForUpdateLock")
public class ForUpdateLockImpl extends AbstractDBLock {
    private final String SELECT_FOR_UPDATE_SQL = StringUtil.INSTANCE.format("select 0 from {} where lock_key=? for update", IDBLock.TABLE_NAME);
    private final String INSERT_SQL = StringUtil.INSTANCE.format("insert into {} values(?,?,?,?)", IDBLock.TABLE_NAME);


    @Override
    protected void executeLockSQL(final Connection connection, final String lockName) throws SQLException {
        getLogger().debug("Lock '{}' is being obtained by thread:{} ", lockName, Thread.currentThread().getName());
        if (lockViaSelectForUpdate(connection, lockName)) {
            getLogger().debug("Lock: '{}' is obtained by thread:{} ", lockName, Thread.currentThread().getName());
            return;
        }
        /**
         * 数据存在：第一个线程执行SQL成功，返回
         * 数据不存在：所有的线程不会等待，都会到这里执行insert
         * 第一个进入的线程会等待(因为上面的for update)，其它线程会产生死锁异常，然后第一个进入线程会执行成功，返回
         */
        lockViaInsert(connection, lockName);
        getLogger().debug("Insert a row,Lock: '{}' is obtained by thread:{} ", lockName, Thread.currentThread().getName());
    }

    private boolean lockViaSelectForUpdate(Connection connection, String lockName) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_FOR_UPDATE_SQL);
        ps.setString(1, lockName);
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            if (rs.next()) {
                // obtained lock, go
                return true;
            }
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
        }
        return false;
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
