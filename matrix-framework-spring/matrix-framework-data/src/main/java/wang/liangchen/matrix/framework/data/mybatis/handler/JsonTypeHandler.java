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

/**
 * @author Liangchen.Wang 2022-09-01 6:51
 * 这里没有指定类型，不能自动匹配，需要在SQL配置中指定
 */
public class JsonTypeHandler extends AbstractObjectTypeHandler {

    public JsonTypeHandler(Class<?> resultClass) {
        super(resultClass);
    }

    public JsonTypeHandler(Class<?> resultClass, Type resultType) {
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
        Class<?>[] actualClasses = this.getActualClasses();
        Class<?> resultClass = this.getResultClass();
        if (null == actualClasses) {
            return DefaultObjectMapper.INSTANCE.readValue(jsonString, resultClass);
        }
        JavaType javaType = DefaultObjectMapper.INSTANCE.typeFactory().constructParametricType(resultClass, actualClasses);
        return DefaultObjectMapper.INSTANCE.readValue(jsonString, javaType);
    }
}
