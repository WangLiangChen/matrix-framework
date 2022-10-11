package wang.liangchen.matrix.framework.data.dao.criteria;


import wang.liangchen.matrix.framework.commons.enumeration.ConstantEnum;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.data.annotation.IdStrategy;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.SQLXML;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Liangchen.Wang 2021-10-22 9:37
 */
public class ColumnMeta {
    private static final String NON_IMPORT = "java.lang";
    private static final String BYTE_ARRAY = "byte[]";
    private static final Map<String, Class<?>> mapping = new HashMap<>();

    private final String columnName;
    private final String columnComment;
    private final String dataTypeName;
    private final String jdbcTypeName;
    private final boolean id;
    private final IdStrategy.Strategy idStrategy;
    private final boolean unique;
    private final boolean version;
    private final boolean json;
    private final boolean state;
    private final boolean stateUseConstantEnum;
    private final String markDeleteValue;
    private final String fieldName;
    private final Class<?> fieldClass;
    private final Type fieldType;
    private final String importPackage;
    private final String modifier;

    static {
        populateMySQL();
        populatePostgreSQL();
    }

    private ColumnMeta(String fieldName, Class<?> fieldClass, Type fieldType,
                       String columnName, boolean isId, IdStrategy.Strategy idStrategy, boolean isUnique, boolean isVersion, boolean isJson, boolean isState, String markDeleteValue) {
        this.fieldName = fieldName;
        this.fieldClass = fieldClass;
        this.fieldType = fieldType;
        this.columnName = columnName;
        this.columnComment = null;
        this.id = isId;
        this.idStrategy = idStrategy;
        this.unique = isUnique;
        this.version = isVersion;
        this.json = isJson;
        this.state = isState;
        if (fieldClass.isAssignableFrom(ConstantEnum.class)) {
            this.stateUseConstantEnum = true;
        } else {
            this.stateUseConstantEnum = false;
        }
        this.markDeleteValue = markDeleteValue;

        this.dataTypeName = null;
        this.jdbcTypeName = null;
        this.importPackage = null;
        this.modifier = null;
    }

    public static ColumnMeta newInstance(String fieldName, Class<?> fieldClass, Type fieldType,
                                         String columnName, boolean isId, IdStrategy.Strategy idStrategy, boolean isUnique, boolean isVersion, boolean isJson, boolean isState, String markDeleteValue) {
        return new ColumnMeta(fieldName, fieldClass, fieldType, columnName, isId, idStrategy, isUnique, isVersion, isJson, isState, markDeleteValue);
    }

    private ColumnMeta(String columnName, String dataTypeName, String jdbcTypeName,
                       boolean isId, boolean isUnique, boolean isVersion, boolean isJson, boolean isState, boolean isStateUseConstantEnum, String markDeleteValue, boolean underline2camelCase, String columnComment) {
        this.columnName = columnName;
        this.columnComment = columnComment;
        this.dataTypeName = dataTypeName;
        this.jdbcTypeName = jdbcTypeName;
        this.id = isId;
        this.idStrategy = null;
        this.unique = isUnique;
        this.version = isVersion;
        this.json = isJson;
        this.state = isState;
        this.stateUseConstantEnum = isStateUseConstantEnum;
        this.markDeleteValue = markDeleteValue;

        if (underline2camelCase) {
            this.fieldName = StringUtil.INSTANCE.underline2lowerCamelCase(columnName);
        } else {
            this.fieldName = columnName;
        }
        this.fieldClass = dataType2JavaType(dataTypeName);
        this.fieldType = this.fieldClass;
        if (isState && isStateUseConstantEnum) {
            this.modifier = "ConstantEnum";
        } else if (ByteArray.class.isAssignableFrom(this.fieldClass)) {
            this.modifier = BYTE_ARRAY;
        } else {
            this.modifier = fieldClass.getSimpleName();
        }
        String packageName = fieldClass.getPackage().getName();
        if (NON_IMPORT.equals(packageName)) {
            importPackage = "";
        } else {
            importPackage = fieldClass.getName();
        }
    }

    public static ColumnMeta newInstance(String columnName, String dataTypeName, String jdbcTypeName,
                                         boolean isId, boolean isUnique, boolean isVersion, boolean isJson, boolean isState, boolean isStateUseConstantEnum, String markDeleteValue, boolean underline2camelCase, String columnComment) {
        return new ColumnMeta(columnName, dataTypeName, jdbcTypeName, isId, isUnique, isVersion, isJson, isState, isStateUseConstantEnum, markDeleteValue, underline2camelCase, columnComment);
    }


    public String getColumnName() {
        return columnName;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public String getDataTypeName() {
        return dataTypeName;
    }

    public String getJdbcTypeName() {
        return jdbcTypeName;
    }

    public boolean isId() {
        return id;
    }

    public boolean isNotId() {
        return !id;
    }

    public IdStrategy.Strategy getIdStrategy() {
        return idStrategy;
    }

    public boolean isUnique() {
        return unique;
    }

    public boolean isJson() {
        return json;
    }

    public boolean isVersion() {
        return version;
    }

    public String getMarkDeleteValue() {
        return markDeleteValue;
    }

    public String getImportPackage() {
        return importPackage;
    }

    public String getModifier() {
        return modifier;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getFieldClass() {
        return fieldClass;
    }

    public Type getFieldType() {
        return fieldType;
    }

    public boolean isState() {
        return state;
    }

    public boolean isStateUseConstantEnum() {
        return stateUseConstantEnum;
    }

    private Class<?> dataType2JavaType(String dataTypeName) {
        Class<?> javaType = mapping.get(dataTypeName.toLowerCase());
        if (null != javaType) {
            return javaType;
        }
        throw new MatrixWarnException("can't mapping dataType to javaType:{}", dataTypeName);
    }

    private static void populateMySQL() {
        mapping.put("bit", Boolean.class);
        mapping.put("varchar", String.class);
        mapping.put("char", String.class);
        mapping.put("longvarchar", String.class);
        mapping.put("text", String.class);
        mapping.put("mediumtext", String.class);
        mapping.put("tinyint", Byte.class);
        mapping.put("smallint", Short.class);
        mapping.put("mediumint", Integer.class);
        mapping.put("int", Integer.class);
        mapping.put("integer", Integer.class);
        mapping.put("bigint", Long.class);
        mapping.put("", BigDecimal.class);
        mapping.put("float", Float.class);
        mapping.put("real", Float.class);
        mapping.put("double", Double.class);
        mapping.put("numeric", BigDecimal.class);
        mapping.put("decimal", BigDecimal.class);
        mapping.put("date", LocalDate.class);
        mapping.put("tim1e", LocalTime.class);
        mapping.put("datetime", LocalDateTime.class);
        mapping.put("timestamp", Timestamp.class);
    }

    private static void populatePostgreSQL() {
        mapping.put("bool", Boolean.class);
        mapping.put("varchar", String.class);
        mapping.put("text", String.class);
        mapping.put("char", String.class);
        mapping.put("bpchar", String.class);
        mapping.put("name", String.class);
        mapping.put("text", String.class);
        mapping.put("int2", Short.class);
        mapping.put("smallserial", Short.class);
        mapping.put("int", Integer.class);
        mapping.put("int4", Integer.class);
        mapping.put("serial", Integer.class);
        mapping.put("int8", Long.class);
        mapping.put("bigserial", Long.class);
        mapping.put("oid", Long.class);
        mapping.put("numeric", BigDecimal.class);
        mapping.put("float4", Float.class);
        mapping.put("float8", Double.class);
        mapping.put("money", Double.class);
        mapping.put("date", LocalDate.class);
        mapping.put("time", LocalTime.class);
        mapping.put("timetz", LocalTime.class);
        mapping.put("timestamp", LocalDateTime.class);
        mapping.put("timestamptz", Timestamp.class);
        mapping.put("xml", SQLXML.class);
        mapping.put("bytea", ByteArray.class);
        mapping.put("blob", ByteArray.class);
        mapping.put("longblob", ByteArray.class);
        mapping.put("mediumblob", ByteArray.class);
        mapping.put("tinyblob", ByteArray.class);
        mapping.put("clob", ByteArray.class);
        mapping.put("varbinary", ByteArray.class);
        mapping.put("binary", ByteArray.class);
        mapping.put("longvarbinary", ByteArray.class);
    }

    interface ByteArray {
    }
}
