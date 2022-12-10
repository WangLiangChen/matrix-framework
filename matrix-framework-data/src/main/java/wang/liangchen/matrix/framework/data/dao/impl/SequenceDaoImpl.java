package wang.liangchen.matrix.framework.data.dao.impl;


import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.thread.ThreadUtil;
import wang.liangchen.matrix.framework.data.dao.ISequenceDao;
import wang.liangchen.matrix.framework.data.dao.SequenceKey;
import wang.liangchen.matrix.framework.data.datasource.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * @author LiangChen.Wang
 */
public class SequenceDaoImpl implements ISequenceDao {
    private final String SELECT_SQL = "select sequence_number from matrix_sequence where sequence_group=? and sequence_key=?";
    private final String UPDATE_SQL = "update matrix_sequence set sequence_number=sequence_number+1 where sequence_group=? and sequence_key=?";
    private final String INSERT_SQL = "insert into matrix_sequence values(?,?,?)";

    @Override
    public Long sequenceNumber(SequenceKey sequenceKey) {
        for (int i = 0; i < 3; i++) {
            long sequenceNumber = fetchSequenceNumber(sequenceKey);
            if (-1 == sequenceNumber) {
                ThreadUtil.INSTANCE.sleep(TimeUnit.MILLISECONDS, 100);
                continue;
            }
            return sequenceNumber;
        }
        throw new MatrixWarnException("Could't fetch sequence number!");
    }

    private long fetchSequenceNumber(SequenceKey sequenceKey) {
        return ConnectionManager.INSTANCE.executeInNonManagedConnection((connection) -> {
            PreparedStatement statement = null;
            int rows;
            // 先更新,如果数据还不存在则rows==0
            try {
                statement = connection.prepareStatement(UPDATE_SQL);
                statement.setString(1, sequenceKey.getSequenceGroup());
                statement.setString(2, sequenceKey.getSequenceKey());
                rows = statement.executeUpdate();
            } catch (SQLException e) {
                // 更新失败 重试
                return -1L;
            } finally {
                ConnectionManager.INSTANCE.closeStatement(statement);
            }
            // 数据不存在 则尝试插入
            if (0 == rows) {
                try {
                    statement = connection.prepareStatement(INSERT_SQL);
                    statement.setString(1, sequenceKey.getSequenceGroup());
                    statement.setString(2, sequenceKey.getSequenceKey());
                    statement.setLong(3, sequenceKey.getInitialValue());
                    statement.executeUpdate();
                    statement.close();
                    return sequenceKey.getInitialValue();
                } catch (SQLException ex) {
                    // 插入失败(含主键冲突) 重试
                    ConnectionManager.INSTANCE.closeStatement(statement);
                    return -1L;
                }
            }
            // 数据存在，则查询返回
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(SELECT_SQL);
                statement.setString(1, sequenceKey.getSequenceGroup());
                statement.setString(2, sequenceKey.getSequenceKey());
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getLong("sequence_number");
                }
            } catch (SQLException e) {
                return -1L;
            } finally {
                ConnectionManager.INSTANCE.closeResultSet(resultSet);
                ConnectionManager.INSTANCE.closeStatement(statement);
            }
            return -1L;
        }, Connection.TRANSACTION_READ_COMMITTED);
    }
}
