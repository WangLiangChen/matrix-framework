package wang.liangchen.matrix.framework.data.dao.impl;

import wang.liangchen.matrix.framework.commons.utils.StringUtil;
import wang.liangchen.matrix.framework.data.dao.AbstractDBLock;
import wang.liangchen.matrix.framework.data.dao.IDBLock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * @author LiangChen.Wang
 */
public class ReplaceIntoLockImpl extends AbstractDBLock {

    private final String REPLACE_LOCK = StringUtil.INSTANCE.format("replace into {} values (?,?,?,?)", IDBLock.TABLE_NAME);

    @Override
    protected void executeLockSQL(final Connection connection, final String lockKey) throws SQLException {
        getLogger().debug("Lock '{}' is being obtained by thread:{}", lockKey, Thread.currentThread().getName());
        lockViaReplaceInto(connection, lockKey, REPLACE_LOCK);
        getLogger().debug("Lock '{}' is obtained by thread:{}", lockKey, Thread.currentThread().getName());
    }

    private boolean lockViaReplaceInto(Connection connection, String lockKey, String sql) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        LocalDateTime now = LocalDateTime.now();
        ps.setString(1, lockKey);
        ps.setObject(2, now);
        ps.setObject(3, now);
        ps.setString(4, "");
        try {
            return ps.executeUpdate() >= 1;
        } finally {
            closeStatement(ps);
        }
    }
}
