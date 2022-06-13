package wang.liangchen.matrix.framework.data.util;

import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;

import java.math.BigDecimal;
import java.sql.SQLXML;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LiangChen.Wang
 */
public enum DatabaseUtil {
    /**
     *
     */
    INSTANCE;
    private static final Map<String, Class<?>> mapping = new HashMap<String, Class<?>>() {{
        // MySQL
        put("bit", Boolean.class);
        put("varchar", String.class);
        put("char", String.class);
        put("longvarchar", String.class);
        put("text", String.class);
        put("mediumtext", String.class);
        put("tinyint", Byte.class);
        put("smallint", Short.class);
        put("mediumint", Integer.class);
        put("int", Integer.class);
        put("integer", Integer.class);
        put("bigint", Long.class);
        put("", BigDecimal.class);
        put("float", Float.class);
        put("real", Float.class);
        put("double", Double.class);
        put("numeric", BigDecimal.class);
        put("decimal", BigDecimal.class);
        put("date", LocalDate.class);
        put("time", LocalTime.class);
        put("datetime", LocalDateTime.class);
        put("timestamp", Timestamp.class);

        // PostgreSQL
        put("bool", Boolean.class);
        put("varchar", String.class);
        put("text", String.class);
        put("char", String.class);
        put("bpchar", String.class);
        put("name", String.class);
        put("text", String.class);
        put("int2", Short.class);
        put("smallserial", Short.class);
        put("int", Integer.class);
        put("int4", Integer.class);
        put("serial", Integer.class);
        put("int8", Long.class);
        put("bigserial", Long.class);
        put("oid", Long.class);
        put("numeric", BigDecimal.class);
        put("float4", Float.class);
        put("float8", Double.class);
        put("money", Double.class);
        put("date", LocalDateTime.class);
        put("time", LocalTime.class);
        put("timetz", LocalTime.class);
        put("timestamp", Timestamp.class);
        put("timestamptz", Timestamp.class);
        put("xml", SQLXML.class);
        put("bytea", ByteArray.class);
        put("blob", ByteArray.class);
        put("longblob", ByteArray.class);
        put("mediumblob", ByteArray.class);
        put("tinyblob", ByteArray.class);
        put("clob", ByteArray.class);
        put("varbinary", ByteArray.class);
        put("binary", ByteArray.class);
        put("longvarbinary", ByteArray.class);
    }};

    public Class<?> dataType2JavaType(String dataType) {
        Class<?> javaType = mapping.get(dataType);
        if (null != javaType) {
            return javaType;
        }
        throw new MatrixInfoException("can't mapping dataType:{}", dataType);
    }

    class ByteArray {
        public String getSimpleName() {
            return "byte[]";
        }
    }

}
