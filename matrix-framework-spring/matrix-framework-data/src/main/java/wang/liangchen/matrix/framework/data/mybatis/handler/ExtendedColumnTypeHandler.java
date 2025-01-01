package wang.liangchen.matrix.framework.data.mybatis.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import org.apache.ibatis.type.JdbcType;
import wang.liangchen.matrix.framework.commons.CollectionUtil;
import wang.liangchen.matrix.framework.commons.jackson.JacksonUtil;
import wang.liangchen.matrix.framework.data.context.ExtendedColumnsContext;
import wang.liangchen.matrix.framework.data.criteria.Criteria;
import wang.liangchen.matrix.framework.data.entity.ExtendedColumn;
import wang.liangchen.matrix.framework.data.entity.ExtendedColumnValueDetail;
import wang.liangchen.matrix.framework.data.entity.ExtendedColumnValues;
import wang.liangchen.matrix.framework.data.repository.StandaloneRepository;
import wang.liangchen.matrix.framework.springboot.context.BeanContext;

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

    private Object jsonString2Object(String jsonString) throws SQLException {
        // 查询配置的扩展字段
        String columnGroup = ExtendedColumnsContext.INSTANCE.getColumnGroup();
        String tableName = ExtendedColumnsContext.INSTANCE.getTableName();
        List<ExtendedColumn> extendedColumns = standaloneRepository.list(Criteria.of(ExtendedColumn.class)
                ._equals(ExtendedColumn::getColumnGroup, columnGroup)
                ._equals(ExtendedColumn::getTableName, tableName));
        if (CollectionUtil.INSTANCE.isEmpty(extendedColumns)) {
            return new ExtendedColumnValues<ExtendedColumnValueDetail>();
        }
        try {
            JavaType javaType = JacksonUtil.INSTANCE.typeFactory().constructParametricType(this.getResultClass(), ExtendedColumnValueDetail.class);
            ExtendedColumnValues<ExtendedColumnValueDetail> extendedColumnValues = JacksonUtil.INSTANCE.objectMapper().readValue(jsonString, javaType);
            // 补充字段值
            Iterator<ExtendedColumn> iterator = extendedColumns.iterator();
            while (iterator.hasNext()) {
                ExtendedColumn extendedColumn = iterator.next();
                extendedColumnValues.forEach(detail -> {
                    if (detail.getColumnName().equals(extendedColumn.getColumnName())) {
                        detail.setColumnComment(extendedColumn.getColumnComment());
                        detail.setDataType(extendedColumn.getDataType());
                        iterator.remove();
                    }
                });
            }
            // 增加其余的字段
            extendedColumns.forEach(extendedColumn -> {
                ExtendedColumnValueDetail detail = new ExtendedColumnValueDetail();
                detail.setColumnName(extendedColumn.getColumnName());
                detail.setColumnComment(extendedColumn.getColumnComment());
                detail.setDataType(extendedColumn.getDataType());
                extendedColumnValues.add(detail);
            });
            return extendedColumnValues;
        } catch (JsonProcessingException e) {
            throw new SQLException(e);
        }
    }
}
