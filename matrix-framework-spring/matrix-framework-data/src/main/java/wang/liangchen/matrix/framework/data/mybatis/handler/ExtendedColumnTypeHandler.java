package wang.liangchen.matrix.framework.data.mybatis.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import wang.liangchen.matrix.framework.commons.jackson.DefaultObjectMapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * @author Liangchen.Wang 2022-09-01 6:51
 */
public class ExtendedColumnTypeHandler extends AbstractObjectTypeHandler {
    // private final static StandaloneDao standaloneDao = BeanLoader.INSTANCE.getBean(StandaloneDao.class);

    public ExtendedColumnTypeHandler(Class<?> resultClass) {
        super(resultClass);
    }

    public ExtendedColumnTypeHandler(Class<?> resultClass, Type resultType) {
        super(resultClass, resultType);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        String jsonString = DefaultObjectMapper.INSTANCE.writeValueAsString(parameter);
        ps.setObject(i, jsonString);
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return jsonString2Object(rs.getString(columnName));
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return jsonString2Object(rs.getString(columnIndex));
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return jsonString2Object(cs.getString(columnIndex));
    }

    private Object jsonString2Object(String jsonString) throws SQLException {
        return null;
    }
}
