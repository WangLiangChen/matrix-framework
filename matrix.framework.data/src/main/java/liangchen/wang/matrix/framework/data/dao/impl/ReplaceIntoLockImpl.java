package liangchen.wang.matrix.framework.data.dao.impl;

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
@Repository("Gradf_Data_ReplaceIntoLock")
public class ReplaceIntoLockImpl extends AbstractDBLock {

    private final String REPLACE_LOCK = StringUtil.INSTANCE.format("replace into {} values (?,?,?,?)", IDBLock.TABLE_NAME);

    @Override
    protected void executeLockSQL(final Connection connection, final String lockKey) throws SQLException {
        lockViaReplaceInto(connection, lockKey, REPLACE_LOCK);
    }

    private boolean lockViaReplaceInto(Connection connection, String lockKey, String sql) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        LocalDateTime now = LocalDateTime.now();
        ps.setString(1, lockKey);
        ps.setObject(2, now);
        ps.setObject(3, now);
        ps.setString(4, "");
        try {
            getLogger().debug("Lock '{}' is being obtained: {}", lockKey, Thread.currentThread().getName());
            return ps.executeUpdate() >= 1;
        } finally {
            closeStatement(ps);
        }
    }
}
