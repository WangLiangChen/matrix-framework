package wang.liangchen.matrix.framework.data.mybatis.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import org.apache.ibatis.type.JdbcType;
import wang.liangchen.matrix.framework.commons.CollectionUtil;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.jackson.JacksonUtil;
import wang.liangchen.matrix.framework.data.context.DataSourceContext;
import wang.liangchen.matrix.framework.data.criteria.Criteria;
import wang.liangchen.matrix.framework.data.entity.ExtendedColumnDefinition;
import wang.liangchen.matrix.framework.data.entity.ExtendedColumnValue;
import wang.liangchen.matrix.framework.data.entity.ExtendedColumnValueDetail;
import wang.liangchen.matrix.framework.data.entity.ExtendedColumnValues;
import wang.liangchen.matrix.framework.data.mybatis.MyBatisExecutorContext;
import wang.liangchen.matrix.framework.data.repository.StandaloneRepository;
import wang.liangchen.matrix.framework.springboot.context.BeanContext;

import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Liangchen.Wang 2022-09-01 6:51
 */
public class ExtendedColumnTypeHandler extends AbstractObjectTypeHandler {
    private final static StandaloneRepository standaloneRepository = BeanContext.INSTANCE.getBean(StandaloneRepository.class);

    public ExtendedColumnTypeHandler(Class<?> resultClass) {
        super(resultClass);
    }

    public ExtendedColumnTypeHandler(Class<?> resultClass, Type resultType) {
        super(resultClass, resultType);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        try {
            String jsonString = JacksonUtil.INSTANCE.objectMapper().writeValueAsString(parameter);
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

    private Object jsonString2Object(String jsonString) {
        // find table name from context
        String tableName = MyBatisExecutorContext.INSTANCE.getTableName();
        // find column definition by table name
        List<ExtendedColumnDefinition> extendedColumnDefinitions = DataSourceContext.INSTANCE.executeWithPrimaryDataSource(() -> standaloneRepository.list(Criteria.of(ExtendedColumnDefinition.class)
                ._equals(ExtendedColumnDefinition::getTableName, tableName)));
        if (CollectionUtil.INSTANCE.isEmpty(extendedColumnDefinitions)) {
            return new ExtendedColumnValues<ExtendedColumnValueDetail>();
        }
        try {
            // find column name and column value from json string
            JavaType javaType = JacksonUtil.INSTANCE.typeFactory().constructParametricType(this.getResultClass(), ExtendedColumnValue.class);
            ExtendedColumnValues<ExtendedColumnValue> extendedColumnValues = JacksonUtil.INSTANCE.objectMapper().readValue(jsonString, javaType);
            ExtendedColumnValues<ExtendedColumnValueDetail> extendedColumnValuesDetails = new ExtendedColumnValues<>();
            // Complete column definition
            extendedColumnDefinitions.forEach(extendedColumnDefinition -> {
                ExtendedColumnValueDetail detail = new ExtendedColumnValueDetail();
                detail.setColumnName(extendedColumnDefinition.getColumnName());
                detail.setDataType(extendedColumnDefinition.getDataType());
                detail.setColumnComment(extendedColumnDefinition.getColumnComment());
                // find value from extendedColumnValues
                extendedColumnValues.forEach(extendedColumnValue -> {
                    if (extendedColumnValue.getColumnName().equals(extendedColumnDefinition.getColumnName())) {
                        detail.setColumnValue(extendedColumnValue.getColumnValue());
                    }
                });
                extendedColumnValuesDetails.add(detail);
            });
            return extendedColumnValuesDetails;
        } catch (JsonProcessingException e) {
            throw new MatrixErrorException(e);
        }
    }
}
