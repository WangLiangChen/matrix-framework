package wang.liangchen.matrix.framework.data.mybatis.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import wang.liangchen.matrix.framework.commons.enumeration.ConstantEnum;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Liangchen.Wang 2022-09-01 6:51
 */
public class ConstantEnumTypeHandler extends BaseTypeHandler<ConstantEnum> {
    private final Class<ConstantEnum> type;

    public ConstantEnumTypeHandler() {
        this.type = null;
    }

    public ConstantEnumTypeHandler(Class<ConstantEnum> type) {
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ConstantEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.key());
    }

    @Override
    public ConstantEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseConstantEnum(rs.getString(columnName));
    }

    @Override
    public ConstantEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseConstantEnum(rs.getString(columnIndex));
    }

    @Override
    public ConstantEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseConstantEnum(cs.getString(columnIndex));
    }

    public ConstantEnum parseConstantEnum(String name) {
        return ConstantEnum.valueOf(name);
    }
}
