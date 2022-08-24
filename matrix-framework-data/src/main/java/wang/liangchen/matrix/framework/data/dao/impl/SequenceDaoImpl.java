package wang.liangchen.matrix.framework.data.dao.impl;


import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.thread.ThreadUtil;
import wang.liangchen.matrix.framework.data.dao.ISequenceDao;
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
    private final String SELECT_SQL = "select sequence_number from matrix_sequence where sequence_key=?";
    private final String UPDATE_SQL = "update matrix_sequence set sequence_number=sequence_number+1 where sequence_key=?";
    private final String INSERT_SQL = "insert into matrix_sequence values(?,?)";

    @Override
    public Long sequenceNumber(String sequenceKey, long initialValue) {
        for (int i = 0; i < 3; i++) {
            long sequenceNumber = fetchSequenceNumber(sequenceKey, initialValue);
            if (-1 == sequenceNumber) {
                ThreadUtil.INSTANCE.sleep(TimeUnit.MILLISECONDS, 50);
                continue;
            }
            return sequenceNumber;
        }
        throw new MatrixInfoException("Could't fetch sequence number!");
    }

    private long fetchSequenceNumber(String sequenceKey, long initialValue) {
        return ConnectionManager.INSTANCE.executeInNonManagedConnection((connection) -> {
            PreparedStatement statement = null;
            int rows = 0;
            // 先更新
            try {
                statement = connection.prepareStatement(UPDATE_SQL);
                statement.setString(1, sequenceKey);
                rows = statement.executeUpdate();
            } catch (SQLException e) {
                return -1L;
            } finally {
                ConnectionManager.INSTANCE.closeStatement(statement);
            }
            // 更新失败则尝试插入
            if (0 == rows) {
                try {
                    statement = connection.prepareStatement(INSERT_SQL);
                    statement.setString(1, sequenceKey);
                    statement.setLong(2, initialValue);
                    statement.executeUpdate();
                    statement.close();
                    return initialValue;
                } catch (SQLException ex) {
                    // 主键冲突 插入失败
                    return -1L;
                }
            }
            // 更新成功,说明数据存在，则查询返回
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(SELECT_SQL);
                statement.setString(1, sequenceKey);
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
