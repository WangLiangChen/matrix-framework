package wang.liangchen.matrix.framework.data.mybatis.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import wang.liangchen.matrix.framework.data.enumeration.StateEnum;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Liangchen.Wang 2022-09-01 6:51
 */
public class StateEnumTypeHandler extends BaseTypeHandler<StateEnum> {
    private final Class<StateEnum> type;

    public StateEnumTypeHandler() {
        this.type = null;
    }

    public StateEnumTypeHandler(Class<StateEnum> type) {
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, StateEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public StateEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseStateEnum(rs.getString(columnName));
    }

    @Override
    public StateEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseStateEnum(rs.getString(columnIndex));
    }

    @Override
    public StateEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseStateEnum(cs.getString(columnIndex));
    }

    public StateEnum parseStateEnum(String name) {
        return StateEnum.valueOf(name);
    }
}
