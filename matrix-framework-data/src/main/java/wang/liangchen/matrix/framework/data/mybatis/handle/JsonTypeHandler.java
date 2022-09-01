package wang.liangchen.matrix.framework.data.mybatis.handle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Liangchen.Wang 2022-09-01 6:51
 */
public class JsonTypeHandler extends BaseTypeHandler<Object> {
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final static TypeFactory typeFactory = objectMapper.getTypeFactory();

    private final Class<?> resultClass;
    private final Type resultType;
    private final Class<?>[] actualClasses;

    public JsonTypeHandler(Class<?> resultClass) {
        this.resultClass = resultClass;
        this.resultType = null;
        this.actualClasses = null;
    }

    public JsonTypeHandler(Class<?> resultClass, Type resultType) {
        this.resultClass = resultClass;
        this.resultType = resultType;
        if (!(resultType instanceof ParameterizedType)) {
            this.actualClasses = null;
            return;
        }
        ParameterizedType parameterizedType = (ParameterizedType) resultType;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        this.actualClasses = new Class<?>[actualTypeArguments.length];
        for (int i = 0; i < actualTypeArguments.length; i++) {
            this.actualClasses[i] = (Class<?>) actualTypeArguments[i];
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        try {
            String jsonString = objectMapper.writeValueAsString(parameter);
            ps.setObject(i, jsonString);
        } catch (JsonProcessingException e) {
            throw new SQLException(e);
        }
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
        try {
            if (null == this.actualClasses) {
                return objectMapper.readValue(jsonString, resultClass);
            }
            JavaType javaType = typeFactory.constructParametricType(resultClass, this.actualClasses);
            return objectMapper.readValue(jsonString, javaType);
        } catch (JsonProcessingException e) {
            throw new SQLException(e);
        }
    }
}
