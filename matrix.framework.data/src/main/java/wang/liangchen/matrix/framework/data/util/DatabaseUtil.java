package wang.liangchen.matrix.framework.data.util;

/**
 * @author LiangChen.Wang
 */
public enum DatabaseUtil {
    /**
     *
     */
    INSTANCE;

    public String jdbcType2JavaType(String jdbcType) {
        jdbcType = jdbcType.toLowerCase();
        String javaType = null;
        switch (jdbcType) {
            case "varchar":
            case "char":
            case "longvarchar":
            case "text":
            case "mediumtext":
                javaType = "java.lang.String";
                break;
            case "blob":
            case "longblob":
            case "mediumblob":
            case "tinyblob":
            case "clob":
            case "varbinary":
            case "binary":
            case "longvarbinary":
                javaType = "byte[]";
                break;
            case "bit":
                javaType = "java.lang.Boolean";
                break;
            case "bigint":
                javaType = "java.lang.Long";
                break;
            case "tinyint":
                javaType = "java.lang.Byte";
                break;
            case "smallint":
                javaType = "java.lang.Short";
                break;
            case "mediumint":
            case "int":
            case "integer":
                javaType = "java.lang.Integer";
                break;
            case "float":
            case "real":
                javaType = "java.lang.Float";
                break;
            case "double":
                javaType = "java.lang.Double";
                break;
            case "numeric":
            case "decimal":
                javaType = "java.math.BigDecimal";
                break;
            case "date":
                javaType = "java.time.LocalDate";
                break;
            case "time":
                javaType = "java.time.LocalTime";
                break;
            case "datetime":
            case "timestamp":
                javaType = "java.time.LocalDateTime";
                break;
            default:
                break;
        }
        return javaType;
    }
}
