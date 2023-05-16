package wang.liangchen.matrix.framework.data.mybatis.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.data.context.ExtendedColumnsContext;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.data.dao.criteria.Criteria;
import wang.liangchen.matrix.framework.data.dao.entity.ExtendedColumn;
import wang.liangchen.matrix.framework.data.dao.entity.ExtendedColumnValueDetail;
import wang.liangchen.matrix.framework.data.dao.entity.ExtendedColumnValues;
import wang.liangchen.matrix.framework.springboot.context.BeanLoader;
import wang.liangchen.matrix.framework.springboot.jackson.DefaultObjectMapper;

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
public class ExtendedColumnTypeHandler extends BaseTypeHandler<Object> {
    private final static StandaloneDao standaloneDao = BeanLoader.INSTANCE.getBean(StandaloneDao.class);
    private final Class<?> resultClass;
    private final Type resultType;
    private final Class<?>[] actualClasses;


    public ExtendedColumnTypeHandler(Class<?> resultClass) {
        this.resultClass = resultClass;
        this.resultType = null;
        this.actualClasses = null;
    }

    public ExtendedColumnTypeHandler(Class<?> resultClass, Type resultType) {
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
            String jsonString = DefaultObjectMapper.INSTANCE.objectMapper().writeValueAsString(parameter);
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
        List<ExtendedColumn> extendedColumns = standaloneDao.list(Criteria.of(ExtendedColumn.class)
                ._equals(ExtendedColumn::getColumnGroup, columnGroup)
                ._equals(ExtendedColumn::getTableName, tableName));
        if (CollectionUtil.INSTANCE.isEmpty(extendedColumns)) {
            return new ExtendedColumnValues<ExtendedColumnValueDetail>();
        }
        try {
            JavaType javaType = DefaultObjectMapper.INSTANCE.typeFactory().constructParametricType(resultClass, ExtendedColumnValueDetail.class);
            ExtendedColumnValues<ExtendedColumnValueDetail> extendedColumnValues = DefaultObjectMapper.INSTANCE.objectMapper().readValue(jsonString, javaType);
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
